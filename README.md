TOML for Java
===
This is a parser for Tom Preson-Werner's (@mojombo) [TOML](https://raw.github.com/mojombo/toml/) markup language, using Java.

[![Build Status](https://travis-ci.org/agrison/jtoml.png?branch=master)](https://travis-ci.org/agrison/jtoml)

Get it
----

jtoml is published in the sonatype nexus snapshots repository. In order to use it, you may add this repository in your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

Add the jtoml dependency:

```xml
<dependency>
  <groupId>me.grison</groupId>
  <artifactId>jtoml</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

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

```toml
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

### Serialization

JToml supports also serialization. Indeed, you can serialize a custom type to a String having the TOML format representing the original object.
Imagine the following custom Objects:

```java
public class Stats {
    Long maxSpeed;
    Double weight;
    // Constructors
}

public class Car {
    String brand;
    String model;
    Stats stats;
    Boolean legendary;
    Calendar date;
    List<String> options;
    // Constructors
}

Car f12Berlinetta = new Car("Ferrari", "F12 Berlinetta", true, "2012-02-29",
    360, 1525.5, Arrays.asList("GPS", "Leather", "Nitro")
);
String toml = Toml.serialize("f12b", f12Berlinetta);
```

The call to `Toml.serialize()` will produce the following TOML format:
```toml
[f12b]
brand = "Ferrari"
model = "F12 Berlinetta"
legendary = true
date = 2012-02-29T00:00:00Z
options = ["GPS", "Leather", "Nitro"]

[f12b.stats]
maxSpeed = 347
weight = 1525.5
```

You can also serialize the current instance of a `Toml` object:
```java
Toml toml = Toml.parse("[player]\nname = \"foo\"\nscore = 42");
toml.serialize();
```

Will produce the following TOML String
```toml
[player]
name = "foo"
score = 42
```

**Note:** Like for custom types above, supported types are `Long`, `String`, `Double`, `Boolean`, `Calendar`, `List`, `Map` or Objects having the pre-cited types only.


License
-----
[MIT License (MIT)](http://opensource.org/licenses/mit-license.php).

See the [`LICENSE`](https://github.com/agrison/jtoml/blob/master/LICENSE) file.
