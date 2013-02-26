package me.grison.jtoml;

import me.grison.jtoml.impl.Toml;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        Toml toml = Toml.parse("foo = \"Hello\\tWorld\\nI'm having \\\"!\"");
        Assert.assertEquals("Hello\tWorld\nI'm having \"!", toml.getString("foo"));
    }

    @Test
    public void testArray() {
        Toml toml = Toml.parse("foo = [\n\"Hello\",\n\n\t \"World\"\n,\"Nice\"]");
        Assert.assertEquals(Arrays.asList("Hello", "World", "Nice"), toml.getList("foo"));
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
                "booleanKey=true\nlistKey=[1,2,3]\n[foo.bar]\nbazz=\"Hello\"\ndummy=459");
        Foo foo = toml.getAs("foo", Foo.class);
        Assert.assertEquals("a", foo.stringKey);
        Assert.assertEquals(Long.valueOf(42), foo.longKey);
        Assert.assertEquals(Double.valueOf(13.37), foo.doubleKey, 0.00001d);
        Assert.assertEquals(Boolean.TRUE, foo.booleanKey);
        Assert.assertEquals(Arrays.asList(1L, 2L, 3L), foo.listKey);
        Assert.assertEquals("Hello", foo.bar.get("bazz"));
        Assert.assertEquals(Long.valueOf(459), foo.bar.get("dummy"));
    }

    /**
     * Simple class tested above.
     */
    public static class Foo {
        String stringKey;
        Long longKey;
        Double doubleKey;
        Boolean booleanKey;
        List<Object> listKey;
        Map<String, Object> bar;

        @Override
        public String toString() {
            return "stringKey=" + stringKey + ", longKey=" + longKey + ", doubleKey=" + doubleKey + //
                    ", booleanKey=" + booleanKey + ", listKey=" + listKey + ", bar=" + bar;
        }
    }
}
