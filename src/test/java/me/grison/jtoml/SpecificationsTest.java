package me.grison.jtoml;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

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
        Assert.assertEquals(42, toml.getInt("foo").intValue());
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
}
