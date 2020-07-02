package uk.ac.ucl.jsh.model;

import java.io.*;
import java.util.ArrayList;

public class Model {

    private String currentDirectory;
    private OutputStream stdOutput;

    public Model()
    {
        currentDirectory = System.getProperty("user.dir");
    }

    private void runCommands(ArrayList<String> rawCommands) throws IOException {
        for (String rawCommand : rawCommands) {
            ArrayList<String> tokens = getArgs(rawCommand);

            ProgramQueueFactory pf = new ProgramQueueFactory();
            pf.setAppArgs(tokens);
            pf.setFinalOutputStream(stdOutput);
            pf.setStartInputStream(getStdInputStream());
            ProgramQueue queue = pf.getQueue(this);
            queue.runCommands();
        }
    }
    public void eval(String cmdline, OutputStream output) throws IOException {
        this.stdOutput = output;
        String subResolved = SubResolver.removeCmdSubs(cmdline, getCurrentDirectory());
        runCommands(CommandParser.parseInput(subResolved));
    }

    public ArrayList<String> getArgs(String input) throws IOException {
        return TokenGen.generateTokens(input, currentDirectory);
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public OutputStream getStdOutput() {return stdOutput;}

    public InputStream getStdInputStream() {
        return System.in;
    }
}
