package uk.ac.ucl.jsh.model.programs;

import org.junit.Test;
import org.junit.Before;
import uk.ac.ucl.jsh.JshTest;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class TailTest extends JshTest {

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
    public void testBasic() throws Exception
    {
        String testString = "tail " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testIOInputBasic() throws Exception {
        String testString = "tail < " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }


    @Test
    public void testStandard() throws Exception //standard example
    {
        String testString = "tail -n 4 " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testNoLines() throws Exception //n = 0
    {
        String testString = "tail -n 0 " + tempFileName;
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testEmptyFile() throws Exception //empty file
    {
        createTempFile();
        String testString = "tail -n 4 " + getTempFileName();
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testBigLines() throws Exception //n > lines in text
    {
        String testString = "tail -n 10 " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testNegativeLines() throws Exception //n < 0
    {
        String testString = "tail -n -4 " + tempFileName;
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testMissingLines() throws Exception //missing args
    {
        try {
            String testString = "tail -n " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("tail: wrong arguments",e.getMessage());
        }
    }

    @Test
    public void testWrongArg() throws Exception //wrong args
    {
        try {
            String testString = "tail -m 3 " + tempFileName;
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("tail: -m is not an option",e.getMessage());
        }
    }

    @Test
    public void testNoSuchFile() throws Exception //no such file
    {
        try {
            String testString = "tail -n 3 test1.txt";
            String result = getOutputString(testString);
        }
        catch (RuntimeException e) {
            assertEquals("tail: test1.txt does not exist",e.getMessage());
        }
    }

    @Test
    public void testPiping() throws Exception {
        String testString = "cat " + tempFileName + " | tail -n 2";
        assertEquals("cat" + System.getProperty("line.separator") + "wow", getOutputString(testString));
    }

    @Test
    public void testIOInput() throws Exception {
        String testString = "tail -n 10 < " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testIOOutput() throws Exception {
        createTempFile();
        String testString = "tail -n 2 " + tempFileName + " > " + getTempFileName();
        getOutputString(testString);
        String newText = getOutputString("cat " + getTempFileName());
        assertEquals("cat" + lineSeparator + "wow", newText);
    }

}

