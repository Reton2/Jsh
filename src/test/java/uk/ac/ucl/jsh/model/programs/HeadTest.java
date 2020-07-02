package uk.ac.ucl.jsh.model.programs;

import org.junit.Test;
import org.junit.Before;
import uk.ac.ucl.jsh.JshTest;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;


public class HeadTest extends JshTest {

    private String tempFileName;
    private String text;

    @Before
    public void init() throws IOException {
        createTempFile();
        FileWriter writer = getTempFileWriter();
        String fatCat = "big" + lineSeparator + "fat" + lineSeparator + "cat" + lineSeparator + "wow";
        this.text = fatCat;
        writer.write(text);
        writer.flush();
        writer.close();
        this.tempFileName = getTempFileName();
    }

    @Test
    public void testStandard() throws Exception //standard example
    {
        String testString = "head -n 4 " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testNoLines() throws Exception //n = 0
    {
        String testString = "head -n 0 " + tempFileName;
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testEmptyFile() throws Exception //empty file
    {
        createTempFile();
        String testString = "head -n 4 " + getTempFileName();
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testBigLines() throws Exception //n > lines in text
    {
        String testString = "head -n 10 " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testNegativeLines() throws Exception //n < 0
    {
        String testString = "head -n -4 " + tempFileName;
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testMissingLines() throws Exception //missing args
    {
        try {
            String testString = "head -n " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("head: wrong arguments",e.getMessage());
        }
    }

    @Test
    public void testWrongArg() throws Exception //wrong args
    {
        try {
            String testString = "head -m 3 " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("head: -m is not an option",e.getMessage());
        }
    }

    @Test
    public void testNoSuchFile() throws Exception //no such file
    {
        try {
            String testString = "head -n 3 test1.txt";
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("head: test1.txt does not exist",e.getMessage());
        }
    }

    @Test
    public void testPiping() throws Exception {
        String testString = "cat " + tempFileName + " | head -n 2";
        assertEquals("big" + lineSeparator + "fat", getOutputString(testString));
    }

    @Test
    public void testIOInputWithoutNumber() throws Exception {
        String testString = "head < " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testIOInput() throws Exception {
        String testString = "head -n 10 < " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testIOOutput() throws Exception {
        createTempDir();
        String testString = "head -n 10 " + tempFileName + " > " + getTempFileName();
        String newText = getOutputString("cat " + getTempFileName());
        assertEquals(text, newText);
    }

}