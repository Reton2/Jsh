package uk.ac.ucl.jsh.model;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assume;



import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitQuickcheck.class)
public class CommandParserTest {

    @Test
    public void unmatchedQuote() {
        ArrayList<String> output = CommandParser.parseInput("cat \"stuff maybe done this; echo");
        ArrayList<String> exp = new ArrayList<>(List.of("cat \"stuff maybe done this", "echo"));
        assertEquals(exp, output);
    }

    @Property(trials =  10)
    public void what(String a, String b)
    {
        Assume.assumeTrue(a.length() > 1);
        Assume.assumeTrue(a.length() > 1);
        Assume.assumeTrue(b.length() > 1);
        ArrayList<String> output = CommandParser.parseInput(a + ";" + b);
        ArrayList<String> exp = new ArrayList<>(List.of(a, b));
        System.out.println(exp);
        assertEquals(exp, output);
    }

    @Test
    public void endWithSemi() {
        ArrayList<String> output = CommandParser.parseInput("cat \"stuff maybe done this;");
        ArrayList<String> exp = new ArrayList<>(List.of("cat \"stuff maybe done this"));
        assertEquals(exp, output);
    }

    @Test
    public void endWithSpaceAfterSemi() {
        ArrayList<String> output = CommandParser.parseInput("cat \"stuff maybe done this; ");
        ArrayList<String> exp = new ArrayList<>(List.of("cat \"stuff maybe done this"));
        assertEquals(exp, output);
    }

    @Test
    public void empty() {
        ArrayList<String> output = CommandParser.parseInput("");
        assertTrue(output.isEmpty());
    }

}
