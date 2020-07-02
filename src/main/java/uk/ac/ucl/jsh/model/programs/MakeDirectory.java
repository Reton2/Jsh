package uk.ac.ucl.jsh.model.programs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeDirectory extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "mkdir";

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
                output =  makeDirWithParent(fileName);
            } else {
                try {
                    output = makeDir(fileName);
                } catch (NoSuchFileException e) {
                    output = fileName + ": no such file or directory";
                }
            }
            if (verbose) write(PROGRAM_COMMAND + ": " + output);
        }
    }

    private String makeDir(String fileName) throws IOException {
        Path path = Paths.get(getCurrentDirectory()).resolve(fileName);
        if (Files.exists(path)) return fileName + " already exists";
        Files.createDirectory(path);
        return fileName + " successfully created";
    }

    private String makeDirWithParent(String files) throws IOException {
        Path path = Paths.get(getCurrentDirectory()).resolve(files);
        if (Files.exists(path)) return files + " already exists";
        Files.createDirectories(path);
        return files + " successfully created";
    }

}
