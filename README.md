TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

Usage
----

### Parsing

```java
Toml toml = Toml.parse("pi = 3.14\nfoo = \"bar\""); // parse a String
toml = Toml.parse(new File("foo.toml")); // or a file
```

### Getting values

The `Toml` class support different types of getters so that you can retrieve a specific type or the underlying `Object` without casting.

```java
// get different types
toml.get("foo"); // Object
toml.getString("foo"); // String
toml.getBoolean("foo"); // Boolean
toml.getDate("foo"); // Calendar
toml.getDouble("foo"); // Double
toml.getLong("foo"); // Long
toml.getList("foo"); // List<Object>
toml.getMap("foo"); // Map<String, Object>
```

### Mapping custom types

You can map a custom type from an entire TOML String or part of it.
Let's say you would like to map the following TOML to a `Player` entity.

```ini
[player]
nickName = "foo"
score = 42
```

You could do it as simple as following:
```java
// or Custom objects
public class Player {
    String name;
    Long score;
}
Toml toml = Toml.parse("[player]\nname = \"foo\"\nscore = 42");
Player player = toml.getAs("player", Player.class);
player.name; // "foo"
player.score; // 42L
```

**Note:** Supported types are `Long`, `String`, `Double`, `Boolean`, `Calendar`, `List`, `Map` or Objects having the pre-cited types only.

Todo
-----

* Consider using ANTLR or Parboiled for parsing instead of a hand-made one.

License
-----
MIT.
