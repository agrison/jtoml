package me.grison.jtoml;

import java.io.File;
import java.io.IOException;

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

    /**
     * Parse the given File as TOML.
     * @param file the file to be parsed.
     * @throws IOException
     */
    void parseFile(File file) throws IOException;
}
