package uk.ac.ucl.jsh.model.programs.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ShellInputReader extends BufferedReader {

    private static final String END_STRING = "!:q";

    public ShellInputReader(@NotNull Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException
    {
        String line = super.readLine();
        if (line == null || line.equals(END_STRING))
            return null;
        else
            return line;
    }

}
