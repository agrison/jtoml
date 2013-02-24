import me.grison.jtoml.Toml;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
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
        Toml toml = new Toml();
        String tomlContent = new String(IOUtils.toByteArray(getClass().getResourceAsStream("/test.toml")), "UTF-8");
        toml.parseString(tomlContent);

        Assert.assertEquals("TOML Example", toml.getString("title"));

        Assert.assertEquals("Tom Preston-Werner", toml.getString("owner.name"));
        Assert.assertEquals("GitHub", toml.getString("owner.organization"));
        Assert.assertEquals("GitHub Cofounder & CEO\nLikes tater tots and beer.", toml.getString("owner.bio"));
        Assert.assertEquals("Sun May 27 09:32:00 CEST 1979", toml.getDate("owner.dob").getTime().toString());

        Assert.assertEquals("192.168.1.1", toml.getString("database.server"));
        Assert.assertEquals(3, toml.getList("database.ports").size());
        Assert.assertEquals(5000, toml.getInt("database.connection_max").intValue());
        Assert.assertEquals(true, toml.getBoolean("database.enabled"));

        Assert.assertEquals("10.0.0.1", toml.getString("servers.alpha.ip"));
        Assert.assertEquals("eqdc10", toml.getString("servers.alpha.dc"));
        Assert.assertEquals("10.0.0.2", toml.getString("servers.beta.ip"));
        Assert.assertEquals("eqdc10", toml.getString("servers.beta.dc"));

        List<Object> clientsData = toml.getList("clients.data");
        Assert.assertEquals(2, clientsData.size());
        Assert.assertEquals(2, ((List<?>)clientsData.get(0)).size());
        Assert.assertEquals(2, ((List<?>)clientsData.get(1)).size());
    }
}
