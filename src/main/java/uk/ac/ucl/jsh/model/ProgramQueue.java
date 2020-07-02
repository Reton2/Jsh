package uk.ac.ucl.jsh.model;

import uk.ac.ucl.jsh.model.programs.Program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

public class ProgramQueue extends LinkedList<Program> {

    public void setStartInput(InputStream in) {
        getFirst().setInput(in);
    }

    public void setFinalOutput(OutputStream out) {
        getLast().setOutput(out);
    }

    public void runCommands() throws IOException
    {
        Program current;
        while ((current = poll()) != null) {
            current.execute();
            current.closeOutput();
        }
    }

}
