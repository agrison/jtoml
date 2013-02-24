package me.grison.jtoml;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Toml parsing class front-end.
 * 
 * <code>
 *     Toml toml = new Toml();
 *     toml.parse("pi = 3.14");
 *     toml.getFloat("pi");
 * </code>
 *
 * @author $Author: alexandre grison$
*/
public class Toml implements Parser, Getter {
    Map<String, Object> context = new LinkedHashMap<String, Object>();
    final Matcher keyPathMatcher = Pattern.compile("((\\w+[.])+).*").matcher("");

    @Override
    public void parseString(String string) {
        context = TomlParser.parse(string);
        System.out.println(context);
    }

    private String keyPath(String s) {
        if (keyPathMatcher.reset(s).matches()) {
            return keyPathMatcher.group(1).substring(0, keyPathMatcher.group(1).length() - 1);
        } else {
            return s;
        }
    }

    @Override
    public Object get(String key) {
        if (key.contains(".")) {
            String keyPath = keyPath(key);
            return TomlParser.findContext(context, keyPath).get(key.replace(keyPath + ".", ""));
        } else {
            return context.get(key);
        }
    }

    @Override
    public String getString(String key) {
        Object value = get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type String" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }

    @Override
    public Integer getInt(String key) {
        Object value = get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type Integer" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }

    @Override
    public Float getFloat(String key) {
        Object value = get(key);
        if (value instanceof Float) {
            return (Float) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type Float" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }

    @Override
    public Calendar getDate(String key) {
        Object value = get(key);
        if (value instanceof Calendar) {
            return (Calendar) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type Calendar" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }

    @Override
    public List<Object> getList(String key) {
        Object value = get(key);
        if (value instanceof List) {
            return (List) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type List" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }

    @Override
    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalArgumentException("Value for key `" + key + "` is not of type Boolean" + //
                    (value == null ? "" : " but of type " + value.getClass().getName()));
        }
    }
}
