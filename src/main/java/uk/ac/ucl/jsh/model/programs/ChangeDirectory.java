package uk.ac.ucl.jsh.model.programs;

import java.io.*;

public class ChangeDirectory extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "cd";

    @Override
    public void execute() throws IOException
    {
        try {
            checkArgs();
            String dirString = getAppArgs().get(0);
            File dir = getDirIfExists(dirString);
            setModelCurrentDirectory(dir);
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void setModelCurrentDirectory(File dir) throws IOException
    {
        getModel().setCurrentDirectory(dir.getCanonicalPath());
    }

    private File getDirIfExists(String dirString) throws ProgramException, IOException
    {
        File dir = new File(fileGetCanonicalPath(dirString));
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ProgramException(dirString + " is not an existing directory");
        }
        return dir;
    }

    private void checkArgs() throws ProgramException
    {
        if (getAppArgs().isEmpty()) {
            throw new ProgramException("missing argument");
        } else if (getAppArgs().size() > 1) {
            throw new ProgramException("too many arguments");
        }
    }

}
