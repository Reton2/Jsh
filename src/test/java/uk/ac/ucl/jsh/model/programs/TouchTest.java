package uk.ac.ucl.jsh.model.programs;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;
import uk.ac.ucl.jsh.model.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.*;

public class TouchTest extends JshTest {
    private Model model;

    @Before
    public void init() throws IOException {
        createTempDir();

        createTempFile(getTempDir());

        model = getPostActionModel("cd " + getTempDirName());
    }

    @Test
    public void testBasic() throws IOException {
        long beforeAccess = tempFileAccessOffSetTime();
        long beforeModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch " + getTempFileName(), model);
        long afterAccess = tempFileAccessOffSetTime();
        long afterModify = tempFileModifiedOffSetTime();
        assertEquals(afterAccess, beforeAccess);
        assertEquals(afterModify, beforeModify);
    }

    @Test
    public void testEmptyArgs() throws IOException {
        try {
            getOutputString("touch", model);
        } catch (Exception e) {
            assertEquals("touch: incorrect number of args", e.getMessage());
        }
    }

    @Test
    public void testWrongOptions() throws IOException {
        try {
            getOutputString("touch -ral out.txt", model);
        } catch (Exception e) {
            assertEquals("touch: incorrect options", e.getMessage());
        }
    }

    @Test
    public void testWrongArgsWithoutRead() throws IOException {
        try {
            getOutputString("touch -a", model);
        } catch (Exception e) {
            assertEquals("touch: incorrect number of args", e.getMessage());
        }
    }

    @Test
    public void testWrongArgsWithRead() throws IOException {
        try {
            getOutputString("touch -ra out.txt", model);
        } catch (Exception e) {
            assertEquals("touch: incorrect number of args", e.getMessage());
        }
    }

    @Test
    public void testWrongArgsWithReadWithOffsetOption() throws IOException {
        try {
            getOutputString("touch -ra out.txt -B 200", model);
        } catch (Exception e) {
            assertEquals("touch: incorrect number of args", e.getMessage());
        }
    }

    @Test
    public void testOffSetNotNumber() throws IOException {
        try {
            getOutputString("touch -ra out.txt -B asd out.txt", model);
        } catch (Exception e) {
            assertEquals("touch: time offset is supposed to be a long", e.getMessage());
        }
    }

    @Test
    public void testBasicCreateFile() throws IOException {
        String output = getOutputString("touch out.txt", model);
        Path path = getTempDir().resolve("out.txt");
        assertTrue(Files.exists(path));
        Files.deleteIfExists(path);
    }

    @Test
    public void testAccessTimeStamp() throws IOException {
        long beforeAccess = tempFileAccessOffSetTime();
        long beforeModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -a " + getTempFileName(), model);
        long afterAccess = tempFileAccessOffSetTime();
        long afterModify = tempFileModifiedOffSetTime();
        assertNotEquals(afterAccess, beforeAccess);
        assertEquals(afterModify, beforeModify);
    }

    @Test
    public void testModifyTimeStamp() throws IOException {
        long beforeAccess = tempFileAccessOffSetTime();
        long beforeModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -m " + getTempFileName(), model);
        long afterAccess = tempFileAccessOffSetTime();
        long afterModify = tempFileModifiedOffSetTime();
        assertEquals(afterAccess, beforeAccess);
        assertNotEquals(afterModify, beforeModify);
    }

    @Test
    public void testBothTimeStamp() throws IOException {
        long beforeAccess = tempFileAccessOffSetTime();
        long beforeModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -am " + getTempFileName(), model);
        long afterAccess = tempFileAccessOffSetTime();
        long afterModify = tempFileModifiedOffSetTime();
        assertNotEquals(afterAccess, beforeAccess);
        assertNotEquals(afterModify, beforeModify);
    }

    @Test
    public void testReadModifyTimeNoOffSetStamp() throws IOException {
        long readFileAccess = tempFileAccessOffSetTime();
        long readFileModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -rm " + getTempFileName() + " out.txt", model);
        Path path = getTempDir().resolve("out.txt");
        long afterAccess = fileAccessOffSetTime(path);
        long afterModify = fileModifiedOffSetTime(path);
        assertNotEquals(afterAccess, readFileAccess);
        assertEquals(afterModify, readFileModify);
        Files.deleteIfExists(path);
    }

    @Test
    public void testReadBothNoOffSetStamp() throws IOException {
        long readFileAccess = tempFileAccessOffSetTime();
        long readFileModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -ram " + getTempFileName() + " out.txt", model);
        Path path = getTempDir().resolve("out.txt");
        long afterAccess = fileAccessOffSetTime(path);
        long afterModify = fileModifiedOffSetTime(path);
        assertEquals(afterAccess, readFileAccess);
        assertEquals(afterModify, readFileModify);
        Files.deleteIfExists(path);
    }

    @Test
    public void testReadBothBackStamp() throws IOException {
        long readFileAccess = tempFileAccessOffSetTime();
        long readFileModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -ram " + getTempFileName() + " -B 10 out.txt", model);
        Path path = getTempDir().resolve("out.txt");
        long afterAccess = fileAccessOffSetTime(path);
        long afterModify = fileModifiedOffSetTime(path);
        assertEquals(afterAccess + 10000, readFileAccess);
        assertEquals(afterModify + 10000, readFileModify);
        Files.deleteIfExists(path);
    }

    @Test
    public void testReadBothForwardStamp() throws IOException {
        long readFileAccess = tempFileAccessOffSetTime();
        long readFileModify = tempFileModifiedOffSetTime();
        String output = getOutputString("touch -ram " + getTempFileName() + " -F 100 out.txt", model);
        Path path = getTempDir().resolve("out.txt");
        long afterAccess = fileAccessOffSetTime(path);
        long afterModify = fileModifiedOffSetTime(path);
        assertEquals(afterAccess - 100000, readFileAccess);
        assertEquals(afterModify - 100000, readFileModify);
        Files.deleteIfExists(path);
    }

    private long fileAccessOffSetTime(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return attrs.lastAccessTime().toMillis();
    }

    private long fileModifiedOffSetTime(Path path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        return attrs.lastModifiedTime().toMillis();
    }

}
