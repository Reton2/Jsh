package uk.ac.ucl.jsh.model.programs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveDirectory extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "rmdir";

    @Override
    public void execute() throws IOException {
        setWriter();
        try {
            makeDirs();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void makeDirs() throws IOException, ProgramException {

        boolean parent = false, verbose = false;

        if (getAppArgs().size() == 0) throw new ProgramException("missing operand");

        final String option = getAppArgs().get(0);

        if (option.startsWith("-")) {
            final String regex = "-((?<verbose>v)|(?<parent>p)){1,2}$";
            final Matcher matcher = Pattern.compile(regex).matcher(option);

            if (matcher.find()) {
                verbose = matcher.group("verbose") != null;
                parent = matcher.group("parent") != null;
            } else
                throw new ProgramException(option + " is not a valid option");
        }

        final int startIndex = parent || verbose ? 1 : 0;

        for (int i = 0; i < getAppArgs().size() - startIndex; i++) {
            String fileName = getAppArgs().get(i + startIndex);
            String output;
            if (parent) {
                output =  deleteDirWithParent(fileName);
            } else {
                output = deleteDir(fileName);
            }
            if (verbose) write(PROGRAM_COMMAND + ": " + output);
        }
    }

    private String deleteDir(String fileName) throws IOException {
        Path path = Paths.get(getCurrentDirectory()).resolve(fileName);
        String output = deleteOutcome(path, fileName, false);
        if (output.equals(fileName + " was successfully deleted")) Files.delete(path);
        return output;
    }

    private String deleteOutcome(Path path, String fileName, boolean isParent) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                if (path.toFile().listFiles().length == (isParent ? 1 : 0)) {
                    return fileName + " was successfully deleted";
                } else {
                    return fileName + " was not been deleted: is not empty";
                }
            } else {
                return fileName + " is not a directory";
            }
        } else {
            return fileName + " does not exist";
        }
    }

    private String deleteDirWithParent(String fileName) throws IOException {
        final Path currentDir = Paths.get(getCurrentDirectory());

        Path path = Paths.get(fileName);
        int count = path.getNameCount();
        ArrayList<Path> paths = new ArrayList<>();
        String output;
        boolean isParent = false;

        do {
            Path root = currentDir.resolve(path);
            String rootName = root.getFileName().toString();
            output = deleteOutcome(root, rootName, isParent);
            if (!output.equals(rootName + " was successfully deleted")) break;
            paths.add(path);
            path = path.getParent();
            isParent = true;
            count--;
        } while (count > 0);

        for (Path root : paths) {
            Files.delete(root);
        }

        return output;
    }

}
