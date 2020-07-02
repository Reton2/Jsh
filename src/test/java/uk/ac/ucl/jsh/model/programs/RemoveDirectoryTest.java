package uk.ac.ucl.jsh.model.programs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class RemoveDirectoryTest extends JshTest {

    @Before
    public void init() throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Files.createDirectory(currentDir.resolve("emptyDir"));
        Path hasDirInside = Files.createDirectory(currentDir.resolve("hasInside"));
        Files.createDirectory(hasDirInside.resolve("inside"));
        Path hasFileInside = Files.createDirectory(currentDir.resolve("hasFile"));
        Files.createFile(hasFileInside.resolve("inside.txt"));
    }

    @Test
    public void basicTest() throws IOException
    {
        String output = getOutputString("rmdir emptyDir");
        assertFalse(checkIfExists("emptyDir"));
        assertEquals("", output);
    }

    @Test
    public void basicVerboseTest() throws IOException
    {
        String output = getOutputString("rmdir -v emptyDir");
        assertFalse(checkIfExists("emptyDir"));
        assertEquals("rmdir: emptyDir was successfully deleted", output);
    }

    @Test
    public void parentSingleTest() throws IOException
    {
        String output = getOutputString("rmdir -p emptyDir");
        assertFalse(checkIfExists("emptyDir"));
        assertEquals("", output);
    }

    @Test
    public void parentTest() throws IOException
    {
        String output = getOutputString("rmdir -p emptyDir");
        assertFalse(checkIfExists("emptyDir"));
        assertEquals("", output);
    }

    @Test
    public void parentTestWithInside() throws IOException
    {
        String output = getOutputString("rmdir -vp hasInside/inside");
        assertFalse(checkIfExists("hasInside"));
        assertEquals("rmdir: hasInside was successfully deleted", output);
    }

    @Test
    public void hasFile() throws IOException
    {
        String output = getOutputString("rmdir -vp hasFile");
        assertTrue(checkIfExists("hasFile"));
        assertEquals("rmdir: hasFile was not been deleted: is not empty", output);
    }

    @Test
    public void doesNotExist() throws IOException
    {
        String output = getOutputString("rmdir -v none");
        assertFalse(checkIfExists("none"));
        assertEquals("rmdir: none does not exist", output);
    }

    @Test
    public void tryFile() throws IOException
    {
        String output = getOutputString("rmdir -v hasFile/inside.txt");
        assertTrue(checkIfExists("hasFile/inside.txt"));
        assertEquals("rmdir: hasFile/inside.txt is not a directory", output);
    }

    @Test
    public void wrongOptions() throws IOException
    {
        String output = getOutputString("_rmdir -va hasFile/inside.txt");
        assertEquals("rmdir: -va is not a valid option", output);
    }

    @Test
    public void noArgs() throws IOException
    {
        String output = getOutputString("_rmdir");
        assertEquals("rmdir: missing operand", output);
    }



    @After
    public void delete() throws IOException {
        deleteFile("emptyDir");
        deleteFile("hasInside/inside");
        deleteFile("hasInside");
        deleteFile("hasFile/inside.txt");
        deleteFile("hasFile");
    }

    private void deleteFile(String pathName) throws IOException {
        Files.deleteIfExists((Paths.get(System.getProperty("user.dir")).resolve(pathName)));
    }

    private boolean checkIfExists(String pathName) {
        return Files.exists(Paths.get(System.getProperty("user.dir")).resolve(pathName));
    }

}
