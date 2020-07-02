package uk.ac.ucl.jsh.model.programs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Copy extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "cp";

    @Override
    public void execute() throws IOException {
        setWriter();
        try {
            startFunction();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void startFunction() throws IOException, ProgramException {

        boolean verbose = false, dir = false;
        int startOfInput = 0;

        if (getAppArgs().size() == 0) throw new ProgramException("operands missing");

        final String option = getAppArgs().get(0);

        if (option.startsWith("-")) {
            final String regex = "-((?<verbose>v)|(?<dir>r)){1,2}$";
            final Matcher matcher = Pattern.compile(regex).matcher(option);

            startOfInput = 1;
            if (matcher.find()) {
                verbose = matcher.group("verbose") != null;
                dir = matcher.group("dir") != null;
            } else
                throw new ProgramException(option + " is not a valid option");
        }

        ArrayList<Path> paths = new ArrayList<>();
        getAppArgs().subList(startOfInput, getAppArgs().size()).forEach(s -> paths.add(Paths.get(getCurrentDirectory()).resolve(s)));

        if (getAppArgs().size() > 2 + startOfInput) {
            if (dir) throw new ProgramException("invalid args");
            copyAllFiles(paths, verbose);
        } else if (getAppArgs().size() == 2 + startOfInput) {
            String output = copy(paths, dir, true);
            if (verbose) write(PROGRAM_COMMAND + ": " + output);
        } else
            throw new ProgramException("too few operands");

    }

    private String copy(ArrayList<Path> paths, boolean dir, boolean first) throws IOException {
        final int offset = getAppArgs().size() - 2;
        final Path target = paths.get(1);
        final Path file = paths.get(0);
        final String fileName = first ? getAppArgs().get(offset) : file.getFileName().toString();
        String output = fileName + " ";
        if (Files.exists(file)) {
            if (Files.isDirectory(file)) {
                if (dir) {
                    File[] childFiles = file.toFile().listFiles();
                    for (File childFile : childFiles) {
                        if (childFile.isDirectory()) {
                            Files.createDirectory(target.resolve(childFile.getName()));
                            ArrayList<Path> childPaths =
                                    new ArrayList<>(List.of(childFile.toPath(), target.resolve(childFile.getName())));
                            copy(childPaths, true, false);
                        } else {
                            ArrayList<Path> childPaths = new ArrayList<>(List.of(childFile.toPath(), target));
                            copy(childPaths, true, false);
                        }
                    }
                    output += "successfully copied";
                } else {
                    output += "is a directory: could not be copied";
                }
            } else {
                boolean tIsDir = Files.isDirectory(target);
                try {
                    Files.copy(file, tIsDir ? target.resolve(fileName) : target);
                    output += "successfully copied";
                } catch (FileAlreadyExistsException e) {
                    output = Paths.get(getCurrentDirectory()).relativize(target) + " already exists";
                }
            }
        } else {
            output += "does not exist";
        }
        return output;
    }

    private void copyAllFiles(ArrayList<Path> paths, boolean verbose) throws IOException
    {
        int offset = getAppArgs().size() - paths.size();
        final Path dir = paths.get(paths.size() - 1);

        for (int count = 0; count < paths.size() - 1; count++) {
            final Path filePath = paths.get(count);
            String output = getAppArgs().get(count + offset);
            if (Files.exists(filePath)) {
                if (!Files.isDirectory(filePath)) {
                    try {
                        Files.copy(filePath, dir.resolve(filePath.getFileName()));
                        output += " successfully copied";
                    } catch (FileAlreadyExistsException e) {
                        output = Paths.get(getCurrentDirectory()).relativize(dir.resolve(filePath.getFileName())) + " already exists";
                    }
                } else {
                    output += " is a directory: could not be copied";
                }
            } else {
                output += " does not exist";
            }
            if (verbose) write(PROGRAM_COMMAND + ": " + output);
        }
    }

}
