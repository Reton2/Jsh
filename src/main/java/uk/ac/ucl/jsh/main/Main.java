package uk.ac.ucl.jsh.main;

import uk.ac.ucl.jsh.model.Model;
import uk.ac.ucl.jsh.ui.Jsh;

public class Main {

    public static void main(String[] args) {
        Jsh jsh = new Jsh(new Model());
        jsh.runShell(args);
    }

}
