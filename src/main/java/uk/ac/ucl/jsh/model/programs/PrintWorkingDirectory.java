package uk.ac.ucl.jsh.model.programs;

import java.io.IOException;

public class PrintWorkingDirectory extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "pwd";

    public void execute() throws IOException
    {
        setWriter();
        getWriter().write(getCurrentDirectory());
        getWriter().write(System.getProperty("line.separator"));
        getWriter().flush();
    }
}
