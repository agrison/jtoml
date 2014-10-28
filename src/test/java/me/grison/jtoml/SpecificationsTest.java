package me.grison.jtoml;

import me.grison.jtoml.impl.Toml;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

/**
 * Unit test for specifications (types).
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class SpecificationsTest {
    static {
        Locale.setDefault(Locale.US);
    }
    @Test
    public void testInteger() {
        Toml toml = Toml.parse("foo = 42");
        Assert.assertEquals(42L, toml.getLong("foo").longValue());
    }

    @Test
    public void testDouble() {
        Toml toml = Toml.parse("foo = 3.141592653589793");
        Assert.assertEquals(3.14159265, toml.getDouble("foo").doubleValue(), 0.00001d);
    }

    @Test
    public void testBoolean() {
        Toml toml = Toml.parse("foo = true\nbar = false");
        Assert.assertEquals(true, toml.getBoolean("foo").booleanValue());
        Assert.assertEquals(false, toml.getBoolean("bar").booleanValue());
    }

    @Test
    public void testString() {
        Toml toml = Toml.parse("foo = \"Hello\\tWorld\\nI'm having \\u0061 good time \\u263a \\\"!\"");
        Assert.assertEquals("Hello\tWorld\nI'm having a good time ☺ \"!", toml.getString("foo"));
    }

    @Test
    public void testMultiLineString() {
        Toml toml = Toml.parse("foo = \"\"\"Hello\nWorld\nfoo\tbar\"\"\"");
        Assert.assertEquals("Hello\nWorld\nfoo\tbar", toml.getString("foo"));
        toml = Toml.parse("foo = \"\"\"\nHello\nWorld\nfoo\tbar\"\"\"");
        Assert.assertEquals("Hello\nWorld\nfoo\tbar", toml.getString("foo"));
    }

    @Test
    public void testArray() {
        Toml toml = Toml.parse("foo = [\n\"Hello\",\n\n\t \"World\"\n,\"Nice\"]");
        Assert.assertEquals(Arrays.asList("Hello", "World", "Nice"), toml.getList("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInconsistentArray() {
        Toml.parse("foo = [1, true, \"Hello\"]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongStringEscaping() {
        // wrong = "C:\Users\nodejs\templates" # note: doesn't produce a valid path
        // right = "C:\\Users\\nodejs\\templates"
        Toml toml = Toml.parse("right = \"C:\\\\Users\\\\nodejs\\\\templates\"");
        Assert.assertEquals("C:\\Users\\nodejs\\templates", toml.get("right"));
        toml = Toml.parse("wrong = \"C:\\Users\\nodejs\\templates\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOverwritePreviousKey() {
        Toml.parse("[fruit]\ntype = \"apple\"\n\n[fruit.type]\napple = \"yes\"");
    }

    @Test
    public void testNull() {
        Toml toml = Toml.parse("foo = 1337");
        Assert.assertEquals(null, toml.get("bar"));
        Assert.assertEquals(null, toml.getString("bar"));
        Assert.assertEquals(null, toml.getLong("bar"));
        Assert.assertEquals(null, toml.getDouble("bar"));
        Assert.assertEquals(null, toml.getDate("bar"));
        Assert.assertEquals(null, toml.getBoolean("bar"));
        Assert.assertEquals(null, toml.getList("bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleType() {
        Toml toml = Toml.parse("foo = 1337");
        toml.getString("foo");
    }

    @Test
    public void testGetMap() {
        Toml toml = Toml.parse("[foo]\nbar = true\nbaz = false");
        Map<String, Object> map = (Map<String, Object>)toml.get("foo");
        Assert.assertTrue(map.containsKey("bar") && map.get("bar").equals(Boolean.TRUE));
        Assert.assertTrue(map.containsKey("baz") && map.get("baz").equals(Boolean.FALSE));
    }

    @Test
    public void testCustomObject() {
        Toml toml = Toml.parse("[foo]\nstringKey=\"a\"\nlongKey=42\ndoubleKey=13.37\n" + //
                "booleanKey=true\nlistKey=[1,2,3]\n[foo.bar]\nbazz=\"Hello\"\ndummy=459\n");
        Foo foo = toml.getAs("foo", Foo.class);
        Assert.assertEquals("a", foo.stringKey);
        Assert.assertEquals(Long.valueOf(42), foo.longKey);
        Assert.assertEquals(Double.valueOf(13.37), foo.doubleKey, 0.00001d);
        Assert.assertEquals(Boolean.TRUE, foo.booleanKey);
        Assert.assertEquals(Arrays.asList(1L, 2L, 3L), foo.listKey);
        Assert.assertEquals("Hello", foo.bar.bazz);
        Assert.assertEquals(Long.valueOf(459), foo.bar.dummy);
        // test no root group
        toml = Toml.parse("stringKey=\"a\"\nlongKey=42\ndoubleKey=13.37\n" + //
                "booleanKey=true\nlistKey=[1,2,3]\n[bar]\nbazz=\"Hello\"\ndummy=459");
        Assert.assertEquals(foo.toString(), toml.getAs("", Foo.class).toString());
    }

    @Test
    public void testSerialization() throws Exception {
        String tomlContent = "[foo]\nstringKey = \"a\"\nlongKey = 42\ndoubleKey = 13.37\n" + //
                "booleanKey = true\nlistKey = [[1, 2, 3], [\"hello\", \"world\"]]\nawesome = true\n\n" +
                "[foo.bar]\nbazz = \"Hello\"\ndummy = 459\n\n[foo.map]\none = 1\ntwo = 2";
        Toml toml = Toml.parse(tomlContent);
        Foo foo = toml.getAs("foo", Foo.class);
        Assert.assertEquals(tomlContent, Toml.serialize("foo", foo).trim() /* removes last \n */);
        Assert.assertEquals(tomlContent, toml.serialize().trim());
    }

    /**
     * Simple class tested above.
     */
    public static class Bar {
        String bazz;
        Long dummy;

        @Override
        public String toString() {
            return bazz + dummy;
        }
    }

    public static class Foo {
        String stringKey;
        Long longKey;
        Double doubleKey;
        Boolean booleanKey;
        List<Object> listKey;
        Bar bar;
        Boolean awesome;
        Map<String, Integer> map;


        @Override
        public String toString() {
            return stringKey + longKey + doubleKey + booleanKey + listKey + bar + awesome + map;
        }
    }
}
