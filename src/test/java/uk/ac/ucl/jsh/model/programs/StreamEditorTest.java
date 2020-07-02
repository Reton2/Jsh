package uk.ac.ucl.jsh.model.programs;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StreamEditorTest extends JshTest{

    private String tempFileName;
    private String text;

    @Before
    public void init() throws IOException {
        createTempFile();
        FileWriter writer = getTempFileWriter();
        this.text = "big" + lineSeparator + "fat" + lineSeparator + "cat" + lineSeparator + "wow";;
        writer.write(text);
        writer.flush();
        writer.close();
        this.tempFileName = getTempFileName();
    }

    @Test
    public void testBasic() throws Exception {
        String testString = "sed 's/fat/cat' " + tempFileName;
        String expected = "big" + lineSeparator + "cat" + lineSeparator + "cat" + lineSeparator + "wow";
        assertEquals(expected, getOutputString(testString));
    }

    @Test
    public void testTooManyOptions() {
        try {
            String testString = "sed 's/fat/cat/g/a' " + tempFileName;
            getOutputString(testString);
        } catch (Exception e) {
            assertEquals("sed: incorrect number of options", e.getMessage());
        }
    }

    @Test
    public void testTooFewOptions() {
        try {
            String testString = "sed 's/fat' " + tempFileName;
            getOutputString(testString);
        } catch (Exception e) {
            assertEquals("sed: incorrect number of options", e.getMessage());
        }
    }

    @Test
    public void testPiping() throws Exception {
        String testString = "cat " + tempFileName + " | sed 's/fat/cat' ";
        String expected = "big" + lineSeparator + "cat" + lineSeparator + "cat" + lineSeparator + "wow";
        assertEquals(expected, getOutputString(testString));
    }

    @Test
    public void testIOInput() throws Exception {
        String testString = "sed 's/fat/cat' < " + tempFileName;
        String expected = "big" + lineSeparator + "cat" + lineSeparator + "cat" + lineSeparator + "wow";
        assertEquals(expected, getOutputString(testString));
    }

    @Test
    public void testIOOutput() throws Exception {
        createTempFile();
        String testString = "sed 's/fat/cat' " + tempFileName + " > " + getTempFileName();
        getOutputString(testString);
        String newText = getOutputString("cat " + getTempFileName());
        String expected = "big" + lineSeparator + "cat" + lineSeparator + "cat" + lineSeparator + "wow";
        assertEquals(expected, newText);
    }

    @Test
    public void testLongOps() throws Exception {
        try {
            String testString = "sed  's/fat/cat/fat' " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("sed: incorrect number of options",e.getMessage());
        }
    }

    @Test
    public void testNoArgs() throws Exception {
        try {
            String testString = "sed ";
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("sed: wrong number of args",e.getMessage());
        }
    }

    @Test
    public void testManyArgs() throws Exception {
        try {
            String testString = "sed s/pla/gla/g maybe.txt sad.play";
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("sed: wrong number of args",e.getMessage());
        }
    }

    @Test
    public void BasicTest() throws IOException {
        createTempFile();
        String text = "maybe this duck is very cool" + lineSeparator +
                "maybe this duck is not very cool" + lineSeparator +
                "or maybe it's just a duck";
        FileWriter writer = getTempFileWriter();
        writer.write(text);
        writer.flush();
        writer.close();
        String output = getOutputString("sed s/[^\\s]*e[^\\s]*/blah/g " + getTempFileName());
        String expected = "blah this duck is blah cool" + lineSeparator +
                "blah this duck is not blah cool" + lineSeparator +
                "or blah it's just a duck";
        assertEquals(expected, output);
    }

    @Test
    public void testOpCode() throws Exception {
        try {
            String testString = "sed 'c/fat/cat/g' " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("sed: incorrect options",e.getMessage());
        }
    }
}
