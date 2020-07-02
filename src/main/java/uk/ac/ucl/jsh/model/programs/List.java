package uk.ac.ucl.jsh.model.programs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class List extends AbstractProgram {

    public static final String PROGRAM_COMMAND = "ls";

    public void execute() throws IOException
    {
        try {
            setWriter();
            if (getAppArgs().size() > 0 && getAppArgs().get(0).equals("-l")) {
                timeStamps(getDir(true));
            }
            else
                writeDir(getDir(false));
        } catch (ProgramException e) {
            writeToStdErr(PROGRAM_COMMAND + ": " + e.getMessage());
        }
    }

    private void timeStamps(File file) throws IOException {
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            for (File aFile : listOfFiles) {
                if (!aFile.getName().startsWith(".")) {
                    getWriter().write(fileAccessOffSetTime(aFile.toPath()) + "\t" + aFile.getName() + System.lineSeparator());
                    getWriter().flush();
                }
            }
        } else {
            getWriter().write(fileAccessOffSetTime(file.toPath()) + "\t" + file.getName() + System.lineSeparator());
            getWriter().flush();
        }
    }

    private void writeDir(File currDir) throws IOException, ProgramException {
        File[] listOfFiles = currDir.listFiles();
        boolean atLeastOnePrinted = false;
        for (File file : listOfFiles) {
            if (!file.getName().startsWith(".")) {
                getWriter().write(file.getName());
                getWriter().write("\t");
                getWriter().flush();
                atLeastOnePrinted = true;
            }
        }
        if (atLeastOnePrinted) {
            getWriter().write(System.getProperty("line.separator"));
            getWriter().flush();
        }
    }

    private File getDir(boolean timeStamp) throws ProgramException {
        File currDir;
        int difference = timeStamp ? 1 : 0;
        if (getAppArgs().size() == difference) {
            currDir = new File(getCurrentDirectory());
        } else if (getAppArgs().size() == 1 + difference) {
            String dir = getAppArgs().get(difference);
            Path current = Paths.get(getCurrentDirectory());
            currDir = current.resolve(dir).toFile();
        } else {
            throw new ProgramException("Too many args");
        }
        if (!currDir.exists())
            throw new ProgramException("no such directory");
        return currDir;
    }

    private String fileAccessOffSetTime(Path file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(attrs.lastAccessTime().toMillis());
    }

}
