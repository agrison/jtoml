package me.grison.jtoml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.grison.jtoml.impl.Toml;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Junit test for the toml example file from toml "spec"
 * (https://github.com/mojombo/toml).
 * 
 * @author $Author: Johannes Bergmann$
 */
public class ExampleTomlTest {
	private static Toml toml;

	@BeforeClass
	public static void parseExampleFile() throws FileNotFoundException {
        toml = new Toml();
		String exampleToml = Util.FileToString.read(new File(toml.getClass().getResource("/example.toml").getFile()));
		toml.parseString(exampleToml);
	}

	@Test
	public void testTopLevelString() {
		// title = "TOML Example"
		assertEquals("TOML Example", toml.getString("title"));
	}

	@Test
	public void testNestedStrings() {
		// [owner]
		// name = "Tom Preston-Werner"
		// organization = "GitHub"
		assertEquals("Tom Preston-Werner", toml.getString("owner.name"));
		assertEquals("GitHub", toml.getString("owner.organization"));
	}

	@Test
	public void testNewlineAndHashInString() {
		// [owner]
		// bio = "GitHub Cofounder & CEO\nLikes tater tots and beer #awesome."
		assertEquals(
				"GitHub Cofounder & CEO\nLikes tater tots and beer #awesome.",
				toml.getString("owner.bio"));
	}

	@Test
	public void testDate() throws ParseException {
		// dob = 1979-05-27T07:32:00Z # First class dates? Why not?
		assertEquals(
				createCalendar("yyyy-MM-dd-HH:mm:ssZ",
						"1979-05-27-07:32:00-0000"), toml.getDate("owner.dob"));
	}

	@Test
	public void testArray() {
		// [database]
		// server = "192.168.1.1"
		// ports = [ 8001, 8001, 8002 ]
		assertEquals("192.168.1.1", toml.get("database.server"));
		assertEquals(
				createList(Long.valueOf(8001), Long.valueOf(8001), Long.valueOf(8002)),
                toml.getList("database.ports"));
	}

	@Test
	public void testLong() {
		// [database]
		// connection_max = 5000
		// latency_max = 42 # this is in milliseconds
		assertEquals(Long.valueOf(5000),
				toml.getLong("database.connection_max"));
		assertEquals(Long.valueOf(42), toml.getLong("database.latency_max"));
	}

	@Test
	public void testBoolean() {
		// [database]
		// enabled = true
		// awesome = false # just because
		assertEquals(Boolean.valueOf(true), toml.getBoolean("database.enabled"));
		assertEquals(Boolean.valueOf(false),
				toml.getBoolean("database.awesome"));
	}

	@Test
	public void testIndentationDoesntMatter() {
		// [servers]
		//
		// # You can indent as you please. Tabs or spaces. TOML don't care.
		// [servers.alpha]
		// ip = "10.0.0.1"
		// dc = "eqdc10"
		assertEquals("10.0.0.1", toml.get("servers.alpha.ip"));
		assertEquals("eqdc10", toml.get("servers.alpha.dc"));

		// [servers.beta]
		// ip = "10.0.0.2"
		// dc = "eqdc10"
		assertEquals("10.0.0.2", toml.get("servers.beta.ip"));
		assertEquals("eqdc10", toml.get("servers.beta.dc"));
	}

	@Test
	public void testArrayOfArrays() {
		// [clients]
		// data = [ ["gamma", "delta"], [1, 2] ] # just an update to make sure
		// parsers support it
		assertEquals(ExampleTomlTest.<Object> createList(
				createList("gamma", "delta"),
				createList(Long.valueOf(1), Long.valueOf(2))),
				toml.getList("clients.data"));
	}

	private static Calendar createCalendar(String pattern, String value)
			throws ParseException {
		Date date = new SimpleDateFormat(pattern).parse(value);
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

    @Test
    public void testNegativeIntegerInTable() {
        Toml t = Toml.parse("[a]\ncd = -3");
        assertEquals(t.getMap("a").get("cd"), -3L);
    }

    @Test
    /** Should not crash :) */
    public void testIssue10() {
        Toml.serialize("something", "hello");
    }

	private static <T> List<T> createList(T... elements) {
		return Arrays.<T> asList(elements);
	}
}
