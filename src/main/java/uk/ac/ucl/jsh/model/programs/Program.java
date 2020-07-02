package uk.ac.ucl.jsh.model.programs;

import uk.ac.ucl.jsh.model.Model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Program {

    void setModel(Model model);
    void setAppArgs(ArrayList<String> appArgs);
    void setOutput(OutputStream out);
    void setStdErr(OutputStream stdErr);
    void setUnsafe(boolean unsafe);
    void setInput(InputStream in);
    void closeOutput() throws IOException;
    void execute() throws IOException;
//    void closeWriter() throws IOException;

}
