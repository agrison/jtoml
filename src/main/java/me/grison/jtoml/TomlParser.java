package me.grison.jtoml;

import java.util.Map;

/**
 * Toml Parser interface.
 *
 * <p>Every parser should implement that interface.</p>
 *
 * <p>See: The <a href="https://github.com/mojombo/toml">TOML GitHub</a> project for more information about it.</p>
 *
 * @author Alexandre Grison
 */
public interface TomlParser {
    /**
     * Parses the given TOML String.
     *
     * @param tomlString the TOML String
     * @return a Map representing the given TOML structure.
     */
    Map<String, Object> parse(String tomlString);
}
