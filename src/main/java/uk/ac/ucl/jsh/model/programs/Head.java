package uk.ac.ucl.jsh.model.programs;

import java.io.*;
import java.util.ArrayList;

public class Head extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "head";

    private static final int OPTION_INDEX = 0;
    private static final int NUM_INDEX = 1;
    private static final int FILE_INDEX_WITH_OPTION = 2;
    private static final int FILE_INDEX = 0;
    private static final int DEFAULT_LINES = 10;

    private boolean nOption;

    //Make exception case for StdIn

    @Override
    public void execute() throws IOException {
        try {
            setWriter();
            if(checkArgs()) {
                start(getFileReader());
            } else {
                writeFromRedirection();
            }
        } catch (ProgramException e) {
            writeToStdErr( PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private BufferedReader getFileReader() throws ProgramException, IOException {
        int index = nOption? FILE_INDEX_WITH_OPTION :FILE_INDEX;
        String file = getAppArgs().get(index);
        return getReader(file);
    }

    private void start(BufferedReader reader) throws IOException {
        int lines;
        lines = nOption? Integer.parseInt(getAppArgs().get(NUM_INDEX)) :DEFAULT_LINES;
        writeToShell(reader, lines);
    }

    private void writeFromRedirection() throws IOException {
        start(getInputReader());
    }

    private void writeToShell(BufferedReader reader, int headLines) throws IOException {
        ArrayList<String> storage = readerToArrayList(reader);
        if (storage.size() < headLines) {headLines = storage.size();}
        for (int i = 0; i < headLines; i++) {
            getWriter().write(storage.get(i) + System.getProperty("line.separator"));
            getWriter().flush();
        }
    }

    private ArrayList<String> readerToArrayList(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> storage = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            storage.add(line);
        }
        return storage;
    }

    private boolean checkArgs() throws ProgramException {
        if (getAppArgs().isEmpty()) {
            return false;
        }
        if (getAppArgs().size() > 1 && !getAppArgs().get(OPTION_INDEX).equals("-n"))
            throw new ProgramException(getAppArgs().get(OPTION_INDEX) + " is not an option");
        nOption = getAppArgs().get(OPTION_INDEX).equals("-n");
        if (nOption) {
            checkIfInteger();
            return getAppArgs().size() > 2;
        }
        return true;
    }

    private void checkIfInteger() throws ProgramException
    {
        try {
            Integer.parseInt(getAppArgs().get(NUM_INDEX));
        } catch (NumberFormatException e) {
            throw new ProgramException("wrong arguments");
        }
    }

}