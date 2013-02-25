package me.grison.jtoml.impl;

import me.grison.jtoml.TomlParser;
import me.grison.jtoml.Util;

import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Builtin Toml parser.
 *
 * <p>Uses a first pass to make multi-line arrays one-liner, then iterate line by line, matching against known regular expressions,
 * to extract content and store it into a context map.</p>
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class BuiltinTomlParser implements TomlParser {
    /** Encapsulate both a Matcher and a method to cast the retrieved value to the according type. */
    static abstract class Handler {
        final Matcher matcher;
        public Handler(String regex) { this.matcher = Pattern.compile(regex).matcher(""); }
        Matcher matcher() { return this.matcher; }
        abstract Object cast(String v);
    }
    private static final String SPACES = "\\s*";
    private static final String POSSIBLE_COMMENT = "(#.*)?";
    private static final String KEY_EQUALS = "(" + SPACES + "(\\w+)" + SPACES + "=" + SPACES + ")?";
    private static final String ARRAY = SPACES + "\\[" + SPACES + "(.*)" + SPACES + "\\]" + SPACES;
    private static final String DATE = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*)";
    private static final String DOUBLE = "([-+]?\\d*\\.\\d+([eE][-+]?\\d+)?)";
    private static final String DIGITS = "(\\d+)";
    private static final String STRING = "\"(.*)\"";
    private static final String BOOLEAN = "(true|false)";
    private static final Matcher ARRAY_LINE_MATCHER = Pattern.compile(KEY_EQUALS + ARRAY, Pattern.DOTALL).matcher("");
    private static final Matcher GROUP_MATCHER = Pattern.compile(SPACES + "\\[(.*)\\]" + SPACES).matcher("");
    private static final Matcher COMMENT = Pattern.compile("(,|\"|\\])\\s*(#.*)").matcher("");
    /** The list of handlers */
    private static final List<Handler> HANDLERS = new ArrayList<Handler>() {{
        // dates
        add(new Handler(KEY_EQUALS + DATE) {Object cast(String v) { try { return Util.ISO8601.toCalendar(v); } catch (Exception e) { return null; } }});
        // doubles
        add(new Handler(KEY_EQUALS + DOUBLE + SPACES + POSSIBLE_COMMENT) {Object cast(String v) {return Double.valueOf(v);}});
        // integers
        add(new Handler(KEY_EQUALS + DIGITS + SPACES + POSSIBLE_COMMENT) {Object cast(String v) {return Integer.valueOf(v);}});
        // strings
        add(new Handler(KEY_EQUALS + STRING + SPACES) {Object cast(String v) {return Util.TomlString.unescape(v.trim());}});
        // booleans
        add(new Handler(KEY_EQUALS + BOOLEAN + SPACES + POSSIBLE_COMMENT) {Object cast(String v) {return Boolean.parseBoolean(v);}});
    }};

    @Override
    public Map<String, Object> parse(String tomlString) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Map<String, Object> context = result;
        tomlString = prepareArrays(tomlString);
        // match lines
        Matcher lines = Pattern.compile("([^\n]+)\n?").matcher(tomlString);
        while (lines.find()) {
            String line = lines.group().trim();
            if (COMMENT.reset(line).find()) {
                line = line.replace(COMMENT.group(2), "");
            }
            if (GROUP_MATCHER.reset(line).matches()) {
                context = createContextIfNeeded(result, GROUP_MATCHER.group(1));
            }
            Object[] val = readObject(line);
            if (val != null)
                context.put((String)val[0], val[1]);
        }
        return result;
    }

    /**
     * Find every arrays in the given String and make them one liner.
     * prepareArrays('foo = [\n 1, 2, 3,\n 4,\n 5, #this is ok\n ]')
     * -> 'foo = [ 1, 2, 3, 4, 5 ]'
     * @return the given String with arrays on one line
     */
    private String prepareArrays(String s) {
        StringBuffer buffer = new StringBuffer();
        String currentLine = "";
        for (String l: s.split("\n")) {
            currentLine = currentLine + l;
            if (Util.TomlString.countOccurrences(currentLine, "[") == Util.TomlString.countOccurrences(currentLine, "]")) {
                if (l.equals(currentLine)) { // nothing done
                    buffer.append(currentLine);
                } else { // multiline -> single line
                    buffer.append(
                        currentLine.replaceAll("#[^],]+", "") // skip comments
                           .replaceAll("\\[\\s*", "[").replaceAll("\\s*\\]", "]") // remove spaces around brackets
                           .replaceAll(",\\s*", ",").replaceAll(",,", ",") // spaces and empty commas
                    );
                }
                buffer.append("\n");
                currentLine = "";
            }
        }
        return buffer.toString();
    }

    /**
     * Create the context if needed.
     * createContextIfNeeded({}, "foo.bar.bazz")
     * -> {"foo": {"bar": {"bazz": {}}}}
     *
     * @param context the context
     * @param key the key
     * @return the newly created level
     */
    private Map<String, Object> createContextIfNeeded(Map<String, Object> context, String key) {
        Map<String, Object> visitor = context;
        for (String part: key.split("[.]")) {
            if (!visitor.containsKey(part)) {
                visitor.put(part, new LinkedHashMap<String, Object>());
            }
            visitor = (Map<String, Object>)visitor.get(part);
        }
        return visitor;
    }

    /**
     * Read the given line and returns an array of Object like the following:
     * index 0: the key
     * index 1: the value
     *
     * @param line the line where to extract key/value
     */
    private Object[] readObject(String line) {
        for (Handler handler: HANDLERS) {
            if (handler.matcher().reset(line).matches()) {
                String key = handler.matcher().group(2);
                Object value = handler.cast(handler.matcher().group(3));
                return new Object[] { key, value };
            }
        }
        // it might be an array
        if (ARRAY_LINE_MATCHER.reset(line).matches()) {
            String key = ARRAY_LINE_MATCHER.group(2);
            String array = ARRAY_LINE_MATCHER.group(3);
            List<Object> values = new ArrayList<Object>();
            // find nested arrays
            if (array.matches(".*(?:\\]),.*")) {
                for (String nested: array.split("(?:\\]),")) {
                    nested += "]";
                    Object nestedArray[] = readObject(nested.trim());
                    if (nestedArray != null)
                        values.add(nestedArray[1]);
                }
            } else {
                for (String value : array.split(",")) {
                    value = value.trim();
                    if (value.endsWith("]"))
                        value = value.substring(0, value.length() - 1);
                    Object[] nested = readObject(value.trim());
                    if (nested != null)
                        values.add(nested[1]);
                }
            }
            return new Object[] { key, values };
        }
        return null;
    }
}
