package me.grison.jtoml.impl;

import me.grison.jtoml.TomlSerializer;
import me.grison.jtoml.Util;

import java.lang.reflect.Field;
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
    }};

    @Override
    public String serialize(Object object) {
        return serialize(null, object);
    }

    @Override
    public String serialize(String rootKey, Object object) {
        try {
            StringBuffer buffer = new StringBuffer(rootKey == null ? "" : "[" + rootKey + "]\n");
            List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
            Collections.sort(fields, Util.Reflection.newTomlFieldComparator(fields));
            for (Field f : fields) {
                Class<?> type = f.getType();
                Object value = Util.Reflection.getFieldValue(f, object);
                if (Util.Reflection.isTomlSupportedType(type)) {
                    if (converters.containsKey(type)) {
                        value = converters.get(type).convert(value);
                    }
                    buffer.append(f.getName() + " = " + value + "\n");
                } else {
                    rootKey = rootKey + "." + f.getName();
                    buffer.append("\n" + serialize(rootKey, value));
                }
            }
            return buffer.toString();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not serialize object with rootKey `" + rootKey + "`.", e);
        }
    }
}
