package uk.ac.ucl.jsh.model.programs;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;
import uk.ac.ucl.jsh.model.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class ListTest extends JshTest {

    private String testString = "ls";
    private String tempDirName;
    private String tempFileName;

    @Before
    public void init() throws IOException{
        createTempDir();
        this.tempDirName = getTempDirName();

        createTempFile(getTempDir());
        this.tempFileName = getTempFileName();
    }

    @Test
    public void testBasic() throws IOException{
        Model postCD = getPostActionModel("cd " + tempDirName);
        String result = getOutputString(testString, postCD);
        assertEquals(result, tempFileName);
    }

    @Test
    public void testDirectoryArgument() throws IOException{
        String result = getOutputString(testString + " " + tempDirName);
        assertEquals(result, tempFileName);
    }

    @Test
    public void testDirectoryAccessTimeStamp() throws IOException{
        String result = getOutputString(testString + " -l " + tempDirName);
        assertEquals(result,
                new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(tempFileAccessOffSetTime())
                + "\t" + tempFileName);
    }

    @Test
    public void testFileAccessTimeStamp() throws IOException{
        String result = getOutputString(testString + " -l " + tempFileName, getPostActionModel("cd " + tempDirName));
        assertEquals(result,
                new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(tempFileAccessOffSetTime()) + "\t" + tempFileName);
    }

    @Test
    public void testCurrDirAccessTimeStamp() throws IOException{
        Files.createTempFile(getTempDir(), ".", "hidden");
        String result = getOutputString(testString + " -l", getPostActionModel("cd " + tempDirName));
        assertEquals(result,
                new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(tempFileAccessOffSetTime()) + "\t" + tempFileName);
    }

    @Test
    public void emptyDir() throws IOException {
        createTempDir();
        this.tempDirName = getTempDirName();
        File hidden = getTempDir().resolve(".hidden").toFile();
        if (!hidden.exists()) hidden.createNewFile();
        String result = getOutputString(testString + " " + tempDirName);
        assertEquals(result, "");
        hidden.delete();
    }

    @Test()
    public void testTooManyArgs() throws IOException{
        try {
            String result = getOutputString(testString + " " + tempDirName + " extraArgument");
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "ls: Too many args");
        }
    }

    @Test()
    public void testTooManyArgsWithOption() throws IOException{
        try {
            String result = getOutputString(testString + " -l " + tempDirName + " extraArgument");
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "ls: Too many args");
        }
    }

    @Test()
    public void testInvalidDirectory() throws IOException{
        try {
            String result = getOutputString(testString + " " + "randomFakeDirectory");
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "ls: no such directory");
        }
    }

}


