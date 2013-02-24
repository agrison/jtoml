import me.grison.jtoml.Toml;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Toml test junit.
 *
 * @author $Author: alexandre grison$
 */
public class TestToml {
    @Test
    public void test() throws IOException {
        String tomlContent = new String(IOUtils.toByteArray(getClass().getResourceAsStream("/test.toml")), "UTF-8");
        Toml toml = Toml.parse(tomlContent);

        Assert.assertEquals("TOML Example", toml.getString("title"));

        Assert.assertEquals("Tom Preston-Werner", toml.getString("owner.name"));
        Assert.assertEquals("GitHub", toml.getString("owner.organization"));
        Assert.assertEquals("GitHub Cofounder & CEO\nLikes tater tots and beer #awesome.", toml.getString("owner.bio"));
        Assert.assertEquals("Sun May 27 09:32:00 CEST 1979", toml.getDate("owner.dob").getTime().toString());

        Assert.assertEquals("192.168.1.1", toml.getString("database.server"));
        Assert.assertEquals(3, toml.getList("database.ports").size());
        Assert.assertEquals(Arrays.asList(8001, 8001, 8002), toml.getList("database.ports"));
        Assert.assertEquals(5000, toml.getInt("database.connection_max").intValue());
        Assert.assertEquals(42, toml.getInt("database.latency_max").intValue());
        Assert.assertEquals(true, toml.getBoolean("database.enabled"));
        Assert.assertEquals(false, toml.getBoolean("database.awesome"));

        Assert.assertEquals("10.0.0.1", toml.getString("servers.alpha.ip"));
        Assert.assertEquals("eqdc10", toml.getString("servers.alpha.dc"));
        Assert.assertEquals("10.0.0.2", toml.getString("servers.beta.ip"));
        Assert.assertEquals("eqdc10", toml.getString("servers.beta.dc"));

        List<Object> clientsData = toml.getList("clients.data");
        Assert.assertEquals(2, clientsData.size());
        Assert.assertEquals(Arrays.asList("gamma", "delta"), clientsData.get(0));
        Assert.assertEquals(Arrays.asList(1, 2), clientsData.get(1));

        List<Object> multiline = toml.getList("clients.multiline");
        Assert.assertEquals(2, multiline.size());
        Assert.assertEquals(Arrays.asList(1, 2, 3), multiline.get(0));
        Assert.assertEquals(Arrays.asList("hello", "world"), ((List<?>) multiline).get(1));

        List<Object> superList = toml.getList("clients.super");
        Assert.assertEquals(Arrays.asList(1, 2), superList);
    }
}
