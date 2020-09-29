package me.grison.jtoml;

import me.grison.jtoml.impl.Toml;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for specifications (types).
 *
 * @author Alexandre Grison
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
        Assert.assertEquals(3.14159265, toml.getDouble("foo"), 0.00001d);
    }

    @Test
    public void testBoolean() {
        Toml toml = Toml.parse("foo = true\nbar = false");
        Assert.assertEquals(true, toml.getBoolean("foo"));
        Assert.assertEquals(false, toml.getBoolean("bar"));
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
        toml = Toml.parse("foo = \"\"\"\nHello\nWorld \\       \nfoo\tbar\"\"\"");
        Assert.assertEquals("Hello\nWorld foo\tbar", toml.getString("foo"));
        toml = Toml.parse("foo = \"\"\"\\" +
                "       The quick brown \\\n" +
                "       fox jumps over \\\n" +
                "       the lazy dog.\"\"\"");
        Assert.assertEquals("The quick brown fox jumps over the lazy dog.", toml.getString("foo"));
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
        Assert.assertNull(toml.get("bar"));
        Assert.assertNull(toml.getString("bar"));
        Assert.assertNull(toml.getLong("bar"));
        Assert.assertNull(toml.getDouble("bar"));
        Assert.assertNull(toml.getDate("bar"));
        Assert.assertNull(toml.getBoolean("bar"));
        Assert.assertNull(toml.getList("bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleType() {
        Toml toml = Toml.parse("foo = 1337");
        toml.getString("foo");
    }

    @Test
    public void testGetMap() {
        Toml toml = Toml.parse("[foo]\nbar = true\nbaz = false");
        Map<String, Object> map = (Map<String, Object>) toml.get("foo");
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
        Assert.assertEquals(13.37, foo.doubleKey, 0.00001d);
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
    public void testSerialization() {
        String tomlContent = "[foo]\nstringKey = \"a\"\nlongKey = 42\ndoubleKey = 13.37\n" + //
                "booleanKey = true\nlistKey = [[1, 2, 3], [\"hello\", \"world\"]]\nawesome = true\n\n" +
                "[foo.bar]\nbazz = \"Hello\"\ndummy = 459\n\n[foo.map]\none = 1\ntwo = 2";
        Toml toml = Toml.parse(tomlContent);
        Foo foo = toml.getAs("foo", Foo.class);
        Assert.assertEquals(tomlContent, Toml.serialize("foo", foo).trim() /* removes last \n */);
        Assert.assertEquals(tomlContent, toml.serialize().trim());
    }

    @Test
    public void testNegativeIntegerInTable() {
        Toml t = Toml.parse("[a]\ncd = -3");
        assertEquals(t.getMap("a").get("cd"), -3L);
    }

    @Test
    public void testSingleCharacterKey() {
        Toml t = Toml.parse("a = \"Some awesome value\"");
        assertEquals(t.getString("a"), "Some awesome value");
    }

    @Test
    public void testLiteralStrings() {
        Toml t = Toml.parse("winpath  = 'C:\\Users\\nodejs\\templates'\n" +
                "winpath2 = '\\\\ServerX\\admin$\\system32\\'\n" +
                "quoted   = 'Tom \"Dubs\" Preston-Werner'\n" +
                "regex    = '<\\i\\c*\\s*>'");
        assertEquals("C:\\Users\\nodejs\\templates", t.getString("winpath"));
        assertEquals("\\\\ServerX\\admin$\\system32\\", t.getString("winpath2"));
        assertEquals("Tom \"Dubs\" Preston-Werner", t.getString("quoted"));
        assertEquals("<\\i\\c*\\s*>", t.getString("regex"));
        t = Toml.parse("regex2 = '''I [dw]on't need \\d{2} apples'''\n" +
                "lines  = '''\n" +
                "The first newline is\n" +
                "trimmed in raw strings.\n" +
                "   All other whitespace\n" +
                "   is preserved.\n" +
                "'''");
        assertEquals("I [dw]on't need \\d{2} apples", t.getString("regex2"));
        assertEquals("The first newline is\n" +
                "trimmed in raw strings.\n" +
                "   All other whitespace\n" +
                "   is preserved.\n", t.getString("lines")); // even the last newline character is kept
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
