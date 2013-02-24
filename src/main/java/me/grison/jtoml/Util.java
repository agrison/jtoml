package me.grison.jtoml;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * Toml Utilities.
 * 
 * @author $Author: alexandre grison$
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
		public static Calendar toCalendar(final String iso8601string)
				throws ParseException {
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

	public static class TomlString {
		// \0 - null character (0x00)
		// \t - tab (0x09)
		// \n - newline (0x0a)
		// \r - carriage return (0x0d)
		// \" - quote (0x22)
		// \\ - backslash (0x5c)
		public static String unescape(String input) {
			StringBuffer buffer = new StringBuffer(input.length());
			for (int i = 0; i < input.length(); i++) {
				char ch = input.charAt(i);
				if (ch != '\\') {
					buffer.append(ch);
				} else {
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
						throw new IllegalArgumentException(
								"Escape sequence \\ "
										+ ch
										+ " in isn't known. Known sequences are: "
										+ "\\0, \\t, \\n, \\b, \\r, \\\\, \\\".\n"
										+ "Offending string: " + input + "\n"
										+ "                 "
										+ createWhitespaceString(i) + "^");
					}
				}
			}
			return buffer.toString();
		}

		private static String createWhitespaceString(int length) {
			char[] charArray = new char[length];
			Arrays.fill(charArray, ' ');
			return String.valueOf(charArray);
		}
	}

	public static class FileToString {

		public static String read(File file) throws FileNotFoundException {
			return new Scanner(file).useDelimiter("\\Z").next();
		}

	}
}
