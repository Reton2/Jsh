package uk.ac.ucl.jsh.main;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void mainTest() {
        new Main();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Main.main(new String[]{"-c", "echo foo;"});
        assertEquals("foo" + System.lineSeparator(), new String(out.toByteArray()));
    }

}
