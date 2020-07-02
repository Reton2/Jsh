package uk.ac.ucl.jsh.model.programs;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;
import uk.ac.ucl.jsh.model.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FindTest extends JshTest {

    private String currDirectory = System.getProperty("user.dir");
    private String tempDirName;
    private String tempFileName;
    private static Model model;

    @BeforeClass
    public static void makeMainDir() throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path main = currentDir.resolve("MainDirFind");
        if (!main.toFile().exists())
            Files.createDirectory(main);
        Path inner1 = main.resolve("Inner1");
        if (!inner1.toFile().exists()) Files.createDirectory(inner1);
        Path inner2 = inner1.resolve("Inner2");
        if (!inner2.toFile().exists()) Files.createDirectory(inner2);
        Files.createFile(main.resolve("test1.txt")).toFile().deleteOnExit();
        Files.createFile(main.resolve("test2.py")).toFile().deleteOnExit();
        Files.createFile(main.resolve("test3.txt")).toFile().deleteOnExit();
        Files.createFile(inner1.resolve("test4.py")).toFile().deleteOnExit();
        File file = Files.createFile(inner1.resolve("test5.txt")).toFile();
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file);
        writer.write("*.py");
        writer.flush();
        writer.close();
        inner1.toFile().deleteOnExit();
        main.toFile().deleteOnExit();
        model = getPostActionModel("cd MainDirFind");
    }

    @Before
    public void init() throws IOException{
        createTempDir();
        this.tempDirName = getTempDirName();

        createTempFile(getTempDir());
        this.tempFileName = getTempFileName();
    }

    @Test
    public void testBasic() throws IOException{
        String test = getOutputString("find " + tempDirName + " -name " + tempFileName);
        String expected = tempDirName + "/" + tempFileName;
        assertEquals(Paths.get(test), Paths.get(expected));
    }

    @Test
    public void testIOOutput() throws Exception {
        createTempFile();
        String test = getOutputString("find " + tempDirName + " -name " + tempFileName + " > " + getTempFileName());
        String expected = tempDirName + File.separator + tempFileName;
        getOutputString(test);
        String newText = getOutputString("cat " + getTempFileName());
        assertEquals(expected, newText);
    }

    @Test
    public void testOpArg() throws Exception {
        try {
            String test = getOutputString("find " + tempDirName + " -nae " + tempFileName);
        } catch (java.lang.RuntimeException e){
            assertEquals(e.getMessage(), "find: does not contain option arg");
        }
    }

    @Test
    public void testToManyArgs() throws Exception {
        try {
            String test = getOutputString("find " + tempDirName + " -name fatcat " + tempFileName);
        } catch (java.lang.RuntimeException e){
            assertEquals(e.getMessage(), "find: too many args");
        }
    }

    @Test
    public void BasicTest() throws IOException
    {
        String output = getOutputString("find -name *.txt", model);
        ArrayList<String> actual = new ArrayList<>(java.util.List.of(output.split("[\\s]")));
        ArrayList<String> expected = new ArrayList<>(java.util.List.of(
                "." + File.separator + "Inner1" + File.separator + "test5.txt",
                "." + File.separator + "test1.txt",
                "." + File.separator + "test3.txt"
        ));
        Collections.sort(actual);
        TestCase.assertEquals(expected, actual);
    }

    @Test
    public void BasicTestWithPath() throws IOException {
        String output = getOutputString("find Inner1 -name *.txt", model);
        ArrayList<String> actual = new ArrayList<>(java.util.List.of(output.split("[\\s]")));
        ArrayList<String> expected = new ArrayList<>(java.util.List.of(
                "Inner1" + File.separator + "test5.txt"
        ));
        TestCase.assertEquals(expected, actual);
    }

    @Test
    public void BasicTestFileAsPath() throws IOException {
        String output = getOutputString("find test1.txt -name *.txt", model);
        ArrayList<String> actual = new ArrayList<>(java.util.List.of(output.split("[\\s]")));
        actual.removeIf(s -> s.length() < 1);
        TestCase.assertEquals(0, actual.size());
    }

    @Test
    public void IORedirectionTest() throws IOException {
        String output = getOutputString("find -name < Inner1/test5.txt", model);
        ArrayList<String> actual = new ArrayList<>(java.util.List.of(output.split("[\\s]")));
        ArrayList<String> expected = new ArrayList<>(java.util.List.of(
                "." + File.separator + "Inner1" + File.separator + "test4.py",
                "." + File.separator + "test2.py"
        ));
        TestCase.assertEquals(expected, actual);
    }

    @Test
    public void EmptyDirTest() throws IOException {
        String output = getOutputString("find Inner1/Inner2 -name *", model);
        TestCase.assertEquals("", output);
    }

    @Test
    public void PipeTest() throws IOException {
        String output = getOutputString("cat Inner1/test5.txt | find -name", model);
        ArrayList<String> actual = new ArrayList<>(java.util.List.of(output.split("[\\s]")));
        ArrayList<String> expected = new ArrayList<>(List.of(
                "." + File.separator + "Inner1" + File.separator + "test4.py",
                "." + File.separator + "test2.py"
        ));
        TestCase.assertEquals(expected, actual);
    }

    @Test
    public void TooManyArgsExceptionTest() throws IOException {
        String output;
        try {
            output = getOutputString("find apple -name bee hives", model);
        } catch (RuntimeException e) {
            output = e.getMessage();
        }
        TestCase.assertEquals("find: too many args", output);
    }

    @Test
    public void OptionMissingExceptionTest() throws IOException {
        String output;
        try {
            output = getOutputString("find apple -naam hives", model);
        } catch (RuntimeException e) {
            output = e.getMessage();
        }
        TestCase.assertEquals("find: does not contain option arg", output);
    }


}
