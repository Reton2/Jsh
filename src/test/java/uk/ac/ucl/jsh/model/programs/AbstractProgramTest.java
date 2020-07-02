package uk.ac.ucl.jsh.model.programs;

import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class AbstractProgramTest extends JshTest {

    @Test
    public void test() throws IOException, ProgramException {
        File file = Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("test.txt")).toFile();
        FileWriter writer = new FileWriter(file);
        writer.write("*.py");
        writer.flush();
        writer.close();
        AbstractProgram program = new Concatenate();
        BufferedReader reader = program.getReader(file.toPath());
        assertEquals("*.py", reader.readLine());
        file.delete();
    }

    @Test
    public void test2() throws IOException {
        File file = Files.createFile(Paths.get(System.getProperty("user.dir")).resolve("test3.txt")).toFile();
        FileWriter writer = new FileWriter(file);
        writer.write("*.py");
        writer.flush();
        writer.close();
        file.delete();
        AbstractProgram program = new Concatenate();
        try {
            BufferedReader reader = program.getReader(file.toPath());
        } catch (ProgramException e) {
            assertEquals("test3.txt does not exist", e.getMessage());
        }
    }

    @Test
    public void test3() throws IOException {
        File file = Files.createDirectory(Paths.get(System.getProperty("user.dir")).resolve("test3")).toFile();
        AbstractProgram program = new Concatenate();
        try {
            BufferedReader reader = program.getReader(file.toPath());
        } catch (ProgramException e) {
            assertEquals("test3 is a directory", e.getMessage());
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }

}
