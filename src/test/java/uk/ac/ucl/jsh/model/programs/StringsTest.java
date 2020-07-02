package uk.ac.ucl.jsh.model.programs;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StringsTest extends JshTest {

    @Before
    public void init() throws IOException {
        createTempFile();
        FileWriter writer = getTempFileWriter();
        String fileString = "is this" + lineSeparator + "big" + lineSeparator + "or" +
                lineSeparator + "small";
        writer.write(fileString);
        writer.flush();
        writer.close();
    }

    @Test
    public void basicTest() throws IOException {
        String output = getOutputString("strings " + getTempFileName());
        String expected = "is this" + lineSeparator + "small";
        assertEquals(expected, output);
    }

    @Test
    public void numberTest() throws IOException {
        String output = getOutputString("strings -n 6 " + getTempFileName());
        String expected = "is this";
        assertEquals(expected, output);
    }

    @Test
    public void numberTestIORedirection() throws IOException {
        String output = getOutputString("strings -n 6 < " + getTempFileName());
        String expected = "is this";
        assertEquals(expected, output);
    }

    @Test
    public void typeTestDecimal() throws IOException {
        String output = getOutputString("strings -t d " + getTempFileName());
        String expected = "0\tis this" + lineSeparator + (3 * lineSeparator.length() + 12) +"\tsmall";
        assertEquals(expected, output);
    }

    @Test
    public void bothOptionTest() throws IOException {
        String output = getOutputString("strings -n 6 -t d " + getTempFileName());
        String expected = "0\tis this";
        assertEquals(expected, output);
    }

    @Test
    public void numberOptionMissingTest() throws IOException {
        String output = getOutputString("_strings -n");
        String expected = "strings: missing option input";
        assertEquals(expected, output);
    }

    @Test
    public void typeOptionMissingTest() throws IOException {
        String output = getOutputString("_strings -t");
        String expected = "strings: missing option input";
        assertEquals(expected, output);
    }

    @Test
    public void numberOptionIncorrectTest() throws IOException {
        String output = getOutputString("_strings -n -1" + getTempFileName());
        String expected = "strings: number input should be positive integer";
        assertEquals(expected, output);
    }

    @Test
    public void typeOptionIncorrectTest() throws IOException {
        String output = getOutputString("_strings -t sad" + getTempFileName());
        String expected = "strings: incorrect option input for -t";
        assertEquals(expected, output);
    }

}
