package me.grison.jtoml;

import java.util.Calendar;
import java.util.List;

/**
 * Toml getter interface.
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public interface Getter {
    /**
     * Get the object.
     * @param key the key where the object is located
     * @return the object located at the given key.
     */
    Object get(String key);

    /**
     * Get the string.
     * @param key the key where the object is located
     * @return the string located at the given key.
     */
    String getString(String key);

    /**
     * Get the integer.
     * @param key the key where the object is located
     * @return the integer located at the given key.
     */
    Integer getInt(String key);

    /**
     * Get the double value.
     * @param key the key where the object is located
     * @return the float located at the given key.
     */
    Double getDouble(String key);

    /**
     * Get the date (as Calendar)
     * @param key the key where the object is located
     * @return the date located at the given key.
     */
    Calendar getDate(String key);

    /**
     * Get the list.
     * @param key the key where the object is located
     * @return the list located at the given key.
     */
    List<Object> getList(String key);

    /**
     * Get a boolean.
     * @param key the key where the object is located
     * @return the boolean located at the given key.
     */
    Boolean getBoolean(String key);
}
