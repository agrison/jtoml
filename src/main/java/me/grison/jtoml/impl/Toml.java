package me.grison.jtoml.impl;

import me.grison.jtoml.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Toml parsing class front-end.
 * 
 * <code>
 *     Toml toml = Toml.parse("pi = 3.141592653589793");
 *     Double pi = toml.getDouble("pi");
 * </code>
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
*/
public class Toml implements Parser, Getter {
    private static final Logger LOGGER = Logger.getLogger(Toml.class.getName());
    /** The default {@link TomlParser} loaded from {@link ServiceLoader}. Defaults to {@link SimpleTomlParser} if none found*/
    private static TomlParser defaultParser;
    /** The instance context map holding key/values parsed from a TOML String or File */
    protected Map<String, Object> context = new LinkedHashMap<String, Object>();
    /** A matcher to retrieve the path to a key. */
    protected final Matcher keyPathMatcher = Pattern.compile("((\\w+[.])+).*").matcher("");
    /** Current instance parser: default to `Toml.defaultParser` if none specified */
    protected TomlParser tomlParser;

    /**
     * Retrieve a TomlParser on classpath.
     */
    static {
        initDefaultParser();
    }

    /**
     * Default constructor.
     */
    public Toml() {
        this(null);
    }

    /**
     * Constructor using a specific TOML parser.
     *
     * @param tomlParser the specific TOML parser to be used.
     */
    public Toml(TomlParser tomlParser) {
        this.tomlParser = tomlParser;
    }

    /**
     * Creates a TOML instance loaded with the given String.
     *
     * @param tomlString the TOML String to load.
     * @return a TOM object instance
     */
    public static Toml parse(String tomlString) {
        return parse(tomlString, null);
    }

    /**
     * Creates a TOML instance loaded with the given file and using the given TOML parser.
     *
     * @param tomlString the TOML String to load.
     * @param tomlParser the TOML parser to use
     * @return a TOML object instance
     * @throws IOException
     */
    public static Toml parse(String tomlString, TomlParser tomlParser) {
        return new Toml(tomlParser).parseString(tomlString);
    }

    /**
     * Creates a TOML instance loaded with the given file.
     *
     * @param file the TOML file to load
     * @return a TOML object instance
     * @throws IOException
     */
    public static Toml parse(File file) throws IOException {
        return parse(file, null);
    }

    /**
     * Creates a TOML instance loaded with the given file and using the given TOML parser.
     *
     * @param file the TOML file to load
     * @param tomlParser the TOML parser to use
     * @return a TOML object instance
     * @throws IOException
     */
    public static Toml parse(File file, TomlParser tomlParser) throws IOException {
        return new Toml(tomlParser).parseFile(file);
    }

    @Override
    public Toml parseString(String string) {
        context = internalParser().parse(string);
        return this;
    }

	@Override
	public Toml parseFile(File file) throws FileNotFoundException {
        return parseString(Util.FileToString.read(file));
	}

    /**
     * Gets the parser currently used to parse TOML.
     * Fallback to a new instance of {@link SimpleTomlParser} if not defined.
     *
     * @return the current TOML parser.
     */
    private TomlParser internalParser() {
        // fallback
        if (this.tomlParser == null) {
            this.tomlParser = defaultParser;
        }
        return this.tomlParser;
    }

    /**
     * Gets the parser currently used to parse TOML.
     * @return the current TOML parser.
     */
    public TomlParser getTomlParser() {
        return this.tomlParser;
    }

    /**
     * Get the path to a key.
     * A key can be anything like <code>(\w[.])*\w</code>
     *
     * <p><code>keyPath("foo") -> "foo"</code></p>
     * <p><code>keyPath("foo.bar") -> "foo"</code></p>
     * <p><code>keyPath("foo.bar.bazz") -> "foo.bar"</code></p>
     *
     * @param key the key
     * @return the path leading to the key.
     */
    private String keyPath(String key) {
        if (keyPathMatcher.reset(key).matches()) {
            return keyPathMatcher.group(1).substring(0, keyPathMatcher.group(1).length() - 1);
        } else {
            return key;
        }
    }

