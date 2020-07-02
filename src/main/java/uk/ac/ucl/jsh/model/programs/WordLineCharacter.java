package uk.ac.ucl.jsh.model.programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;

public class WordLineCharacter extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "wc";

    public void execute() throws IOException {
        setWriter();
        try {
            printToShell(checkArgs());
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }
    private void printToShell(int mode) throws ProgramException, IOException{
        int[] res = new int[3];

        if(mode == 0){ // selected options from many inputs
            for(int i = 1; i < getAppArgs().size(); i++){
                printSome(getReader(getAppArgs().get(i)), res, true);
            }

            StringBuilder output = new StringBuilder();
            String options = getAppArgs().get(0);
            if(options.contains("l")){
                output.append(res[0]).append("\t");
            }
            if(options.contains("w")){
                output.append(res[1]).append("\t");
            }
            if(options.contains("m")){
                output.append(res[2]).append("\t");
            }
            getWriter().write(output.toString());
            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();

        }
        if(mode == 1){ // all options from many inputs
            for(int i = 0; i < getAppArgs().size(); i++){
                printAll(getReader(getAppArgs().get(i)), res, true);
            }

            getWriter().write(res[0] + "\t");
            getWriter().write(res[1] + "\t");
            getWriter().write(res[2] + "\t");
            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();

        }
        if(mode == 2){ // all options from selected file
            printAll(getReader(getAppArgs().get(0)), res, false);
        }
        if(mode == 3){ // selected options from selected file
            printSome(getReader(getAppArgs().get(1)), res, false);
        }
        if(mode == 4){ // all options from redirect
            printallStdin(getInputReader());
        }
        if(mode == 5){ // selected options from redirect
            printsomeStdin(getInputReader());
        }
    }

    private int checkArgs() throws ProgramException, IOException {
        if(getAppArgs().size() >= 2){
            if(isOptionValid(getAppArgs().get(0)) && getAppArgs().size()==2) {
                return 3; // options, from file
            }
            if(isOptionValid(getAppArgs().get(0))){
                return 0; // some options from many inputs
            } else {
                if(getAppArgs().get(0).charAt(0)=='-'){
                    throw new ProgramException("Invalid Options");
                }
                return 1; //all options from many inputs
            }
        } else if(getAppArgs().size() == 0) {
            return 4; // all options, from stdin
        } else {
            if(isOptionValid(getAppArgs().get(0))){
                return 5; // options, from stdin
            } else {
                if(getInputReader().ready()) throw new ProgramException("Invalid Options");
                else {
                    return 2; // all options, from file
                }
            }
        }
    }
    private boolean isOptionValid(String option) {
        if (option.length() > 4) return false;
        option = option.replace("-", "");
        if (option.matches("[wml]*$")) {
            return checkDupe(option);
        }
        return false;
    }
    private boolean checkDupe(String s){
        HashSet<Character> set = new HashSet<>();
        for(int i = 0; i < s.length(); i++){
            if(set.contains(s.charAt(i))){
                return false;
            } else{
                set.add(s.charAt(i));
            }
        }
        return true;
    }
    private void printAll(BufferedReader reader, int[] values, boolean justEval) throws IOException, ProgramException {
        int charNum = 0;
        int wordNum = 0;
        int lineNum = 0;
        String[] words;
        int c;
        StringBuilder fullString = new StringBuilder();
        try{
            while((c = reader.read()) != -1) {
                charNum++;
                fullString.append((char)c);
                if(c == 10){
                    lineNum++;
                }
            }
            words = fullString.toString().split("[ \n]");   //Split the word using space
            wordNum += words.length;

            values[0] += lineNum;
            values[1] += wordNum;
            values[2] += charNum;
        } catch (IOException e){
            throw new ProgramException("Directory");
        }
        if(!justEval){
            getWriter().write(lineNum + "\t");
            getWriter().write(wordNum + "\t");
            getWriter().write(charNum + "\t");

            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();
        }
    }
    private void printSome(BufferedReader reader, int[] values, boolean justEval) throws IOException, ProgramException {
        int charNum = 0;
        int wordNum = 0;
        int lineNum = 0;

        String[] words;
        int c;
        StringBuilder fullString = new StringBuilder();
        try{
            while((c = reader.read()) != -1) {
                charNum++;
                fullString.append((char)c);
                if(c == 10){
                    lineNum++;
                }
            }
            words = fullString.toString().split("[ \n]");   //Split the word using space
            wordNum += words.length;
            values[0] += lineNum;
            values[1] += wordNum;
            values[2] += charNum;
        } catch (IOException e){
            throw new ProgramException("Directory");
        }

        if(!justEval){
            StringBuilder output = new StringBuilder();
            String options = getAppArgs().get(0);
            if(options.contains("l")){
                output.append(lineNum).append("\t");
            }
            if(options.contains("w")){
                output.append(wordNum).append("\t");
            }
            if(options.contains("m")){
                output.append(charNum).append("\t");
            }
            getWriter().write(output.toString());
            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();
        }
    }

    private void printallStdin(BufferedReader r) throws IOException {
        String s;
        int lineNums = 0;
        int charNums;
        int wordNums;
        StringBuilder stringBuilder = new StringBuilder();
        while ((s = r.readLine()) != null){
            stringBuilder.append(s);
            stringBuilder.append((char)10);
            lineNums++;
        }
        String[] words = stringBuilder.toString().split("[ \n]");   //Split the word using space
        charNums = stringBuilder.length();
        wordNums = words.length;
        getWriter().write(lineNums + "\t");
        getWriter().write(wordNums + "\t");
        getWriter().write(charNums + "\t");

        getWriter().write(System.getProperty("line.separator"));
        getWriter().flush();
    }

    private void printsomeStdin(BufferedReader r) throws IOException {
        String s;
        int lineNums = 0;
        int charNums;
        int wordNums;
        StringBuilder stringBuilder = new StringBuilder();
        while ((s = r.readLine()) != null){
            stringBuilder.append(s);
            stringBuilder.append((char)10);
            lineNums++;
        }
        String[] words = stringBuilder.toString().split("[ \n]");   //Split the word using space
        charNums = stringBuilder.length();
        wordNums = words.length;
        StringBuilder output = new StringBuilder();
        String options = getAppArgs().get(0);
        if(options.contains("l")){
            output.append(lineNums).append("\t");
        }
        if(options.contains("w")){
            output.append(wordNums).append("\t");
        }
        if(options.contains("m")){
            output.append(charNums).append("\t");
        }
        getWriter().write(output.toString());
        getWriter().write(System.getProperty("line.separator"));
        getWriter().flush();
    }
}


