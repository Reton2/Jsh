package uk.ac.ucl.jsh.model.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Find extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "find";

    private static final int PATH_INDEX = 0;
    private static final int PATTERN_INDEX_PATH = 2;
    private static final int PATTERN_INDEX_NO_PATH = 1;

    private int namePatternIndex;

    @Override
    public void execute() throws IOException {
        try {
            setWriter();
            if (checkAppArgs()) {
                filterFiles(getAppArgs().get(namePatternIndex));
            } else {
                BufferedReader reader = getInputReader();
                filterFiles(getStringFromReader(reader).strip());
            }
        } catch (ProgramException e) {
            // Make sure to throw IOExceptions for now. Make sure there is no catch clause for them in programs module
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private boolean checkAppArgs() throws ProgramException
    {
        final String optionString = "-name";

        boolean isOptionAt0;

        if (!getAppArgs().contains(optionString))
            throw new ProgramException("does not contain option arg");
        isOptionAt0 = getAppArgs().indexOf(optionString) == 0;
        setNamePatternIndex(isOptionAt0);
        if (getAppArgs().size() > getNamePatternIndex() + 1)
            throw new ProgramException("too many args");
        return !(getAppArgs().size() == getNamePatternIndex());
    }

    private void filterFiles(String namePattern) throws IOException
    {
        String pathString = getPathString();
        List<File> files = new ArrayList<>();
        addMatchingFiles(namePattern, pathString, files);
        writeToTerminal(files, pathString);
    }

    private String getPathString()
    {
        String pathString;
        if (getNamePatternIndex() == PATTERN_INDEX_NO_PATH)
            pathString = getCurrentDirectory();
        else
            pathString = getAppArgs().get(PATH_INDEX);
        return pathString;
    }

    private void writeToTerminal(List<File> files, String pathString) throws IOException
    {
        String fileRelativePath;

        boolean atLeastOnePrinted = false;
        for (File file : files) {
            fileRelativePath = getFilePrintingPath(file, pathString);
            writeFile(fileRelativePath);
            atLeastOnePrinted = true;
        }
        if (atLeastOnePrinted) {
            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();
        }
    }

    private String getFilePrintingPath(File file, String pathString) throws IOException {
        if (getNamePatternIndex() == PATTERN_INDEX_NO_PATH)
            return file.getCanonicalPath().replaceFirst(Pattern.quote(pathString), ".");
        else {
            Path path = Paths.get(getCurrentDirectory()).resolve(pathString);
            File pathFile = path.toFile();
            return pathString + file.getCanonicalPath().replaceFirst(Pattern.quote(pathFile.getCanonicalPath()), "");
        }
    }

    private void writeFile(String fileName) throws IOException
    {
        getWriter().write(fileName);
        getWriter().write("\t");
        getWriter().flush();
    }


    private void addMatchingFiles(String namePattern, String pathName, List<File> files)
    {
        Path path = Paths.get(getCurrentDirectory()).resolve(pathName);
        final File pathFile = path.toFile();
        File[] fileList = pathFile.listFiles();
        if (fileList == null) return;
        for (final File file : fileList)
        {
            if (file.isDirectory())
                addMatchingFiles(namePattern, file.getAbsolutePath(), files);
            else if (nameMatches(file, namePattern))
                files.add(file);
        }
    }

    private boolean nameMatches(File file, String namePattern)
    {
        final String wildcardString = "*";
        final String wildcardRegex = "[^/]*";
        String regex = namePattern.replace(wildcardString, wildcardRegex);
        return file.getName().matches(regex);
    }

    private void setNamePatternIndex(boolean isOptionAt0)
    {
        namePatternIndex = isOptionAt0 ? PATTERN_INDEX_NO_PATH : PATTERN_INDEX_PATH;
    }

    private int getNamePatternIndex()
    {
        return namePatternIndex;
    }

}
