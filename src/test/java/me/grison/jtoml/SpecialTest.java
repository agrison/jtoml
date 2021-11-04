package me.grison.jtoml;

import me.grison.jtoml.impl.Toml;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SpecialTest {
    private static Toml toml;

    @BeforeClass
    public static void parseExampleFile() {
        toml = new Toml();
        String exampleToml = Util.FileToString.read(new File(toml.getClass().getResource("/special.toml").getFile()));
//        System.out.println(exampleToml);
        toml.parseString(exampleToml);
    }

    @Test
    public void commentInString() {
        Assert.assertEquals("#FF0000", toml.getString("color"));
    }

    @Test
    public void commentInStringArray() {
        Assert.assertEquals("#F00", toml.getList("colors").get(0));
        Assert.assertEquals("#0F0", toml.getList("colors").get(1));
        Assert.assertEquals("#00F", toml.getList("colors").get(2));
    }

    @Test
    public void commentInMultiLineArray() {
        Assert.assertEquals("#F00", toml.getList("colors2").get(0));
        Assert.assertEquals("#0F0", toml.getList("colors2").get(1));
        Assert.assertEquals("#00F", toml.getList("colors2").get(2));
    }

    @Test
    public void commentAfterComma() {
        Assert.assertEquals("A", toml.getList("alphabet").get(0));
        Assert.assertEquals("B", toml.getList("alphabet").get(1));
        Assert.assertEquals("C", toml.getList("alphabet").get(2));
    }

    @Test
    public void commaInStringArraySingle() {
        Assert.assertEquals(",RED", toml.getList("somethings").get(0));
    }

    @Test
    public void commaInStringArray() {
        Assert.assertEquals(",RED", toml.getList("something_more").get(0));
        Assert.assertEquals(",BLU,E", toml.getList("something_more").get(1));
        Assert.assertEquals(",GREEN,", toml.getList("something_more").get(2));
    }

    @Test
    public void emptyArray() {
        Assert.assertEquals(Collections.emptyList(), toml.getList("empty"));
    }

    @Test
    public void nestedArray(){
        Assert.assertEquals(",First", ((List) toml.getList("nestedArray").get(0)).get(0));
        Assert.assertEquals(",Second,", ((List) toml.getList("nestedArray").get(0)).get(1));
        Assert.assertEquals("T,hid", ((List) toml.getList("nestedArray").get(0)).get(2));

        Assert.assertEquals(1L, ((List) toml.getList("nestedArray").get(1)).get(0));
        Assert.assertEquals(2L, ((List) toml.getList("nestedArray").get(1)).get(1));
        Assert.assertEquals(3L, ((List) toml.getList("nestedArray").get(1)).get(2));
    }
}
