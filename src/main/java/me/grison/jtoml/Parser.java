package me.grison.jtoml;

/**
 * Toml Parser interface
 *
 * @author $Author: alexandre grison$
 */
public interface Parser {
    /**
     * Parse the given String as TOML.
     * @param string the string to be parsed.
     */
    void parseString(String string);
    //void parseFile(File file);
}
