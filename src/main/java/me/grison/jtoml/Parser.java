package me.grison.jtoml;

import java.io.File;

/**
 * Toml Parser interface
 *
 * @author Alexandre Grison
 */
public interface Parser {
    /**
     * Parse the given String as TOML.
     *
     * @param string the string to be parsed.
     * @param <T>    the return type.
     * @return the parsed structure
     */
    <T extends Parser & Getter> T parseString(String string);

    /**
     * Parse the given File as TOML.
     *
     * @param file the file to be parsed.
     * @param <T>  the return type.
     * @return the parsed structure
     */
    <T extends Parser & Getter> T parseFile(File file);
}
