package uk.ac.ucl.jsh.model.programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Strings extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "strings";

    @Override
    public void execute() throws IOException {
        setWriter();
        try {
            startFunction();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private int getOptionIndex(String option) {
        int index = getAppArgs().indexOf(option);
        if (index == 0) return 0;
        else if (index == 2) return 2;
        else return -1;
    }

    private void startFunction() throws ProgramException, IOException {
        final String numberOption = "-n";
        final String typeOption = "-t";
        int minChars = 4;
        char type = '\0';
        int inputStart = 0;

        int numberOptionIndex = getOptionIndex(numberOption);
        int typeOptionIndex = getOptionIndex(typeOption);

        if (!(numberOptionIndex < 0) ) {
            final String numberRegex = "[0-9]+";
            if (getAppArgs().size() == numberOptionIndex + 1)
                throw new ProgramException("missing option input");
            if (!getAppArgs().get(numberOptionIndex + 1).matches(numberRegex))
                throw new ProgramException("number input should be positive integer");
            minChars = Integer.parseInt(getAppArgs().get(numberOptionIndex + 1));
            inputStart += 2;
        }
        if (!(typeOptionIndex < 0)) {
            final String typeRegex = "[bdxo]";
            if (getAppArgs().size() == typeOptionIndex + 1)
                throw new ProgramException("missing option input");
            if (!getAppArgs().get(typeOptionIndex + 1).matches(typeRegex))
                throw new ProgramException("incorrect option input for -t");
            type = getAppArgs().get(typeOptionIndex + 1).charAt(0);
            inputStart += 2;
        }

        if (getAppArgs().size() == inputStart) {
            printFunctionOutput(getInputReader(), minChars, type);
        } else {
            for (int i = inputStart; i < getAppArgs().size(); i++) {
                printFunctionOutput(getReader(getAppArgs().get(i)), minChars, type);
            }
        }

    }

    private void printFunctionOutput(BufferedReader reader, int minChars, char type) throws IOException
    {
        int fileCharCount = 0;
        try (reader) {
            String line;
            while ((line = reader.readLine()) != null) {
                int count = line.length();
                if (count >= minChars) {
                    printLineOutput(line, fileCharCount, type);
                }
                fileCharCount += count + System.lineSeparator().length();
            }
        }
    }

    private void printLineOutput(String line, int count, char type) throws IOException {
        String countString = "";
        HashMap<Character, Integer> radixMap = new HashMap<>(Map.of('d', 10, 'b', 2, 'o', 8, 'x', 16));
        if (radixMap.containsKey(type)) countString = Integer.toString(count, radixMap.get(type));
        if (countString.length() != 0) countString += "\t";
        getWriter().write(countString + line + System.lineSeparator());
        getWriter().flush();
    }

}
