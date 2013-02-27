package me.grison.jtoml;

/**
 * Toml TomlSerializer interface.
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public interface TomlSerializer {
    /**
     * Serializes the given Object to a TOML String.
     *
     * @param object the Object to be serialized
     * @return the TOML String representing the given Object.
     */
    String serialize(Object object);

    /**
     * Serializes the given Object to a TOML String.
     *
     * @param rootKey the root key (can be empty or null)
     * @param object the Object to be serialized
     * @return the TOML String representing the given Object.
     */
    String serialize(String rootKey, Object object);
}
