package uk.ac.ucl.jsh.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Glob {

    public static List<String> getGlobbingResult(String token, String currentDir) throws IOException {
        String pattern = getGlobbingPattern(token);
        Path path = Paths.get(currentDir);
        File dir = path.resolve(token.replace(pattern, "")).toFile();
        List<String> files = new ArrayList<>();
        addMatchingFiles(pattern, dir, files, path.toFile());
        return files;
    }

    private static String getGlobbingPattern(String token)
    {
        if (token.contains("/")) {
            String[] dirs = token.split("/");
            return dirs[dirs.length - 1];
        } else return token;
    }

    private static void addMatchingFiles(String namePattern, final File path, List<String> files, final File currentDir) throws IOException {
        for (final File file : Objects.requireNonNull(path.listFiles()))
        {
            if (nameMatches(file, namePattern) && !file.getName().startsWith("."))
                files.add(file.getCanonicalPath()
                        .replace(currentDir.getCanonicalPath() + File.separator, "")
                .replace("\\", "/"));
        }
    }

    private static boolean nameMatches(File file, String namePattern)
    {
        final String wildcardString = "*";
        final String wildcardRegex = "[^/]*";
        String regex = namePattern.replace(wildcardString, wildcardRegex);
        return file.getName().matches(regex);
    }

}
