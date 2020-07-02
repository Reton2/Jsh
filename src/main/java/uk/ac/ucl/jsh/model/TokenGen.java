package uk.ac.ucl.jsh.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenGen {

    public static ArrayList<String> generateTokens(String rawCommand, String currentDirectory) throws IOException {
        ArrayList<String> tokens = new ArrayList<>();
        Matcher regexMatcher = getRegexMatcher(rawCommand);
        while (regexMatcher.find()) {
            if (regexMatcher.group("ComSub") != null) {
                comSubOperation(regexMatcher.group("ComSub"), tokens, currentDirectory);
            } else if (regexMatcher.group("ioRedirect") != null) {
                ioOperation(regexMatcher.group("ioRedirect"), regexMatcher.group("ioMark"), tokens);
            } else if (regexMatcher.group("DoubleQuote") != null || regexMatcher.group("SingleQuote") != null) {
                quoteOperation(regexMatcher.group(), tokens, currentDirectory);
            } else {
                nonQuoteOperation(regexMatcher.group(), tokens, currentDirectory);
            }
        }
        return tokens;
    }

    private static Matcher getRegexMatcher(String rawCommand)
    {
        String comSubSpecial = "(?<ComSub>[^\\s\"\']*"
                + SubResolver.delimiter
                + ".*" + SubResolver.delimiter + "[^\\s\'\"]*)";
        String quoted = "(?<DoubleQuote>\"([^\"]*)\")|(?<SingleQuote>'([^']*)')";
        String spaceRegex = "(?<Token>[^\\s\"']|" + quoted + ")+";
        String other = spaceRegex
                .replace("?<Token>", "")
                .replace("?<DoubleQuote>", "")
                .replace("?<SingleQuote>", "");
        String thisRegex = comSubSpecial + "|" + "(?<ioRedirect>(?<ioMark>>>|<|2>|>)[\\s]?(" + other + "))|" + spaceRegex;
        Pattern regex = Pattern.compile(thisRegex);
        return regex.matcher(rawCommand);
    }

    private static void glob(String token, ArrayList<String> tokens, String currentDir) throws IOException {
        List<String> globbingResult = null;
        globbingResult = Glob.getGlobbingResult(token, currentDir);
        if (globbingResult.isEmpty()) {
            globbingResult.add(token);
        }
        tokens.addAll(globbingResult);
    }

    private static String globForString(String token, String currentDirectory) throws IOException
    {
        ArrayList<String> globs = new ArrayList<>();
        glob(token, globs, currentDirectory);
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (String glob : globs) {
            if (first) {
                builder.append(glob);
                first = false;
            } else
                builder.append(" ").append(glob);
        }
        return builder.toString();
    }

    private static String resolveIt(String token) {
        int next;
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < token.length(); i++)
        {
            char c = token.charAt(i);
            switch (c) {
                case '\"':
                    next = token.indexOf('\"', i + 1);
                    value.append(token, i + 1, next);
                    i = next;
                    break;
                case '\'':
                    next = token.indexOf('\'', i + 1);
                    value.append(token, i + 1, next);
                    i = next;
                    break;
                default:
                    value.append(c);
                    break;
            }
        }
        return value.toString();
    }

    private static void quoteOperation(String token, ArrayList<String> tokens, String currentDir) throws IOException {
        String match = ".*(?<ComSub>" + SubResolver.delimiter + ".*" + SubResolver.delimiter + ").*";
        Pattern pattern = Pattern.compile(match);
        token = resolveIt(token);
        Matcher matcher = pattern.matcher(token);
        while (matcher.find()) {
            String group = matcher.group("ComSub");
            String comSubResult = globForString(group.replace(SubResolver.delimiter, ""), currentDir);
            token = token.replace(group, comSubResult);
        }
        tokens.add(token);
    }

    private static void comSubOperation(String comSub, ArrayList<String> tokens, String currentDirectory) throws IOException {
        comSub = comSub.replace(SubResolver.delimiter, "");
        String[] args = comSub.split("[\\s]");
        for (String arg : args) {
            nonQuoteOperation(arg, tokens, currentDirectory);
        }
    }

    private static void ioOperation(String token, String ioMark, ArrayList<String> tokens) {
        tokens.add(ioMark);
        token = token.replaceFirst(ioMark, "").stripLeading();
        tokens.add(resolveIt(token));
    }

    private static void nonQuoteOperation(String token, ArrayList<String> tokens, String currentDir) throws IOException {
        if (token.matches(".*\\*[^/\\\\]*") && !tokens.get(tokens.size() - 1).equals("-name")) {
            glob(token, tokens, currentDir);
        } else {
            tokens.add(token);
        }
    }


}
