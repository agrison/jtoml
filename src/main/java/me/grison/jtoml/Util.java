package me.grison.jtoml;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Toml Utilities.
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class Util {
    /**
     * Helper class for handling ISO 8601 strings of the following format:
     * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
     *
     * Author: wrigiel (see: http://stackoverflow.com/users/1010931/wrygiel)
     * Taken from:
     * http://stackoverflow.com/questions/2201925/converting-iso8601-
     * compliant-string-to-java-util-date
     */
    public static class ISO8601 {
        /** Transform ISO 8601 string to Calendar. */
        public static Calendar toCalendar(final String iso8601string) throws ParseException {
            Calendar calendar = GregorianCalendar.getInstance();
            String s = iso8601string.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
            calendar.setTime(date);
            return calendar;
        }
    }

    /**
     * Toml String Utilities.
     * <p/>
     * Note: This is to avoid dependency on Apache commons for such limited features.
     */
    public static class TomlString {
        /**
         * Unescapes the a list of literals found in the given String.
         * It will replace characters (<code>'\' + 't'</code>, <code>'\' + 'n'</code>, ...) to there equivalent (<code>'\t'</code>, <code>'\n'</code>).
         *
         * <ul>
         *     <li><code>'\'</code> + <code>'0'</code> -> <code>'\0'</code> null character (0x00)</li>
         *     <li><code>'\'</code> + <code>'t'</code> -> <code>'\t'</code> tab character (0x09)</li>
         *     <li><code>'\'</code> + <code>'n'</code> -> <code>'\n'</code> newline character (0x0a)</li>
         *     <li><code>'\'</code> + <code>'r'</code> -> <code>'\r'</code> carriage return character (0x0d)</li>
         *     <li><code>'\'</code> + <code>'"'</code> -> <code>'"'</code> quote character (0x22)</li>
         *     <li><code>'\'</code> + <code>'\'</code> -> <code>'\'</code> backslash character (0x5c)</li>
         * </ul>
         *
         * @param input the String to unescape
         * @return the unescaped String
         */
        public static String unescape(String input) {
            StringBuffer buffer = new StringBuffer(input.length());
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (ch != '\\') {
                    buffer.append(ch);
                } else {
                    if (i == input.length() - 1) {
                        throw new IllegalStateException("Invalid escape sequence at the end of the input.");
                    }
                    ch = input.charAt(++i);
                    switch (ch) {
                        case '0':
                            buffer.append('\u0000');
                            break;
                        case 't':
                            buffer.append('\t');
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        case 'r':
                            buffer.append('\r');
                            break;
                        case '\\':
                            buffer.append('\\');
                            break;
                        case '\"':
                            buffer.append('"');
                            break;
                        default:
                            throw new IllegalArgumentException("Escape sequence \\ " + ch + " in isn't known. " + //
                                    "Known sequences are: " + "\\0, \\t, \\n, \\b, \\r, \\\\, \\\".\n" + //
                                    "Offending string: " + input + "\n" + "                 " + createWhitespaceString(i) + "^");
                    }
                }
            }
            return buffer.toString();
        }

        /**
         * Creates a String made of spaces repeated for the given amount of time.
         * <p/>
         * <code>createWhitespaceString(3) -> "&nbsp;&nbsp;&nbsp;" // 3 spaces</code>
         *
         * @param length the length of the resulting String
         * @return a String made of a number of spaces equals to the given `length` parameter.
         */
        private static String createWhitespaceString(int length) {
            char[] charArray = new char[length];
            Arrays.fill(charArray, ' ');
            return String.valueOf(charArray);
        }

        /**
         * Counts the number of occurrences of a String in an other String.
         *
         * @param str the String to search in.
         * @param needle the String to count the occurrences of.
         * @return the number of occurrences found.
         */
        public static int countOccurrences(String str, String needle) {
            int index = 0, count = 0;
            while (index != -1) {
                index = str.indexOf(needle, index);
                if (index != -1) {
                    count++;
                    index += needle.length();
                }
            }
            return count;
        }
    }

    /**
     * Utilities around File to String conversions.
     * <p/>
     * Note: This is to avoid dependency on Apache commons for such limited features.
     */
    public static class FileToString {
        public static String read(File file) throws FileNotFoundException {
            return new Scanner(file).useDelimiter("\\Z").next();
        }
    }

    /**
     * Utilities around Reflection.
     */
    public static class Reflection {
        /** List of types supported Natively by TOML + Map */
        static final Set<Class<?>> TOML_SUPPORTED = new HashSet<Class<?>>(Arrays.asList(Long.class, Double.class, //
                Calendar.class, Boolean.class, String.class, List.class, Map.class));

        /**
         * Returns whether the given type is a built-in toml supported type.
         *
         * @param clazz a class object.
         * @return whether it is a built-in toml type
         */
        public static boolean isTomlSupportedType(Class<?> clazz) {
            return TOML_SUPPORTED.contains(clazz);
        }

        /**
         * Set the value of the given field on given object.
         *
         * @param field the field
         * @param object the object
         * @param value the value
         * @throws IllegalAccessException
         */
        public static void setFieldValue(Field field, Object object, Object value) throws IllegalAccessException {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(isAccessible);
        }
    }
}
