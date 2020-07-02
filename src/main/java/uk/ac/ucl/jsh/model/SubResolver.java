package uk.ac.ucl.jsh.model;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class SubResolver {

    public static final String delimiter = "O!h!k!p!S!";

    public static String removeCmdSubs(String cmdline, String currentDirectory) throws IOException {
        String cpy = cmdline;
        int num = numSubs(cpy);
        if(num%2==1) throw new RuntimeException("Invalid Command Substitution");
        while(numSubs(cpy) > 1){
            String inner = cpy.substring(innerStart(cpy)+1, innerEnd(cpy));
            cpy = cpy.replace(cpy.substring(innerStart(cpy), innerEnd(cpy)+1),
                    delimiter +
                            getOutputString(inner, currentDirectory).replace(System.lineSeparator(), " ") +
                            delimiter);
        }
        return cpy;
    }
    private static int numSubs(String cmdLine){
        int num = 0;
        for(int i=0;i<cmdLine.length();i++){
            if(cmdLine.charAt(i)=='`') num++;
        }
        return num;
    }
    private static int innerStart(String cmdLine){
        int n = numSubs(cmdLine);
        int seen = 0;
        int val = -1;
        // echo `echo hi`
        // 0123456789 123
        for(int i = 0; i < cmdLine.length(); i++){
            if(cmdLine.charAt(i) == '`'){
                if(seen == (n/2)-1){
                    val = i;
                }
                seen++;
            }
        }
        return val;
    }
    private static int innerEnd(String cmdLine){
        int n = numSubs(cmdLine);
        int seen = 0;
        int val = -1;
        for(int i = 0; i < cmdLine.length(); i++){
            if(cmdLine.charAt(i) == '`'){
                if(seen == n/2){
                    val = i;
                }
                seen++;
            }
        }
        return val;
    }

    protected static String getOutputString(String testString, String cd) throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        out = new PipedOutputStream(in);
        Model model = new Model();
        model.setCurrentDirectory(cd);
        model.eval(testString, out);
        Scanner scn = new Scanner(in);
        out.close();
        StringBuilder builder = new StringBuilder();
        while (scn.hasNextLine()) builder.append(scn.nextLine()).append(System.getProperty("line.separator"));
        return builder.toString().trim();
    }

}
