package uk.ac.ucl.jsh.model.programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalRegexPrint extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "grep";

    private static final int PATTERN_INDEX = 0;

    @Override
    public void execute() throws IOException
    {
        try {
            setWriter();
            boolean argsProvided = checkArgs();
            if (argsProvided)
                writeFiles();
            else
                writeFromRedirection();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void writeFromRedirection() throws IOException {
        BufferedReader reader = getInputReader();
        writeFileFromReader(reader);
    }

    private void writeFiles() throws ProgramException, IOException
    {
        Path[] filePathArray;
        filePathArray = getFilePathArray();
        for (Path path : filePathArray) {
            BufferedReader reader = getReader(path);
            writeFileFromReader(reader);
        }
    }

    private void writeFileFromReader(BufferedReader reader) throws IOException
    {
        Pattern grepPattern = Pattern.compile(getAppArgs().get(PATTERN_INDEX));
        String line = null;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = grepPattern.matcher(line);
            if (matcher.find()) write(line);
        }
    }

    private boolean checkArgs() throws ProgramException
    {
        if (getAppArgs().isEmpty())
            throw new ProgramException("wrong number of arguments");
        return getAppArgs().size() != 1;
    }

    private Path[] getFilePathArray()
    {
        ArrayList<String> args = getPathArgs();
        int numOfFiles = getAppArgs().size() - 1;
        Path[] filePathArray = new Path[numOfFiles];
        Path currentDir = Paths.get(getCurrentDirectory());
        for (int i = 0; i < numOfFiles; i++)
            filePathArray[i] = currentDir.resolve(args.get(i));
        return filePathArray;
    }

    private ArrayList<String> getPathArgs()
    {
        return new ArrayList<>(getAppArgs().subList(1, getAppArgs().size()));
    }


}
