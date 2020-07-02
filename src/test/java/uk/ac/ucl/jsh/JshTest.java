package uk.ac.ucl.jsh;

import uk.ac.ucl.jsh.model.Model;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Scanner;

public abstract class JshTest {

    protected static String preDir = System.getProperty("user.dir");
    protected static String lineSeparator = System.lineSeparator();

    private File tempFile;
    private Path tempDir;
    private FileWriter tempFileWriter;

    protected static String getOutputString(String testString) throws IOException
    {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        new Model().eval(testString, out);
        out.close();
        Scanner scn = new Scanner(in);
        StringBuilder builder = new StringBuilder();
        while (scn.hasNextLine()) builder.append(scn.nextLine()).append(System.getProperty("line.separator"));
        return builder.toString().trim();
    }

    protected static Model getPostActionModel(String testString) throws IOException
    {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        Model model = new Model();
        model.eval(testString, out);
        return model;
    }

    protected static String getOutputString(String testString, Model model) throws IOException
    {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        model.eval(testString, out);
        Scanner scn = new Scanner(in);
        out.close();
        StringBuilder builder = new StringBuilder();
        while (scn.hasNextLine()) builder.append(scn.nextLine()).append(System.getProperty("line.separator"));
        return builder.toString().trim();
    }

    protected void createTempFile() throws IOException
    {
        createTempFile(Paths.get(preDir));
    }

    protected long tempFileAccessOffSetTime() throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
        return attrs.lastAccessTime().toMillis();
    }

    protected long tempFileModifiedOffSetTime() throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(tempFile.toPath(), BasicFileAttributes.class);
        return attrs.lastModifiedTime().toMillis();
    }

    protected void createTempDir() throws IOException
    {
        Path rootDirectory = FileSystems.getDefault().getPath(preDir);
        Path tempDirectory = Files.createTempDirectory(rootDirectory, "TempDirectory");
        this.tempDir = tempDirectory;

        tempDirectory.toFile().deleteOnExit();
    }

    protected void createTempFile(Path p) throws IOException
    {//182 142
        File tempFile = File.createTempFile("temps", ".txt", p.toFile());
        Files.setAttribute(tempFile.toPath(), "lastAccessTime",
                FileTime.fromMillis(System.currentTimeMillis() - 200000), LinkOption.NOFOLLOW_LINKS);
        Files.setAttribute(tempFile.toPath(), "lastModifiedTime",
                FileTime.fromMillis(System.currentTimeMillis() - 200000), LinkOption.NOFOLLOW_LINKS);
        this.tempFile = tempFile;
        tempFile.deleteOnExit();
        tempFileWriter = new FileWriter(tempFile);
    }

    protected String getTempDirName()
    {
        return tempDir.toFile().getName();
    }

    protected Path getTempDir()
    {
        return tempDir;
    }

    protected void writeToFile(FileWriter writer, String text) throws IOException
    {
        writer.write(text);
        writer.flush();
        writer.close();
    }

    protected FileWriter getTempFileWriter()
    {
        return tempFileWriter;
    }

    protected String getTempFileName()
    {
        return tempFile.getName();
    }

}