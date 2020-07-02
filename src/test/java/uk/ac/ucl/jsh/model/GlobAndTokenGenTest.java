package uk.ac.ucl.jsh.model;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;
import static org.junit.Assert.*;

public class GlobAndTokenGenTest extends JshTest {

    @BeforeClass
    public static void init() throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path main = currentDir.resolve("MainDir");
        if (!main.toFile().exists())
            Files.createDirectory(main);
        Path inner1 = main.resolve("Inner1");
        if (!inner1.toFile().exists()) Files.createDirectory(inner1);
        Files.createFile(main.resolve(".test")).toFile().deleteOnExit();
        Files.createFile(main.resolve("test1.txt")).toFile().deleteOnExit();
        Files.createFile(main.resolve("test2.txt")).toFile().deleteOnExit();
        Files.createFile(main.resolve("test3.txt")).toFile().deleteOnExit();
        Files.createFile(inner1.resolve("test4.txt")).toFile().deleteOnExit();
        inner1.toFile().deleteOnExit();
        main.toFile().deleteOnExit();
    }

    @Test
    public void BasicTest() throws IOException {
        List<String> list = Glob.getGlobbingResult("*", System.getProperty("user.dir") + File.separator + "MainDir");
        List<String> expected = new ArrayList<>(List.of("Inner1", "test1.txt", "test2.txt", "test3.txt"));
        sort(expected);
        sort(list);
        assertEquals(expected, list);
    }

    @Test
    public void TokenExceptionUnmatchedQuote() throws IOException {
        String output = null;
        try {
            TokenGen.generateTokens("echo stuff asm\"ay\"beas", System.getProperty("user.dir"));
        } catch (Exception e) {
            output = e.getMessage();
        }
        System.out.println(output);
    }

    @Test
    public void GlobTokeGenTest() throws IOException {
        List<String> list = TokenGen.generateTokens("echo *", System.getProperty("user.dir") + File.separator + "MainDir");
        List<String> expected = new ArrayList<>(List.of("Inner1", "echo", "test1.txt", "test2.txt", "test3.txt"));
        sort(expected);
        sort(list);
        assertEquals(expected, list);
    }

    @Test
    public void InDirTest() throws IOException {
        List<String> list = Glob.getGlobbingResult("Inner1/*", System.getProperty("user.dir") + File.separator + "MainDir");
        List<String> expected = new ArrayList<>(List.of("Inner1" + File.separator + "test4.txt"));
        sort(list);
        sort(expected);
        assertEquals(expected, list);
    }

    @Test
    public void OutOfDirTest() throws IOException {
        String token = System.getProperty("user.dir").replace(File.separator, "/") + "/MainDir/*";
        List<String> list = Glob.getGlobbingResult(token, System.getProperty("user.dir") + File.separator + "Inner1");
        List<String> expected = new ArrayList<>(List.of(System.getProperty("user.dir") + File.separator + "MainDir" + File.separator + "Inner1",
                System.getProperty("user.dir") + File.separator + "MainDir" + File.separator + "test1.txt",
                System.getProperty("user.dir") + File.separator + "MainDir" + File.separator + "test2.txt",
                System.getProperty("user.dir") + File.separator + "MainDir" + File.separator + "test3.txt"));
        sort(list);
        sort(expected);
        assertEquals(expected, list);
    }

    @Test
    public void SelectiveTest() throws IOException {
        List<String> list = Glob.getGlobbingResult("*.txt", System.getProperty("user.dir") + File.separator + "MainDir");
        List<String> expected = new ArrayList<>(List.of("test1.txt", "test2.txt", "test3.txt"));
        sort(list);
        sort(expected);
        assertEquals(expected, list);
    }

    @Test
    public void ComSubDelTest() throws IOException {
        String delimiter = SubResolver.delimiter;
        String command = "echo \"a sad " + delimiter + "foo" + delimiter + "\"";
        ArrayList<String> tokens = TokenGen.generateTokens(command, System.getProperty("user.dir"));
        ArrayList<String> expected = new ArrayList<>(List.of("echo", "a sad foo"));
        assertEquals(expected, tokens);
    }

    @Test
    public void GlobInTokenGenComSubTest() throws IOException {
        String delimiter = SubResolver.delimiter;
        String command = "echo \"a " + delimiter + "*" + delimiter + "\"";
        ArrayList<String> tokens = TokenGen.generateTokens(command, System.getProperty("user.dir") + File.separator + "MainDir");
        ArrayList<String> expected = new ArrayList<>(List.of("a", "Inner1", "test1.txt", "test2.txt", "test3.txt"));
        ArrayList<String> secondTokenStuff = new ArrayList<>(List.of(tokens.get(1).split("[\\s]")));
        Collections.sort(expected);
        Collections.sort(secondTokenStuff);
        assertEquals(expected, secondTokenStuff);
    }

}