    /**
     * Find the correct level context.
     * findContext({"foo": {"bar": "hello"}}, "foo.bar")
     * -> "hello"
     *
     * @param context the context
     * @param key the key
     * @return the context
     */
    public Map<String, Object> findContext(Map<String, Object> context, String key) {
        Map<String, Object> visitor = context;
        for (String part: key.split("[.]")) {
            if (!visitor.containsKey(part)) {
                return null;
            }
            visitor = (Map<String, Object>)visitor.get(part);
        }
        return visitor;
    }

    @Override
    public Object get(String key) {
        if (key.contains(".")) {
            String keyPath = keyPath(key);
            return findContext(context, keyPath).get(key.replace(keyPath + ".", ""));
        } else {
            return context.get(key);
        }
    }

    @Override
    public String getString(String key) {
        return get(key, String.class);
    }

    @Override
    public Long getLong(String key) {
        return get(key, Long.class);
    }

    @Override
    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    @Override
    public Calendar getDate(String key) {
        return get(key, Calendar.class);
    }

    @Override
    public List<Object> getList(String key) {
        return get(key, List.class);
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return get(key, Map.class);
    }

    @Override
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    /**
     * Get a new instance of the given Class filled with the value that can be found
     * in the current context at the given key.
     *
     * @param key the key where the value is located
     * @param clazz the class of the resulting object
     * @param <T> the resulting object type
     * @return the value whose key is the given parameter
     */
    public <T> T getAs(String key, Class<T> clazz) {
        try {
            T result = clazz.newInstance();
            for (Field f: clazz.getDeclaredFields()) {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);
                f.set(result, get(key + "." + f.getName(), f.getType()));
                f.setAccessible(isAccessible);
            }
            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not map value of key `" + key + //
                    "` to Object of class `" + clazz.getName() + "`.", e);
        }
    }

    /**
     * Get the value whose key is the given parameter from the context map and cast it to the given class.
     * <p>Returns null if the value is null.</p>
     *
     * @param key the key to search the value for.
     * @param clazz the class of the resulting object
     * @param <T> the resulting object type
     * @return the value whose key is the given parameter, <code>null</code> if not found
     */
    private <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (clazz.isInstance(value)) {
            return (T) value;
        } else {
            throw illegalArg(key, value, clazz);
        }
    }

    /**
     * Creates an IllegalArgumentException with a pre-filled message.
     * @param key the key
     * @param expected the expected type
     * @param value the value
     * @return the exception ready to be thrown
     */
    private IllegalArgumentException illegalArg(String key, Object value, Class<?> expected) {
       return new IllegalArgumentException(String.format("Value for key `%s` is `%s`%s.", //
               key, value, (value == null ? "" : ". Expected type was `" + expected.getName() + "`")));
    }

    /**
     * Uses a ServiceLoader to locate available {@link TomlParser} on classpath.
     * If none is found, the default {@link SimpleTomlParser} is used
     *
     * @throws IllegalStateException if too much {@link TomlParser} are found on classpath.
     */
    private static void initDefaultParser() {
        List<TomlParser> parsers = new ArrayList<TomlParser>();
        Iterator<TomlParser> parserIterator = ServiceLoader.load(TomlParser.class).iterator();
        while(parserIterator.hasNext()) parsers.add(parserIterator.next());
        // check too much (built-in one always available + one additional is OK)
        if (parsers.size() > 2) {
            throw new IllegalStateException("Too much TomlParser found on classpath: " + parsers);
        }
        // iterate on all available parsers
        for (TomlParser parser: parsers) {
            LOGGER.log(Level.CONFIG, "Found TomlParser instance on classpath: " + parser.getClass().getName());
            if (SimpleTomlParser.class.equals(parser.getClass()) && defaultParser != null) {
                continue;
            }
            defaultParser = parser;
        }
        // last-chance fallback
        if (defaultParser == null) {
            defaultParser = new SimpleTomlParser();
            LOGGER.log(Level.WARNING, "No TomlParser service loaded, defaulting to: " + defaultParser.getClass().getName());
        }
    }
}
