package uk.ac.ucl.jsh.model.programs;

import org.jetbrains.annotations.NotNull;
import uk.ac.ucl.jsh.model.Model;
import uk.ac.ucl.jsh.model.programs.utils.ShellInputReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class AbstractProgram implements Program {

    private Model model;
    private ArrayList<String> appArgs;
    private OutputStream out;
    private OutputStream stdErr;
    private OutputStreamWriter writer;
    private InputStream in;
    private boolean unsafe;

    private OutputStream getOutput()
    {
        return out;
    }

    void writeToStdErr(String error) throws IOException {
        if (unsafe) {
            getWriter().write(error + System.lineSeparator());
            getWriter().flush();
        } else if (getStdErr() != null) {
            OutputStreamWriter writer = new OutputStreamWriter(getStdErr());
            writer.write(error + System.lineSeparator());
            writer.flush();
        } else {
            throw new RuntimeException(error);
        }
    }

    private OutputStream getStdErr() {
        return stdErr;
    }

    Model getModel()
    {
        return model;
    }

    ArrayList<String> getAppArgs()
    {
        return appArgs;
    }

    OutputStreamWriter getWriter()
    {
        return writer;
    }

    String getCurrentDirectory()
    {
        return getModel().getCurrentDirectory();
    }

    BufferedReader getReader(String pathString) throws ProgramException, IOException
    {
        Path filePath = Paths.get(fileGetCanonicalPath(pathString));
        return getReader(filePath);
    }

    BufferedReader getReader(@NotNull Path filePath) throws ProgramException, IOException {
        Charset encoding = StandardCharsets.UTF_8;
        BufferedReader reader = null;
        File file = filePath.toFile();
        if (!file.exists()) throw new ProgramException(filePath.getFileName() + " does not exist");
        if (file.isDirectory()) throw new ProgramException(filePath.getFileName() + " is a directory");
        else {
            reader = Files.newBufferedReader(filePath, encoding);
        }
        return reader;
    }

    String fileGetCanonicalPath(String token) throws IOException
    {
        Path path = Paths.get(getCurrentDirectory());
        File dir = path.resolve(token).toFile();
        if (!dir.exists()) {
            dir = Paths.get(token).toFile();
        }
        if (dir.exists()) {
            return dir.getCanonicalPath();
        }
        return token;
    }

    BufferedReader getInputReader()
    {
        Charset encoding = StandardCharsets.UTF_8;
        return new ShellInputReader(new InputStreamReader(in, encoding));
    }

    String getStringFromReader(@NotNull BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line).append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    void setWriter() {
        this.writer = new OutputStreamWriter(getOutput());
    }

    void write(String line) throws IOException
    {
        getWriter().write(String.valueOf(line));
        getWriter().write(System.getProperty("line.separator"));
        getWriter().flush();
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void setAppArgs(ArrayList<String> appArgs) {
        this.appArgs = appArgs;
    }

    @Override
    public void setOutput(OutputStream out) {
        this.out = out;
    }

    @Override
    public void setInput(InputStream in) {
        this.in = in;
    }

    @Override
    public void closeOutput() throws IOException {
        if (!model.getStdOutput().equals(out)) out.close();
    }

    public void setStdErr(OutputStream stdErr) {
        this.stdErr = stdErr;
    }

    @Override
    public void setUnsafe(boolean unsafe) {
        this.unsafe = unsafe;
    }
}
