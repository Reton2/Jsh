package uk.ac.ucl.jsh.model.programs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

public class Touch extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "touch";

    private static final char READ_OPTION = 'r';
    private static final char ACCESS_OPTION = 'a';
    private static final char MODIFIED_OPTION = 'm';
    private static final char BACK_OPTION = 'B';

    private ArrayList<Character> options;

    @Override
    public void execute() throws IOException
    {
        setWriter();
        try {
            checkAppArgs();
            startFunction();
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void startFunction() throws IOException, ProgramException {
        final int readOptionIndex = 2;
        final int readOptionTimeIndex = 3;
        final boolean readOption = options.contains(READ_OPTION);
        int filesStartIndex = options.isEmpty() ? 0 : 1;

        if (readOption) {
            final String readOptionRegex = "-[BF]";
            final String readTimeRegex = "[0-9]+";
            boolean readOffSetOption, hasTimeOption;
            boolean offSetOptionBack;
            long offset;
            Path readFile;

            readFile = Paths.get(getCurrentDirectory()).resolve(getAppArgs().get(1));

            fileExistCheck(readFile);

            readOffSetOption = getAppArgs().get(readOptionIndex).matches(readOptionRegex);

            if (readOffSetOption) {
                if (!(getAppArgs().size() > 4))
                    throw new ProgramException("incorrect number of args");

                filesStartIndex = 4;

                hasTimeOption = getAppArgs().get(readOptionTimeIndex).matches(readTimeRegex);

                offSetOptionBack = getAppArgs().get(readOptionIndex).charAt(1) == BACK_OPTION;

                if (!hasTimeOption)
                    throw new ProgramException("time offset is supposed to be a long");
                else
                    offset = Long.parseLong(getAppArgs().get(readOptionTimeIndex)) * 1000;

                if (offSetOptionBack)
                    offset = offset * -1;

            } else {
                filesStartIndex = 2;
                offset = 0;
            }

            timeStampOn(getFiles(filesStartIndex), fileAccessOffSetTime(readFile, offset), fileModifiedOffSetTime(readFile, offset));

        } else {
            timeStampOn(getFiles(filesStartIndex));
        }
    }

    private void checkAppArgs() throws ProgramException
    {
        options = new ArrayList<>();
        final int optionIndex = 0;

        if (getAppArgs().isEmpty())
            throw new ProgramException("incorrect number of args");

        final String optionRegex = "-[ram]{1,3}";
        if (getAppArgs().get(optionIndex).startsWith("-")) {
            if (getAppArgs().get(optionIndex).matches(optionRegex)) {
                setOptions();
            } else
                throw new ProgramException("incorrect options");

            boolean containReadOption = options.contains(READ_OPTION);

            if (getAppArgs().size() < (containReadOption ? 3 : 2))
                throw new ProgramException("incorrect number of args");

        }
    }

    private void setOptions()
    {
        final int optionIndex = 0;

        char[] optionChars = getAppArgs().get(optionIndex).toCharArray();
        for (int i = 1; i < optionChars.length; i++) options.add(optionChars[i]);
    }

    private void timeStampOn(Path[] files, FileTime accessTime, FileTime modTime) throws IOException {
        final boolean access = options.contains(ACCESS_OPTION);
        final boolean modify = options.contains(MODIFIED_OPTION);

        for (Path file : files) {
            fileExistCheck(file);
            if (access) {
                changeAccessTime(file, accessTime);
            }
            if (modify) {
                changeModifiedTime(file, modTime);
            }
        }
    }

    private void timeStampOn(Path[] files) throws IOException {
        FileTime time = FileTime.fromMillis(System.currentTimeMillis());
        timeStampOn(files, time, time);
    }

    private void changeAccessTime(Path file, FileTime time) throws IOException
    {
        Files.setAttribute(file, "lastAccessTime", time, LinkOption.NOFOLLOW_LINKS);
    }

    private void changeModifiedTime(Path file, FileTime time) throws IOException
    {
        Files.setAttribute(file, "lastModifiedTime", time, LinkOption.NOFOLLOW_LINKS);
    }

    private FileTime fileAccessOffSetTime(Path file, long offset) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        return FileTime.fromMillis(attrs.lastAccessTime().toMillis() + offset);
    }

    private FileTime fileModifiedOffSetTime(Path file, long offset) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        return FileTime.fromMillis(attrs.lastModifiedTime().toMillis() + offset);
    }

    private void fileExistCheck(Path file) throws IOException {
        if (!file.toFile().exists()) Files.createFile(file);
    }

    private Path[] getFiles(int startIndex) {
        Path[] files = new Path[getAppArgs().size() - startIndex];
        for (int i = startIndex; i < getAppArgs().size(); i++)
            files[i - startIndex] = Paths.get(getCurrentDirectory()).resolve(getAppArgs().get(i));
        return files;
    }

}
