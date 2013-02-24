package me.grison.jtoml;

/**
 * Toml test class.
 *
 * @author $Author: alexandre grison$
 */
public class Test {
    /**
     [database]
     server = "192.168.1.1"
     ports = [ 8001, 8001, 8002 ]
     connection_max = 5000
     enabled = true

     [servers]

     # You can indent as you please. Tabs or spaces. TOML don't care.
     [servers.alpha]
     ip = "10.0.0.1"
     dc = "eqdc10"

     [servers.beta]
     ip = "10.0.0.2"
     dc = "eqdc10"

     [clients]
     data = [ ["gamma", "delta"], [1, 2] ] # just an update to make sure parsers support it
     * @param args
     */
    public static void main(String[] args) {
        Toml toml = new Toml();
        toml.parseString("# This is a TOML document. Boom.\n" + "\n" + "title = \"TOML Example\"\n" + "\n" + "[owner]\n" + "name = \"Tom Preston-Werner\"\n" + "organization = \"GitHub\"\n" + "bio = \"GitHub Cofounder & CEO\\nLikes tater tots and beer.\"\n" + "dob = 1979-05-27T07:32:00Z # First class dates? Why not?\n" + "\n" + "[database]\n" + "server = \"192.168.1.1\"\n" + "ports = [ 8001, 8001, 8002 ]\n" + "connection_max = 5000\n" + "enabled = true\n" + "\n" + "[servers]\n" + "\n" + "  # You can indent as you please. Tabs or spaces. TOML don't care.\n" + "  [servers.alpha]\n" + "  ip = \"10.0.0.1\"\n" + "  dc = \"eqdc10\"\n" + "\n" + "  [servers.beta]\n" + "  ip = \"10.0.0.2\"\n" + "  dc = \"eqdc10\"\n" + "\n" + "[clients]\n" + "data = [ [\"gamma\", \"delta\"], [1, 2] ] # just an update to make sure parsers support it");

        System.out.println(toml.getString("title"));

        System.out.println(toml.getString("owner.name"));
        System.out.println(toml.getString("owner.organization"));
        System.out.println(toml.getString("owner.bio"));
        System.out.println(toml.getDate("owner.dob").getTime());

        System.out.println(toml.getString("database.server"));
        System.out.println(toml.getList("database.ports"));
        System.out.println(toml.getInt("database.connection_max"));
        System.out.println(toml.getBoolean("database.enabled"));

        System.out.println(toml.getString("servers.alpha.ip"));
        System.out.println(toml.getString("servers.alpha.dc"));
        System.out.println(toml.getString("servers.beta.ip"));
        System.out.println(toml.getString("servers.beta.dc"));

        System.out.println(toml.getList("clients.data"));
    }
}
