package uk.ac.ucl.jsh.model.programs;

import uk.ac.ucl.jsh.model.Model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProgramFactory {

    private ArrayList<String> appArgs;
    private OutputStream output;
    private OutputStream stdErr;
    private InputStream inputStream;
    private String programName;
    private Model model;
    private Program program;
    private boolean unsafe;

    public static ProgramFactory getInstance()
    {
        return new ProgramFactory();
    }

    private Program programSelect()
    {
        checkUnsafe();
        switch (programName)
        {
            case ChangeDirectory.PROGRAM_COMMAND:
                return new ChangeDirectory();

            case PrintWorkingDirectory.PROGRAM_COMMAND:
                return new PrintWorkingDirectory();

            case List.PROGRAM_COMMAND:
                return new List();

            case Head.PROGRAM_COMMAND:
                return new Head();

            case Echo.PROGRAM_COMMAND:
                return new Echo();

            case GlobalRegexPrint.PROGRAM_COMMAND:
                return new GlobalRegexPrint();

            case Tail.PROGRAM_COMMAND:
                return new Tail();

            case Concatenate.PROGRAM_COMMAND:
                return new Concatenate();

            case Find.PROGRAM_COMMAND:
                return new Find();

            case StreamEditor.PROGRAM_COMMAND:
                return new StreamEditor();

            case WordLineCharacter.PROGRAM_COMMAND:
                return new WordLineCharacter();

            case Touch.PROGRAM_COMMAND:
                return new Touch();

            case MakeDirectory.PROGRAM_COMMAND:
                return new MakeDirectory();

            case Strings.PROGRAM_COMMAND:
                return new Strings();

            case Remove.PROGRAM_COMMAND:
                return new Remove();

            case RemoveDirectory.PROGRAM_COMMAND:
                return new RemoveDirectory();

            case Copy.PROGRAM_COMMAND:
                return new Copy();

            default:
                return null;
        }
    }

    private void checkUnsafe()
    {
        final String UNSAFE_PREFIX = "_";

        if (programName.startsWith(UNSAFE_PREFIX)) {
            unsafe = true;
            programName = programName.substring(1);
        } else {
            unsafe = false;
        }
    }

    public boolean isProgram(String programName)
    {
        this.programName = programName;
        setProgram(programSelect());
        return program != null;
    }

    public Program getProgram()
    {
        program.setModel(model);
        program.setAppArgs(appArgs);
        program.setOutput(output);
        program.setInput(inputStream);
        program.setUnsafe(unsafe);
        program.setStdErr(stdErr);
        return program;
    }

    private void setProgram(Program program)
    {
        this.program = program;
    }

    public void setInput(ArrayList<String> appArgs) {
        this.appArgs = appArgs;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setOutput(OutputStream output)
    {
        this.output = output;
    }

    public void setInputStream(InputStream in) {
        inputStream = in;
    }

    public void setStdErr(OutputStream stdErr) {
        this.stdErr = stdErr;
    }
}
