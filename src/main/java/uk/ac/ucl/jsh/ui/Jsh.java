package uk.ac.ucl.jsh.ui;

import uk.ac.ucl.jsh.model.Model;

import java.util.Scanner;

public class Jsh {

    private Model model;

    public Jsh(Model model)
    {
        this.model = model;
    }

    public void runShell(String[] args) {
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("jsh: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.out.println("jsh: " + args[0] + ": unexpected argument");
            }
            try {
                model.eval(args[1], System.out);
            } catch (Exception e) {
                e.getCause();
            }
        } else {
            try (Scanner input = new Scanner(System.in);) {
                while (true) {
                    String prompt = model.getCurrentDirectory() + "> ";
                    System.out.print(prompt);
                    try {
                        String cmdline = input.nextLine();
                        model.eval(cmdline, System.out);
                    } catch (Exception e) {
                        e.getCause();
                    }
                }
            }
        }
    }

}
