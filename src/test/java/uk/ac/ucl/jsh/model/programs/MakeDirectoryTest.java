package uk.ac.ucl.jsh.model.programs;

import org.junit.After;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MakeDirectoryTest extends JshTest {

    @Test
    public void basicTest() throws IOException {
        String output = getOutputString("mkdir stuff");
        assertTrue(checkIfExists("stuff"));
        assertEquals("", output);
        deleteDir("stuff");
    }

    @Test
    public void basicWithParentsTest() throws IOException {
        String output = getOutputString("mkdir -p more/stuff");
        assertTrue(checkIfExists("more/stuff"));
        assertEquals("", output);
        deleteDir("more/stuff");
    }

    @Test
    public void inDirMakeDirTest() throws IOException {
        getOutputString("mkdir more");
        String output = getOutputString("mkdir more/stuff");
        assertTrue(checkIfExists("more/stuff"));
        assertEquals("", output);
        deleteDir("more/stuff");
        deleteDir("more");
    }

    @Test
    public void alreadyExistsTestNoParent() throws IOException {
        getOutputString("mkdir stuff");
        String output = getOutputString("mkdir -v stuff");
        assertTrue(checkIfExists("stuff"));
        assertEquals("mkdir: stuff already exists", output);
        deleteDir("stuff");
    }

    @Test
    public void alreadyExistsTestWithParent() throws IOException {
        getOutputString("mkdir -p more/stuff");
        String output = getOutputString("mkdir -pv more/stuff");
        assertTrue(checkIfExists("more/stuff"));
        assertEquals("mkdir: more/stuff already exists", output);
        deleteDir("more/stuff");
        deleteDir("more");
    }

    @Test
    public void createParentDoesNotExistTest() throws IOException {
        String output = getOutputString("mkdir -v more/stuff");
        assertEquals("mkdir: more/stuff: no such file or directory", output);
    }

    @Test
    public void emptyArgsTest() throws IOException {
        String output = getOutputString("_mkdir");
        assertEquals("mkdir: missing operand", output);
    }

    @Test
    public void invalidOptionTest() throws IOException {
        String output = getOutputString("_mkdir -a more/stuff");
        assertEquals("mkdir: -a is not a valid option", output);
    }

    private void deleteDir(String pathName) throws IOException {
        Files.deleteIfExists(Paths.get(System.getProperty("user.dir")).resolve(pathName));
    }

    private boolean checkIfExists(String pathName) {
        return Files.exists(Paths.get(System.getProperty("user.dir")).resolve(pathName));
    }

    @After
    public void delete() throws IOException {
        deleteDir("more/stuff");
        deleteDir("more");
    }

}
