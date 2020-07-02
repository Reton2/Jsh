package uk.ac.ucl.jsh.model;

import java.util.ArrayList;

public class CommandParser {

    private static int prevDelimiterIndex;
    private static int splitIndex;

    private static void splitRawCommands(String cmdline, ArrayList<String> rawCommands)
    {
        int closingPairIndex;
        prevDelimiterIndex = 0;
        splitIndex = 0;

        for (splitIndex = 0; splitIndex < cmdline.length(); splitIndex++) {
            char ch = cmdline.charAt(splitIndex);
            switch (ch){
                case ';':
                    rawCommands.add(cmdline.substring(prevDelimiterIndex, splitIndex));
                    prevDelimiterIndex = splitIndex + 1;
                    break;
                case '\'':
                case '\"':
                    closingPairIndex = cmdline.indexOf(ch, splitIndex + 1);
                    if (!(closingPairIndex == -1)) splitIndex = closingPairIndex;
                    break;
            }
        }
    }

    private static void addLastCommand(String cmdline, ArrayList<String> rawCommands)
    {
        if (!cmdline.isEmpty() && splitIndex != prevDelimiterIndex) {
            String command = cmdline.substring(prevDelimiterIndex).trim();
            if (!command.isEmpty()) {
                rawCommands.add(cmdline.substring(prevDelimiterIndex, splitIndex).trim());
            }
        }
    }

    public static ArrayList<String> parseInput(String cmdline)
    {
        ArrayList<String> rawCommands = new ArrayList<String>();
        splitRawCommands(cmdline, rawCommands);
        addLastCommand(cmdline, rawCommands);
        return rawCommands;
    }

}
