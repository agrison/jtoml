TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

The code's not the prettiest, but it successfully parses the sample file.
See

Usage:
----

    Toml toml = new Toml();
    toml.parseString("...TOML...");

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

Todo:
-----

* Support for multiline arrays
* Tests
* Maven packaging