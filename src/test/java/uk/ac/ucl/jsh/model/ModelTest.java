package uk.ac.ucl.jsh.model;

import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelTest extends JshTest {

    @Test
    public void FalseProgramTest() {
        String output;
        try {
            output = getOutputString("cad maybe");
        } catch (Exception e) {
            output = e.getMessage();
        }
        assertEquals("cad: unknown application", output);
    }

    @Test
    public void SplitTest() {
        String output;
        try {
            output = getOutputString("echo foo; echo bar");
        } catch (Exception e) {
            output = e.getMessage();
        }
        assertEquals("foo" + lineSeparator + "bar", output);
    }

    @Test
    public void IORedirectionFileDoesNotExist() {
        String output;
        try {
            output = getOutputString("cat < menuSATA.txt");
        } catch (Exception e) {
            output = e.getMessage();
        }
        assertEquals("menuSATA.txt does not exist", output);
    }

    @Test
    public void TurnOffInputStream() throws IOException {
        String output;
        File file = Paths.get(System.getProperty("user.dir")).resolve("menuSATA.txt").toFile();
        if (!file.exists())file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("halo" + lineSeparator + "!:q" + lineSeparator + "balo");
        writer.flush();
        writer.close();
        output = getOutputString("cat < menuSATA.txt");
        assertEquals("halo", output);
        file.delete();
    }

    @Test
    public void IORedirectionCreateOutputFileTest() throws IOException {
        ProgramQueueFactory pf = new ProgramQueueFactory();
        pf.setStartInputStream(System.in);
        pf.setFinalOutputStream(System.out);
        ArrayList<String> tokens = new ArrayList<>(List.of("cat", "maybe.txt", ">", "menuOut.txt", "2>", "menuErr.txt"));
        pf.setAppArgs(tokens);
        pf.getQueue(new Model());
        File menuOut = Paths.get(System.getProperty("user.dir")).resolve("menuOut.txt").toFile();
        File menuErr = Paths.get(System.getProperty("user.dir")).resolve("menuErr.txt").toFile();
        assertTrue(menuOut.exists());
        assertTrue(menuErr.exists());
        menuErr.delete();
        menuOut.delete();
    }

}
