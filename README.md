TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

Usage:
----
```java
Toml toml = Toml.parse(tomlContent); // parse a String
// or a file: Toml.parse(new File("foo.toml"));

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
* Consider using a grammar and ANTLR for parsing instead of a hand-made one.