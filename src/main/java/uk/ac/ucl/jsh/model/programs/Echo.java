package uk.ac.ucl.jsh.model.programs;

import java.io.IOException;

public class Echo extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "echo";

    public void execute() throws IOException
    {
        setWriter();
        if (printArgs()) {
            writeLine();
        }
    }

    private boolean printArgs() throws IOException // Handle this exception
    {
        boolean atLeastOnePrinted = false;
        for (String arg : getAppArgs()) {
            if (atLeastOnePrinted) getWriter().write(" ");
            getWriter().write(arg);
            getWriter().flush();
            atLeastOnePrinted = true;
        }
        return atLeastOnePrinted;
    }

    private void writeLine() throws IOException
    {
        getWriter().write(System.getProperty("line.separator"));
        getWriter().flush();
    }

}
