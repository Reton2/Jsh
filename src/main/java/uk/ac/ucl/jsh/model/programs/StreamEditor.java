package uk.ac.ucl.jsh.model.programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamEditor extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "sed";

    private static final int OPTION_INDEX = 0;
    private static final int FILE_INDEX = 1;

    @Override
    public void execute() throws IOException {
        try {
            setWriter();
            BufferedReader reader;
            if (checkAppArgs())
                reader = getReader(getAppArgs().get(FILE_INDEX));
            else
                reader = getInputReader();
            start(reader);
        } catch (ProgramException e) {
            // Make sure to throw IOExceptions for now. Make sure there is no catch clause for them in programs module
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void start(BufferedReader reader) throws ProgramException, IOException
    {
        final int maxOptions = 4;
        final int minOptions = 3;
        final int separatorIndex = 1;
        final int regexStrIndex = 1;
        final int replaceStrIndex = 2;
        final String optionSuffix = "g";
        final String optionSeparator = String.valueOf(getAppArgs().get(OPTION_INDEX).charAt(separatorIndex));
        final boolean isReplaceAll = getAppArgs().get(OPTION_INDEX).endsWith(optionSuffix);
        final String[] options = getAppArgs().get(OPTION_INDEX).split(Pattern.quote(optionSeparator));

        String line;

        if (options.length > maxOptions || options.length < minOptions)
            throw new ProgramException("incorrect number of options");

        while ((line = reader.readLine()) != null)
            replaceAndWrite(line, options[regexStrIndex], options[replaceStrIndex], isReplaceAll);
    }

    private void replaceAndWrite(String line, String regexString, String replaceString, boolean isReplaceAll) throws IOException
    {
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            line = line.replaceFirst(matcher.group(), replaceString);
            if (!isReplaceAll) break;
        }
        write(line);
    }

    private boolean checkAppArgs() throws ProgramException
    {
        final String optionFixedPrefix = "s";

        if (getAppArgs().isEmpty() || getAppArgs().size() > FILE_INDEX + 1)
            throw new ProgramException("wrong number of args");
        if (!getAppArgs().get(OPTION_INDEX).startsWith(optionFixedPrefix))
            throw new ProgramException("incorrect options");
        return getAppArgs().size() == FILE_INDEX + 1;
    }

}
