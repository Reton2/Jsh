package uk.ac.ucl.jsh.model.programs;

import java.io.*;

public class Concatenate extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "cat";

    @Override
    public void execute() throws IOException {
        try {
            setWriter();
            boolean argsProvided = checkAppArgs();
            if (argsProvided)
                writeFiles();
            else
                writeFromRedirection();
        } catch (IOException | ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void writeFromRedirection() throws IOException
    {
        writeFromReader(getInputReader());
    }

    private void writeFromReader(BufferedReader reader) throws IOException
    {
        String line;
        while ((line = reader.readLine()) != null)
            write(line);
    }

    private void writeFiles() throws ProgramException, IOException
    {
        for (int i = 0; i < getAppArgs().size(); i++) {
            String arg = getAppArgs().get(i);
            writeFileFromPath(arg);
        }
    }

    private void writeFileFromPath(String pathString) throws ProgramException, IOException
    {
        BufferedReader reader = getReader(pathString);
        writeFromReader(reader);
    }

    private boolean checkAppArgs()
    {
        boolean bool = getAppArgs().isEmpty();
        return !(bool);
    }

}
