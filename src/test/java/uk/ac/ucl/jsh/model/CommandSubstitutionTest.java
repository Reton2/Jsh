package uk.ac.ucl.jsh.model;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.IOException;

public class CommandSubstitutionTest extends JshTest {

    @Test(timeout = 1000)
    public void testBasic() throws IOException {
        String result = getOutputString("echo `echo hi`");
        Assert.assertEquals(result, "hi");
    }



    @Test(timeout = 1000)
    public void testBasicDoubleNest() throws IOException {
        String result = getOutputString("echo `echo `echo hi``");
        Assert.assertEquals(result, "hi");
    }

    @Test(timeout = 1000)
    public void testInvalidSub() throws IOException {
        try{
            String result = getOutputString("echo `echo hi``");
        } catch (RuntimeException e){
            Assert.assertEquals(e.getMessage(), "Invalid Command Substitution");
        }
    }
}
