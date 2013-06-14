package me.grison.jtoml.impl;

import me.grison.jtoml.TomlSerializer;
import me.grison.jtoml.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Builtin Toml TomlSerializer.
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class SimpleTomlSerializer implements TomlSerializer {
    interface Converter {
        public String convert(Object o);
    }
    // Special conversions
    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>() {{
        put(String.class, new Converter() {@Override public String convert(Object o) { return "\"" + Util.TomlString.escape((String)o) + "\""; }});
        put(Calendar.class, new Converter() {@Override public String convert(Object o) { return Util.ISO8601.fromCalendar((Calendar)o); }});
        put(char[].class, new Converter() {@Override public String convert(Object o) { return get(String.class).convert(String.valueOf((char[])o)); }});
    }};

    @Override
    public String serialize(Object object) {
        return serialize(null, object);
    }

    private String serializeList(List<?> list) {
        StringBuffer buffer = new StringBuffer("[");
        final StringBuffer ibuff = new StringBuffer();
        for (Object item: (List<?>)list) {
            if (item instanceof List) {
                ibuff.append(", ").append(serializeList((List<?>) item));
            } else if (Util.Reflection.isTomlSupportedType(item.getClass())) {
                if (converters.containsKey(item.getClass())) {
                    ibuff.append(", ").append(converters.get(item.getClass()).convert(item));
                } else {
                    ibuff.append(", ").append(item);
                }
            }
        }
        if (ibuff.length() > 0) {
            buffer.append(ibuff.substring(2));
        }
        buffer.append("]");
        return buffer.toString();
    }

    private String serializeMap(String rootKey, Map<String, Object> map) {
        final StringBuffer buffer = new StringBuffer(rootKey == null ? "" : "[" + rootKey + "]\n");
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            String name = entry.getKey();
            String nextRootKey = rootKey == null ? name : rootKey + "." + name;
            Class<?> type = entry.getValue().getClass();
            Object value = entry.getValue();
            if (value instanceof List) {
                buffer.append(name + " = " + serializeList((List<?>)value) + "\n");
            } else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
                if (converters.containsKey(type)) {
                    value = converters.get(type).convert(value);
                }
                buffer.append(name + " = " + value + "\n");
            } else if (value instanceof Map) {
                buffer.append("\n" + serializeMap(nextRootKey, (Map<String, Object>)value));
            }
            else {
                buffer.append("\n" + serialize(nextRootKey, value));
            }
        }
        return buffer.toString();
    }

    @Override
    public String serialize(String rootKey, Object object) {
        try {
            if (object instanceof Map) {
                return serializeMap(rootKey, (Map<String, Object>) object);
            }
            final StringBuffer buffer = new StringBuffer(rootKey == null ? "" : "[" + rootKey + "]\n");
            final List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
            Collections.sort(fields, Util.Reflection.newTomlFieldComparator(fields));
            for (Field f : fields) {
                Class<?> type = f.getType();
                Object value = Util.Reflection.getFieldValue(f, object);
                if (type.equals(List.class)) {
                    buffer.append(f.getName() + " = " + serializeList((List<?>)value) + "\n");
                } else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
                    if (converters.containsKey(type)) {
                        value = converters.get(type).convert(value);
                    }
                    buffer.append(f.getName() + " = " + value + "\n");
                } else {
                    buffer.append("\n" + serialize(rootKey + "." + f.getName(), value));
                }
            }
            return buffer.toString();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not serialize object with rootKey `" + rootKey + "`.", e);
        }
    }
}
