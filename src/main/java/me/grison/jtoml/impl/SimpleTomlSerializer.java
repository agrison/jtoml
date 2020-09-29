package me.grison.jtoml.impl;

import me.grison.jtoml.TomlSerializer;
import me.grison.jtoml.Util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Builtin Toml TomlSerializer.
 *
 * @author Alexandre Grison
 */
@SuppressWarnings("unchecked")
public class SimpleTomlSerializer implements TomlSerializer {
    // Special conversions
    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>() {{
        put(String.class, o -> "\"" + Util.TomlString.escape((String) o) + "\"");
        put(Calendar.class, o -> Util.ISO8601.fromCalendar((Calendar) o));
        put(char[].class, o -> get(String.class).convert(String.valueOf((char[]) o)));
    }};

    @Override
    public String serialize(Object object) {
        return serialize(null, object);
    }

    private String serializeList(List<?> list) {
        StringBuilder buffer = new StringBuilder("[");
        final StringBuilder ibuff = new StringBuilder();
        for (Object item : list) {
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
        final StringBuilder buffer = new StringBuilder(rootKey == null ? "" : "[" + rootKey + "]\n");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String name = entry.getKey();
            String nextRootKey = rootKey == null ? name : rootKey + "." + name;
            Class<?> type = entry.getValue().getClass();
            Object value = entry.getValue();
            if (value instanceof List) {
                buffer.append(name).append(" = ").append(serializeList((List<?>) value)).append("\n");
            } else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
                if (converters.containsKey(type)) {
                    value = converters.get(type).convert(value);
                }
                buffer.append(name).append(" = ").append(value).append("\n");
            } else if (value instanceof Map) {
                buffer.append("\n").append(serializeMap(nextRootKey, (Map<String, Object>) value));
            } else {
                buffer.append("\n").append(serialize(nextRootKey, value));
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
            final StringBuilder buffer = new StringBuilder(rootKey == null ? "" : "[" + rootKey + "]\n");
            final List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
            fields.sort(Util.Reflection.newTomlFieldComparator(fields));
            for (Field f : fields) {
                Class<?> type = f.getType();
                Object value = Util.Reflection.getFieldValue(f, object);
                if (type.equals(List.class)) {
                    buffer.append(f.getName()).append(" = ").append(serializeList((List<?>) value)).append("\n");
                } else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
                    if (converters.containsKey(type)) {
                        value = converters.get(type).convert(value);
                    }
                    buffer.append(f.getName()).append(" = ").append(value).append("\n");
                } else {
                    buffer.append("\n").append(serialize(rootKey + "." + f.getName(), value));
                }
            }
            return buffer.toString();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not serialize object with rootKey `" + rootKey + "`.", e);
        }
    }

    interface Converter {
        String convert(Object o);
    }
}
