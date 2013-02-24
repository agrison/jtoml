import me.grison.jtoml.Toml;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Toml test junit.
 *
 * @author $Author: alexandre grison$
 */
public class TestToml {
    public static final String SAMPLE = "# This is a TOML document. Boom.\n" + "\n" + "title = \"TOML Example\"\n" + "\n" + "[owner]\n" + "name = \"Tom Preston-Werner\"\n" + "organization = \"GitHub\"\n" + "bio = \"GitHub Cofounder & CEO\\nLikes tater tots and beer.\"\n" + "dob = 1979-05-27T07:32:00Z # First class dates? Why not?\n" + "\n" + "[database]\n" + "server = \"192.168.1.1\"\n" + "ports = [ 8001, 8001, 8002 ]\n" + "connection_max = 5000\n" + "enabled = true\n" + "\n" + "[servers]\n" + "\n" + "  # You can indent as you please. Tabs or spaces. TOML don't care.\n" + "  [servers.alpha]\n" + "  ip = \"10.0.0.1\"\n" + "  dc = \"eqdc10\"\n" + "\n" + "  [servers.beta]\n" + "  ip = \"10.0.0.2\"\n" + "  dc = \"eqdc10\"\n" + "\n" + "[clients]\n" + "data = [ [\"gamma\", \"delta\"], [1, 2] ] # just an update to make sure parsers support it";

    @Test
    public void test() {
    }
}
