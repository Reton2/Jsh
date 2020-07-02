package uk.ac.ucl.jsh.ui;

import org.junit.Test;
import uk.ac.ucl.jsh.model.Model;
import uk.ac.ucl.jsh.ui.Jsh;

import java.io.*;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

public class JshShellTest {

    @Test
    public void checkIfRunsWithArgs() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Jsh jsh = new Jsh(new Model());
        jsh.runShell(new String[]{"-c", "echo foo"});
        assertEquals("foo" + System.lineSeparator(), new String(out.toByteArray()));
    }

    @Test
    public void modelError() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Jsh jsh = new Jsh(new Model());
        jsh.runShell(new String[]{"-c", "dat foo"});
        assertEquals("", new String(out.toByteArray()));
    }

    @Test
    public void argNumberError() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Jsh jsh = new Jsh(new Model());
        jsh.runShell(new String[]{"-c"});
        assertEquals("jsh: wrong number of arguments" + System.lineSeparator(), new String(out.toByteArray()));
    }

    @Test
    public void argOptionError() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        Jsh jsh = new Jsh(new Model());
        jsh.runShell(new String[]{"-csa", ""});
        assertEquals("jsh: -csa: unexpected argument" + System.lineSeparator(), new String(out.toByteArray()));
    }

    @Test
    public void checkIfRuns() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        PipedInputStream in = new PipedInputStream();
        System.setIn(in);
        new JshThread().start();
        PipedOutputStream output = new PipedOutputStream(in);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        writer.write("echo foo");
        writer.write(System.lineSeparator());
        writer.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("foo" + System.lineSeparator(),
                new String(out.toByteArray())
                        .replace(System.getProperty("user.dir") + "> ", ""));
    }

    @Test
    public void exceptionExpected() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        PipedInputStream in = new PipedInputStream();
        System.setIn(in);
        new JshThread().start();
        PipedOutputStream output = new PipedOutputStream(in);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        writer.write("decho foo");
        writer.write(System.lineSeparator());
        writer.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("",
                new String(out.toByteArray())
                        .replace(System.getProperty("user.dir") + "> ", ""));
    }

    private class JshThread extends Thread {

        @Override
        public void run()
        {
            Jsh jsh = new Jsh(new Model());
            jsh.runShell(new String[]{});
        }

    }

}
