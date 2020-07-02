package uk.ac.ucl.jsh.model.programs;

import org.junit.*;
import uk.ac.ucl.jsh.JshTest;
import uk.ac.ucl.jsh.model.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ChangeDirectoryTest extends JshTest {


    private String testString = "cd";
    private String currDirectory = System.getProperty("user.dir");
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

        String result = getOutputString("pwd", postCD);
        String expected = currDirectory + "/" + tempDirName;
        assertEquals(Paths.get(result), Paths.get(expected));
    }

    @Test()
    public void testFakeDirectory() throws IOException{
        try {
            String result = getOutputString(testString + " fakeRandomDirectory");
        } catch (java.lang.RuntimeException e){
            assertEquals(e.getMessage(), "cd: fakeRandomDirectory is not an existing directory");
        }

    }

    @Test()
    public void testNotDirectory() throws IOException{
        try {
            Model postCD = getPostActionModel("cd " + tempDirName);
            String result = getOutputString(testString + " " + tempFileName, postCD);
        } catch (java.lang.RuntimeException e){
            String tempFileName = getTempFileName();
            assertEquals(e.getMessage(), "cd: " + tempFileName + " is not an existing directory");
        }
    }

    @Test()
    public void testNotEnoughArgs() throws IOException{
        try {
            String result = getOutputString(testString);
        } catch (java.lang.RuntimeException e){
            Assert.assertEquals(e.getMessage(), "cd: missing argument");
        }

    }

    @Test()
    public void testTooManyArgs() throws IOException{
        try {
            String result = getOutputString(testString + " " + "arg1" + " " + "arg2");
        } catch (java.lang.RuntimeException e){
            Assert.assertEquals(e.getMessage(), "cd: too many arguments");

        }

    }

}
