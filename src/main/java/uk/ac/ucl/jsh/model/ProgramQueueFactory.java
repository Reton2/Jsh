package uk.ac.ucl.jsh.model;

import uk.ac.ucl.jsh.model.programs.Program;
import uk.ac.ucl.jsh.model.programs.ProgramFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ProgramQueueFactory {

    private ArrayList<String> appArgs;
    private InputStream startInputStream;
    private OutputStream finalOutputStream;
    private Model model;
    private OutputStream stdErrStream;

    public ProgramQueue getQueue(Model model) throws IOException
    {
        this.model = model;
        ProgramQueue queue = new ProgramQueue();
        setStartInput();
        setFinalOutput();
        setStdError();
        addPrograms(queue);
        setStdInputOut(queue);
        return queue;
    }

    private void setStdInputOut(ProgramQueue queue)
    {
        queue.setStartInput(startInputStream);
        queue.setFinalOutput(finalOutputStream);
    }

    private void setStartInput() throws IOException
    {
        String fileName;
        int inputIndex = getAppArgs().indexOf("<");
        if (inputIndex >= 0) {
            fileName = getAppArgs().get(inputIndex + 1);
            Path cur = Paths.get(model.getCurrentDirectory());
            if (fileExists(fileName)) {
                startInputStream = Files.newInputStream(cur.resolve(fileName));
                getAppArgs().remove(inputIndex);
                getAppArgs().remove(inputIndex);
            } else
                throw new RuntimeException(fileName + " does not exist"); // Handle this
            if (getAppArgs().indexOf("<") >= 0) throw new RuntimeException("only one file can be redirected as input");
        }
    }

    private void setFinalOutput() throws IOException
    {
        String fileName;
        String outputSymbol = getAppArgs().contains(">") ? ">" : ">>";
        int outIndex = getAppArgs().indexOf(outputSymbol);
        if (outIndex >= 0) {
            Path cur = Paths.get(model.getCurrentDirectory());
            fileName = getAppArgs().get(outIndex + 1);
            if (!fileExists(fileName)) {
                Files.createFile(cur.resolve(fileName));
            }
            finalOutputStream = new FileOutputStream(cur.resolve(fileName).toFile(), outputSymbol.equals(">>"));
            getAppArgs().remove(outIndex);
            getAppArgs().remove(outIndex);
            if (getAppArgs().indexOf(outputSymbol) >= 0) throw new RuntimeException("only one file can be redirected as output");
        }
    }

    private void setStdError() throws IOException
    {
        String fileName;
        int outIndex = getAppArgs().indexOf("2>");
        if (outIndex >= 0) {
            Path cur = Paths.get(model.getCurrentDirectory());
            fileName = getAppArgs().get(outIndex + 1);
            if (!fileExists(fileName)) {
                Files.createFile(cur.resolve(fileName));
            }
            stdErrStream = Files.newOutputStream(cur.resolve(fileName));
            getAppArgs().remove(outIndex);
            getAppArgs().remove(outIndex);
            if (getAppArgs().indexOf("2>") >= 0) throw new RuntimeException("only one file can be redirected as std error");
        }
    }

    private boolean fileExists(String fileName)
    {
        Path cur = Paths.get(model.getCurrentDirectory());
        return cur.resolve(fileName).toFile().exists();
    }

    private void addPrograms(ProgramQueue queue) throws IOException
    {
        PipedOutputStream pipedOutputStream = null;
        PipedInputStream pipedInputStream = null;
        boolean finished = false;
        ArrayList<String> args = getAppArgs();

        while (!finished)
        {
            pipedOutputStream = new PipedOutputStream();
            int pipeAt = args.indexOf("|");
            if (pipeAt < 0) {
                pipeAt = args.size();
                finished = true;
            }
            ArrayList<String> programArgs = new ArrayList<>(args.subList(0, pipeAt));
            if (!finished) args = new ArrayList<>(args.subList(pipeAt + 1, args.size()));
            queue.offer(getProgram(programArgs, pipedOutputStream, pipedInputStream));
            pipedInputStream = new PipedInputStream(pipedOutputStream);
        }
    }

    private Program getProgram(ArrayList<String> args, OutputStream out, InputStream in)
    {
        String programName = args.get(0);
        ArrayList<String> input = new ArrayList<>(args.subList(1, args.size()));

        ProgramFactory pf = ProgramFactory.getInstance();
        if (!pf.isProgram(programName))
            throw new RuntimeException(programName + ": unknown application");
        pf.setInput(input);
        pf.setOutput(out);
        pf.setInputStream(in);
        pf.setModel(model);
        pf.setStdErr(stdErrStream);
        return pf.getProgram();
    }

    private ArrayList<String> getAppArgs() {
        return appArgs;
    }

    public void setFinalOutputStream(OutputStream finalOutputStream) {
        this.finalOutputStream = finalOutputStream;
    }

    public void setStartInputStream(InputStream startInputStream) {
        this.startInputStream = startInputStream;
    }

    public void setAppArgs(ArrayList<String> args) {
        this.appArgs = args;
    }
}
