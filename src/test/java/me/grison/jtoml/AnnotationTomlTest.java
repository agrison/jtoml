package me.grison.jtoml;

import me.grison.jtoml.annotations.SerializedName;
import me.grison.jtoml.impl.Toml;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationTomlTest {

    public static class Player {
        String name;

        @SerializedName("abstract")
        String _abstract;

        @SerializedName("")
        Double position;
    }

    @Test
    public void testSerializedName() {
        Toml toml = Toml.parse("[player]\nname=\"Zachary\"\nabstract=\"This is abstract.\"\nposition=1.8");
        Player player = toml.getAs("player", Player.class);
        assertEquals("Zachary", player.name);
        assertEquals("This is abstract.", player._abstract);
        assertEquals(Double.valueOf(1.8) , player.position);
    }
}
