TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

Usage:
----
```java
Toml toml = Toml.parse("pi = 3.14\nfoo = \"bar\""); // parse a String
toml = Toml.parse(new File("foo.toml")); // or a file

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
