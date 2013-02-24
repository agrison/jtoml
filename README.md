TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

The code's not the prettiest, but it successfully parses the sample file.
See

Usage:
----
```java
Toml toml = new Toml();

// parse a String
toml.parseString(tomlContent);
// or a file: toml.parseFile(new File("foo.toml"));

// get different types
toml.get("foo"); // Object
toml.getString("foo"); // String
toml.getBoolean("foo"); // Boolean
toml.getDate("foo"); // Calendar
toml.getFloat("foo"); // Float
toml.getInt("foo"); // Integer
toml.getList("foo"); // List<Object>
```

Todo:
-----

* Support for multiline arrays
