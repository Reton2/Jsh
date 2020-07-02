package uk.ac.ucl.jsh.model.programs;

import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import static org.junit.Assert.assertEquals;

public class EchoTest extends JshTest{

    @Test
    public void testEcho1() throws Exception
    {
        String testString = "echo foo";
        assertEquals("foo", getOutputString(testString));
    }
    @Test
    public void testEmptyEcho() throws Exception
    {
        String testString = "echo";
        assertEquals("", getOutputString(testString));
    }
    @Test
    public void testEcho2() throws Exception
    {
        String testString = "echo foo bar";
        assertEquals("foo bar", getOutputString(testString));
    }
    @Test
    public void testEcho3() throws Exception
    {
        String testString = "echo \"foo bar\"";
        assertEquals("foo bar", getOutputString(testString));
    }
    @Test
    public void testEcho4() throws Exception
    {
        String testString = "echo \'foo foo\'";
        assertEquals("foo foo", getOutputString(testString));
    }
    @Test
    public void testEcho5() throws Exception
    {
        String testString = "echo \'foo" + lineSeparator +  "foo\'";
        assertEquals("foo" +lineSeparator+ "foo", getOutputString(testString));
    }

    @Test
    public void testPipe(){
        String test = "ls | echo";

    }

    @Test
    public void testIOInput(){

        String test = "echo < cat ";

    }

    @Test
    public void testIOOutput(){
        String test = "ls | echo";

    }

}
