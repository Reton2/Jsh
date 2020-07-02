package uk.ac.ucl.jsh.model.programs;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class CopyTest extends JshTest {

    @Before
    public void init() throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Files.createDirectory(currentDir.resolve("targetDir"));
        Path dir = Files.createDirectory(currentDir.resolve("dir"));
        writeToFile(Files.createFile(dir.resolve("file1.txt")));
        writeToFile(Files.createFile(dir.resolve("file2.txt")));
        Path dir2 = Files.createDirectory(currentDir.resolve("dir2"));
        writeToFile(Files.createFile(dir2.resolve("file1.txt")));
        writeToFile(Files.createFile(dir2.resolve("file2.txt")));
        Path dir3 = Files.createDirectory(dir2.resolve("dir3"));
        writeToFile(Files.createFile(dir3.resolve("file1.txt")));
        writeToFile(Files.createFile(dir3.resolve("file2.txt")));
        writeToFile(Files.createFile(currentDir.resolve("file3.txt")));
    }

    @Test
    public void basicTest() throws IOException {
        String output = getOutputString("cp file3.txt file4.txt");
        assertEquals(getStringFromReader("file3.txt"), getStringFromReader("file4.txt"));
        assertEquals("", output);
        deleteFile("file4.txt");
    }

    @Test
    public void dirTest() throws IOException {
        String output = getOutputString("cp dir2 file4");
        assertEquals("", output);
    }

    @Test
    public void alreadyExists() throws IOException {
        String output = getOutputString("cp -v dir2/* dir2/dir3");
        ArrayList<String> out = new ArrayList<>(List.of(output.split(lineSeparator)));
        Collections.sort(out);
        String expected = "cp: dir2/dir3 is a directory: could not be copied" + lineSeparator +
                "cp: dir2"+File.separator+"dir3"+File.separator+"file1.txt already exists" + lineSeparator +
                "cp: dir2"+File.separator+"dir3"+File.separator+"file2.txt already exists";
        ArrayList<String> exp = new ArrayList<>(List.of(expected.split(lineSeparator)));
        Collections.sort(exp);
        assertEquals(exp, out);
    }

    @Test
    public void alreadyExistsNonVerbose() throws IOException {
        String output = getOutputString("cp dir2/* dir2/dir3");
        assertEquals("", output);
    }

    @Test
    public void invalidArgs() throws IOException {
        String output = getOutputString("_cp -r dir2/* dir2/dir3");
        String expected = "cp: invalid args";
        assertEquals(expected, output);
    }

    @Test
    public void invalidOptions() throws IOException {
        String output = getOutputString("_cp -ra dir2/* dir2/dir3");
        String expected = "cp: -ra is not a valid option";
        assertEquals(expected, output);
    }

    @Test
    public void missingArgs() throws IOException {
        String output = getOutputString("_cp");
        String expected = "cp: operands missing";
        assertEquals(expected, output);
    }

    @Test
    public void fewArgs() throws IOException {
        String output = getOutputString("_cp fal.txt");
        String expected = "cp: too few operands";
        assertEquals(expected, output);
    }

    @Test
    public void notExists() throws IOException {
        String output = getOutputString("cp dir2.txt targetDir");
        assertEquals("", output);
    }

    @Test
    public void notExistsLarge() throws IOException {
        String output = getOutputString("cp -v file32.txt file41.txt targetDir");
        String expected = "cp: file32.txt does not exist" + lineSeparator +
                "cp: file41.txt does not exist";
        assertEquals(expected, output);
    }

    @Test
    public void alreadyExistsTest() throws IOException {
        String output = getOutputString("cp -v dir2/file1.txt dir2/file2.txt");
        assertEquals("cp: dir2" + File.separator + "file2.txt already exists", output);
    }

    @Test
    public void basicVerboseTest() throws IOException {
        String output = getOutputString("cp -v file3.txt file4.txt");
        assertEquals(getStringFromReader("file3.txt"), getStringFromReader("file4.txt"));
        assertEquals("cp: file3.txt successfully copied", output);
        deleteFile("file4.txt");
    }

    @Test
    public void multipleVerboseTest() throws IOException {
        String output = getOutputString("cp -v dir/file1.txt dir/file2.txt targetDir");
        assertEquals(getStringFromReader("dir/file1.txt"), getStringFromReader("targetDir/file1.txt"));
        assertEquals(getStringFromReader("dir/file2.txt"), getStringFromReader("targetDir/file2.txt"));
        String expected = "cp: dir/file1.txt successfully copied" + lineSeparator +
                "cp: dir/file2.txt successfully copied";
        deleteFile("targetDir/file1.txt");
        deleteFile("targetDir/file2.txt");
        assertEquals(expected, output);
    }

    @Test
    public void multipleVerboseWithGlobTest() throws IOException {
        String output = getOutputString("cp -v dir/* targetDir");
        assertEquals(getStringFromReader("dir/file1.txt"), getStringFromReader("targetDir/file1.txt"));
        assertEquals(getStringFromReader("dir/file2.txt"), getStringFromReader("targetDir/file2.txt"));
        String expected = "cp: dir/file1.txt successfully copied" + lineSeparator +
                "cp: dir/file2.txt successfully copied";
        String expected2 = "cp: dir/file2.txt successfully copied"+ lineSeparator +"cp: dir/file1.txt successfully copied";
        deleteFile("targetDir/file1.txt");
        deleteFile("targetDir/file2.txt");
        assertTrue(expected.equals(output) || expected2.equals(output));
    }

    @Test
    public void recursiveTest() throws IOException {
        String output = getOutputString("cp -vr dir2 targetDir");
        assertEquals(getStringFromReader("dir2/file1.txt"), getStringFromReader("targetDir/file1.txt"));
        assertEquals(getStringFromReader("dir2/file2.txt"), getStringFromReader("targetDir/file2.txt"));
        assertEquals(getStringFromReader("dir2/dir3/file1.txt"), getStringFromReader("targetDir/dir3/file1.txt"));
        assertEquals(getStringFromReader("dir2/dir3/file2.txt"), getStringFromReader("targetDir/dir3/file2.txt"));
        String expected = "cp: dir2 successfully copied";
        deleteFile("targetDir/file1.txt");
        deleteFile("targetDir/file2.txt");
        deleteFile("targetDir/dir3/file1.txt");
        deleteFile("targetDir/dir3/file2.txt");
        deleteFile("targetDir/dir3");
        assertEquals(expected, output);
    }


    @After
    public void delete() throws IOException {
        deleteFile("dir/file1.txt");
        deleteFile("dir/file2.txt");

        deleteFile("dir2/file1.txt");
        deleteFile("dir2/file2.txt");

        deleteFile("dir2/dir3/file1.txt");
        deleteFile("dir2/dir3/file2.txt");

        deleteFile("file3.txt");
        deleteFile("targetDir");
        deleteFile("dir");
        deleteFile("dir2/dir3");
        deleteFile("dir2");
    }

    private String getStringFromReader(@NotNull String pathName) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir")).resolve(pathName);

        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        while ((line = reader.readLine()) != null) {
            builder.append(line).append(System.getProperty("line.separator"));
        }
        reader.close();
        return builder.toString();
    }

    private void writeToFile(Path path) throws IOException {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 1; i < random.nextInt() % 10; i++) {
            builder.append(random.nextInt()).append(System.lineSeparator());
        }
        FileWriter writer = new FileWriter(path.toFile());
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    private void deleteFile(String pathName) throws IOException {
        Files.deleteIfExists((Paths.get(System.getProperty("user.dir")).resolve(pathName)));
    }

    private boolean same(String file1, String file2) throws IOException {
        Path path1 = Paths.get(System.getProperty("user.dir")).resolve(file1);
        Path path2 = Paths.get(System.getProperty("user.dir")).resolve(file2);
        return Files.isSameFile(path1, path2);
    }

    private boolean checkIfExists(String pathName) {
        return Files.exists(Paths.get(System.getProperty("user.dir")).resolve(pathName));
    }

}
