package me.grison.jtoml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Toml parser.
 * 
 * <p>
 * Should maybe use an ANTLR grammar in next version instead of such a basic
 * parsing.
 * </p>
 * 
 * @author $Author: Alexandre Grison$
 */
public class TomlParser {
	/**
	 * Encapsulate both a Matcher and a method to cast the retrieved value to
	 * the according type.
	 */
	static abstract class Handler {
		final Matcher m;

		public Handler(String s) {
			this.m = Pattern.compile(s).matcher("");
		}

		Matcher matcher() {
			return this.m;
		}

		abstract Object cast(String v);
	}

	private static final String SPACES = "\\s*";
	private static final String ANY = ".*";
	private static final String KEY_EQUALS = "(" + SPACES + "(\\w+)" + SPACES
			+ "=" + SPACES + ")?";
	private static final String ARRAY = SPACES + "\\[" + SPACES + "(.*)"
			+ SPACES + "\\]" + SPACES;
	private static final Matcher ARRAY_MATCHER = Pattern.compile(ARRAY)
			.matcher("");
	private static final Matcher ARRAY_LINE_MATCHER = Pattern.compile(
			KEY_EQUALS + ARRAY).matcher("");
	private static final Matcher GROUP_MATCHER = Pattern.compile(
			SPACES + "\\[(.*)\\]" + SPACES).matcher("");
	private static final Matcher COMMENT = Pattern.compile("(\"|\\])\\s*(#.*)")
			.matcher("");
	private static final List<Handler> HANDLERS = new ArrayList<Handler>() {
		{
			// dates
			add(new Handler(KEY_EQUALS
					+ "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*)") {
				@Override
				Object cast(String v) {
					try {
						return Util.ISO8601.toCalendar(v);
					} catch (Exception e) {
						return null;
					}
				}
			});
			// integers
			add(new Handler(KEY_EQUALS + "(\\d+)" + SPACES + ANY) {
				@Override
				Object cast(String v) {
					return Integer.valueOf(v);
				}
			});
			// floats
			add(new Handler(KEY_EQUALS + "([-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?)"
					+ SPACES + ANY) {
				@Override
				Object cast(String v) {
					return Float.valueOf(v);
				}
			});
			// strings
			add(new Handler(KEY_EQUALS + "\"(.*)\"" + SPACES) {
				@Override
				Object cast(String v) {
					return Util.TomlString.unescape(v.trim());
				}
			});
			// booleans
			add(new Handler(KEY_EQUALS + "(true|false)" + SPACES + ANY) {
				@Override
				Object cast(String v) {
					return Boolean.parseBoolean(v);
				}
			});
		}
	};

	/**
	 * Parse the given String as TOML.
	 * 
	 * @param s
	 *            the string
	 * @return a Map representing the TOML structure.
	 */
	public static Map<String, Object> parse(String s) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		Map<String, Object> context = result;
		// match lines
		Matcher lines = Pattern.compile("([^\n]+)\n?").matcher(s);
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
				context.put((String) val[0], val[1]);
		}
		return result;
	}

	/**
	 * Find the correct level context. findContext({"foo": {"bar": "hello"}},
	 * "foo.bar") -> "hello"
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the context
	 */
	public static Map<String, Object> findContext(Map<String, Object> context,
			String key) {
		Map<String, Object> visitor = context;
		for (String part : key.split("[.]")) {
			if (!visitor.containsKey(part)) {
				return null;
			}
			visitor = (Map<String, Object>) visitor.get(part);
		}
		return visitor;
	}

	/**
	 * Create the context if needed. createContextIfNeeded({}, "foo.bar.bazz")
	 * -> {"foo": {"bar": {"bazz": {}}}}
	 * 
	 * @param context
	 *            the context
	 * @param key
	 *            the key
	 * @return the newly created level
	 */
	private static Map<String, Object> createContextIfNeeded(
			Map<String, Object> context, String key) {
		Map<String, Object> visitor = context;
		for (String part : key.split("[.]")) {
			if (!visitor.containsKey(part)) {
				visitor.put(part, new LinkedHashMap<String, Object>());
			}
			visitor = (Map<String, Object>) visitor.get(part);
		}
		return visitor;
	}

	/**
	 * Read the given line and returns an array of Object like the following:
	 * index 0: the key index 1: the value
	 * 
	 * @param line
	 *            the line where to extract key/value
	 */
	private static Object[] readObject(String line) {
		for (Handler handler : HANDLERS) {
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
				for (String nested : array.split("(?:\\]),")) {
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
