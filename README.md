TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

Usage
----
```java
Toml toml = Toml.parse("pi = 3.14\nfoo = \"bar\""); // parse a String
toml = Toml.parse(new File("foo.toml")); // or a file

// get different types
toml.get("foo"); // Object
toml.getString("foo"); // String
toml.getBoolean("foo"); // Boolean
toml.getDate("foo"); // Calendar
toml.getDouble("foo"); // Double
toml.getLong("foo"); // Long
toml.getList("foo"); // List<Object>
toml.getMap("foo"); // Map<String, Object>

// or Custom objects
public class Player {
    String nickName;
    Long score;
}
toml = Toml.parse("[player]\nnickName = \"foo\"\nscore = 42");
Player player = toml.getAs("player", Player.class);
player.nickName; // "foo"
player.score; // 42L
```

Todo
-----

* Consider using ANTLR or Parboiled for parsing instead of a hand-made one.

License
-----
MIT.
