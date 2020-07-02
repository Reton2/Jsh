
// There are two versions of Remove and RemoveTest corresponding to each other. One Remove Program includes the feature
// for forceful deletion of files which are not writable. This is done using commons-io library by apache.
// Due to fear of the docker image used for marking not having permission to change file writability we have included both
// versions of Remove. The tests and class requiring those permissions are fully commented below the other version.
// To try the other version uncomment the dependency commons-io from pom.xml and the relative code and tests.

package uk.ac.ucl.jsh.model.programs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Remove extends AbstractProgram {

    public static final String PROGRAM_COMMAND  = "rm";

    @Override
    public void execute() throws IOException {
        setWriter();
        try {
            startFunction();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void startFunction() throws ProgramException, IOException {
        boolean verbose = false, dir = false;
        int inputStart = 0;

        if (getAppArgs().size() == 0)
            throw new ProgramException("missing operands");

        if (getAppArgs().get(0).startsWith("-")) {
            String option = getAppArgs().get(0);
            inputStart = 1;

            if (!option.equals("--")) {
                final String optionRegex = "-((?<verbose>v)|(?<dir>r)){1,2}$";
                Matcher matcher = Pattern.compile(optionRegex).matcher(option);
                if (matcher.find()) {
                    verbose = matcher.group("verbose") != null;
                    dir = matcher.group("dir") != null;
                } else {
                    throw new ProgramException(option + " is an invalid option config");
                }
            }
        }

        if (getAppArgs().size() == inputStart) throw new ProgramException("missing operands");

        for (int i = inputStart; i < getAppArgs().size(); i++) {
            String fileName = getAppArgs().get(i);
            Path path = Paths.get(getCurrentDirectory()).resolve(fileName);
            String output = remove(path, fileName, dir);
            if (verbose) write(PROGRAM_COMMAND + ": " + output);
        }

    }

    private String remove(Path file, String arg, boolean dir) throws IOException
    {
        String output = null;
        if (Files.exists(file)) {
            if (Files.isDirectory(file)) {
                if (dir) {
                    File[] childFiles = file.toFile().listFiles();
                    for (File childFile : childFiles) {
                        remove(childFile.toPath(), arg, true);
                    }
                    removeFile(file, arg);
                } else
                    output = arg + " is a directory: not removed";
            } else {
                output = removeFile(file, arg);
            }
        } else {
            output = arg + " does not exist";
        }
        if (output == null) output = arg + " was successfully deleted";
        return output;
    }

    private String removeFile(Path file, String arg) throws IOException {
        String output = arg + " was successfully deleted";
        Files.deleteIfExists(file);
        return output;
    }

}



//package uk.ac.ucl.jsh.model.programs;
//
//import org.apache.commons.io.FileDeleteStrategy;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Remove extends AbstractProgram {
//
//    public static final String PROGRAM_COMMAND  = "rm";
//
//    @Override
//    public void execute() throws IOException {
//        setWriter();
//        try {
//            startFunction();
//        } catch (ProgramException e) {
//            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
//        }
//    }
//
//    private void startFunction() throws ProgramException, IOException {
//        boolean verbose = false, force = false, dir = false;
//        int inputStart = 0;
//
//        if (getAppArgs().size() == 0)
//            throw new ProgramException("missing operands");
//
//        if (getAppArgs().get(0).startsWith("-")) {
//            String option = getAppArgs().get(0);
//            inputStart = 1;
//
//            if (!option.equals("--")) {
//                final String optionRegex = "-((?<verbose>v)|(?<dir>r)|(?<force>f)){1,3}$";
//                Matcher matcher = Pattern.compile(optionRegex).matcher(option);
//                if (matcher.find()) {
//                    verbose = matcher.group("verbose") != null;
//                    dir = matcher.group("dir") != null;
//                    force = matcher.group("force") != null;
//                } else {
//                    throw new ProgramException(option + " is an invalid option config");
//                }
//            }
//        }
//
//        if (getAppArgs().size() == inputStart) throw new ProgramException("missing operands");
//
//        for (int i = inputStart; i < getAppArgs().size(); i++) {
//            String fileName = getAppArgs().get(i);
//            Path path = Paths.get(getCurrentDirectory()).resolve(fileName);
//            String output = remove(path, fileName, force, dir);
//            if (verbose) write(PROGRAM_COMMAND + ": " + output);
//        }
//
//    }
//
//    private String remove(Path file, String arg, boolean force, boolean dir) throws IOException
//    {
//        String output = null;
//        if (Files.exists(file)) {
//            if (Files.isDirectory(file)) {
//                if (dir) {
//                    File[] childFiles = file.toFile().listFiles();
//                    boolean isDeletable = force || isDirDeletable(file);
//                    for (File childFile : childFiles) {
//                        remove(childFile.toPath(), arg, force, true);
//                    }
//                    if (isDeletable && file.toFile().listFiles().length == 0) {
//                        removeFile(file, arg, force);
//                    } else {
//                        output = arg + " could not be deleted";
//                    }
//                } else
//                    output = arg + " is a directory: not removed";
//            } else {
//                output = removeFile(file, arg, force);
//            }
//        } else {
//            output = arg + " does not exist";
//        }
//        if (output == null) output = arg + " was successfully deleted";
//        return output;
//    }
//
//    private boolean isDirDeletable(Path file) {
//        boolean isDeletable = true;
//        File[] childFiles = file.toFile().listFiles();
//        for (File childFile : childFiles) {
//            Path childPath = childFile.toPath();
//            if (Files.isDirectory(childPath))
//                isDeletable = isDeletable && isDirDeletable(childPath);
//            if (!isDeletable) return false;
//            isDeletable = Files.isWritable(childPath);
//        }
//        return isDeletable;
//    }
//
//    private String removeFile(Path file, String arg, boolean force) throws IOException {
//        String output = null;
//        if (Files.isWritable(file)) {
//            Files.deleteIfExists(file);
//        }
//        else if (force) {
//            FileDeleteStrategy.FORCE.delete(file.toFile());
//        }
//        else {
//            output = arg + " could not be deleted";
//        }
//        if (output == null) output = arg + " was successfully deleted";
//        return output;
//    }
//
//}