package me.grison.jtoml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
     * Taken from: http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date
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
}
