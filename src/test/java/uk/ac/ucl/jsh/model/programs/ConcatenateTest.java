package uk.ac.ucl.jsh.model.programs;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConcatenateTest extends JshTest {

    private String tempFileName;
    private String text;
    private String tempDirName;

    @Before
    public void init() throws IOException {
        createTempFile();
        createTempDir();

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
        String testString = "cat " + tempFileName;
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testTwoFiles() throws IOException
    {
        createTempFile();
        FileWriter writer = getTempFileWriter();
        String fluffydog = "very" + lineSeparator + "fluffy" + lineSeparator + "dog" + lineSeparator + "bark";
        writer.write(fluffydog);
        writer.flush();
        writer.close();
        String file2 = getTempFileName();
        String testString = "cat " + tempFileName + " " + file2;
        assertEquals(text + lineSeparator + fluffydog, getOutputString(testString));
    }
    @Test
    public void testNestedFile() throws IOException
    {
        createTempFile(getTempDir());
        String testString = "cat " + getTempDirName() +  "/" + getTempFileName();
        assertEquals("", getOutputString(testString));
    }

    @Test
    public void testNoSuchFile() throws IOException
    {
        String testString = "cat WrongName.txt";
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("cat: WrongName.txt does not exist", resultException);
    }

    @Test
    public void testDoubleIOInput() throws IOException
    {
        String testString = "cat < " + tempFileName + " < " + tempFileName;
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("only one file can be redirected as input", resultException);
    }

    @Test
    public void testDoubleIOOutput() throws IOException
    {
        String testString = "cat > " + tempFileName + " > " + tempFileName;
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("only one file can be redirected as output", resultException);
    }

    @Test
    public void testDoubleIOErr() throws IOException
    {
        String testString = "cat 2> " + tempFileName + " 2> " + tempFileName;
        String resultException = "";
        try{
            getOutputString(testString);
        } catch (RuntimeException e)
        {
            resultException = e.getMessage();
        }
        assertEquals("only one file can be redirected as std error", resultException);
    }

    @Test
    public void testNoSuchFileUnsafeIOOutput() throws IOException
    {
        String testString = "_cat WrongName.txt > " + getTempFileName();
        getOutputString(testString);
        StringBuilder resultException = new StringBuilder();
        FileReader r = new FileReader(getTempFileName());
        try (BufferedReader reader = new BufferedReader(r)) {
            String line = null;
            while ((line  = reader.readLine()) != null) {
                resultException.append(line);
            }
        }

        assertEquals("cat: WrongName.txt does not exist", resultException.toString());
    }

    @Test
    public void testNoSuchFileChangeStdErr() throws IOException
    {
        String testString = "cat WrongName.txt 2> " + getTempFileName();
        getOutputString(testString);
        StringBuilder resultException = new StringBuilder();
        FileReader r = new FileReader(getTempFileName());
        try (BufferedReader reader = new BufferedReader(r)) {
            String line = null;
            while ((line  = reader.readLine()) != null) {
                resultException.append(line);
            }
        }

        assertEquals("cat: WrongName.txt does not exist", resultException.toString());
    }

    @Test
    public void testPiping() throws IOException
    {
        String testString = "head " + tempFileName + " | cat";
        assertEquals(text, getOutputString(testString));
    }

    @Test
    public void testIOInput() throws IOException
    {
        try{
            String testString = "cat < tail " + tempFileName;
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "tail does not exist");
        }
    }

    @Test
    public void testIOOutput() throws IOException
    {
        createTempFile();
        String testString = "cat " + tempFileName +  " > " + getTempFileName();
        getOutputString(testString);
        String newText = getOutputString("cat " + getTempFileName());
        assertEquals(text, newText);
    }

    @Test
    public void testIOOutputAppend() throws IOException
    {
        createTempFile();
        String fileText = "maybe this duck is" + lineSeparator + "AWESOME";
        FileWriter writer = getTempFileWriter();
        writer.write(fileText);
        writer.flush();
        writer.close();
        String testString = "cat " + tempFileName +  " >> " + getTempFileName();
        getOutputString(testString);
        String newText = getOutputString("cat " + getTempFileName());
        assertEquals(fileText + text, newText);
    }

}
