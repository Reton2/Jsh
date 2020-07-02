package uk.ac.ucl.jsh.model.programs;

import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;
import static org.junit.Assert.*;
import java.io.IOException;

public class PrintWorkingDirectoryTest extends JshTest {
    @Test
    public void testBasic() throws IOException {
        String currDir = System.getProperty("user.dir");
        String testString = "pwd";
        String result = getOutputString(testString);
        assertEquals(currDir, result);
    }
}
