package uk.ac.ucl.jsh.model.programs;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GlobalRegexPrintTest extends JshTest {

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
    public void testStandard() throws IOException
    {
        String result = "fat" + lineSeparator + "cat";
        String testString = "grep \"at\" " + tempFileName;
        assertEquals(result, getOutputString(testString));
    }
    @Test
    public void testWrongArgs() throws IOException
    {
        String testString = "grep";
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("grep: wrong number of arguments", resultException);
    }
    @Test
    public void testNoSuchFile() throws IOException
    {
        String testString = "grep aa WrongName.txt";
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("grep: WrongName.txt does not exist", resultException);
    }

    @Test
    public void testPiping() throws IOException
    {
        String result = "fat";
        String testString = "head -n 2 " + tempFileName + " | grep at";
        assertEquals(result, getOutputString(testString));
    }


}
