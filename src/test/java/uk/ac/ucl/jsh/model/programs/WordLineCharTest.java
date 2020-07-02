 package uk.ac.ucl.jsh.model.programs;

 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 import uk.ac.ucl.jsh.JshTest;
 import uk.ac.ucl.jsh.model.Model;

 import java.io.FileWriter;
 import java.io.IOException;

 public class WordLineCharTest extends JshTest {
     private String testString = "wc ";
     private String tempDirName;
     private String tempFileName;
     private String tempFile2Name;
     private Model m;
     private Model originalModel = new Model();

     @Before
     public void init() throws IOException {
         createTempDir();
         this.tempDirName = getTempDirName();

         createTempFile(getTempDir());
         this.tempFileName = getTempFileName();
         createTempFile(getTempDir());
         this.tempFile2Name = getTempFileName();

         FileWriter writer = getTempFileWriter();
         String text = "blah" + lineSeparator + "lah" + lineSeparator + "ah" + lineSeparator + "h";
         writeToFile(writer, text);

         m = getPostActionModel("cd " + tempDirName);
     }

     @Test(timeout = 1000)
     public void testbasic() throws IOException{
         String result = getOutputString(testString  + tempFileName, m);
         String expected = "0\t1\t0";
         Assert.assertEquals(expected, result);
     }

     @Test(timeout = 1000)
     public void testL() throws IOException{
         String result = getOutputString(testString + "-l " + tempFileName, m);
         String expected = "0";
         Assert.assertEquals(expected, result);
     }

     @Test(timeout = 1000)
     public void testM() throws IOException{
         String result = getOutputString(testString + "-m " + tempFileName, m);
         String expected = "0";
         Assert.assertEquals(expected, result);
     }

     @Test(timeout = 1000)
     public void testW() throws IOException{
         String result = getOutputString(testString + "-w " + tempFileName, m);
         String expected = "1";
         Assert.assertEquals(expected, result);
     }

     @Test(timeout = 1000)
     public void testOptions() throws IOException{
         String result = getOutputString(testString + "-lmw " + tempFileName, m);
         String expected = "0\t1\t0";
         Assert.assertEquals(expected, result);
     }


     @Test(timeout = 1000)
     public void testDirectorySomeOptions() throws IOException{
         try{
             String result = getOutputString(testString + " -l " + tempDirName, originalModel);
         } catch (RuntimeException e){
             Assert.assertEquals(e.getMessage(), "wc: " + tempDirName + " is a directory");
         }
     }

     @Test(timeout = 1000)
     public void testInvalidOptions() throws IOException{
         try{
             String result = getOutputString(testString + "-pqr " + tempFileName, m);
         } catch (RuntimeException e){
             Assert.assertEquals(e.getMessage(), "wc: Invalid Options");
         }
     }

     @Test(timeout = 1000)
     public void testInvalidOptionsDupe() throws IOException{
         try{
             String result = getOutputString(testString + "-wll " + tempFileName, m);
         } catch (RuntimeException e){
             Assert.assertEquals(e.getMessage(), "wc: Invalid Options");
         }
     }


     @Test(timeout = 1000)
     public void testMultipleInputsAllOptions() throws IOException{
         String result = getOutputString(testString + tempFileName + " " + tempFile2Name, m);

         String exp = "3\t5\t" + Integer.toString(10 + (3 * lineSeparator.length()));
         Assert.assertEquals(result, exp);
     }

     @Test(timeout = 1000)
     public void testMultipleInputsL() throws IOException{
         String result = getOutputString(testString + "-l "+ tempFileName + " " + tempFile2Name, m);

         int charCount = 10 + (3* lineSeparator.length());
         String exp = "3";
         Assert.assertEquals(result, exp);
     }
     @Test(timeout = 1000)
     public void testMultipleInputsM() throws IOException{
         String result = getOutputString(testString + "-m "+ tempFileName + " " + tempFile2Name, m);

         int charCount = 10 + (3* lineSeparator.length());
         String exp = ""+charCount;
         Assert.assertEquals(result, exp);
     }
     @Test(timeout = 1000)
     public void testMultipleInputsW() throws IOException{
         String result = getOutputString(testString + "-w "+ tempFileName + " " + tempFile2Name, m);

         int charCount = 10 + (3* lineSeparator.length());
         String exp = "5";
         Assert.assertEquals(result, exp);
     }

     @Test(timeout = 1000)
     public void testFromPipeAllOptions() throws IOException{
         String lsRes = getOutputString("ls", m);
         String result = getOutputString("ls | wc", m);

         Assert.assertEquals(result, "1\t1\t"+(lsRes.length()+2));
     }

     @Test(timeout = 1000)
     public void testFromPipeL() throws IOException{
         String lsRes = getOutputString("ls", m);
         String result = getOutputString("ls | wc -l", m);
         Assert.assertEquals(result, "1");
     }
     @Test(timeout = 1000)
     public void testFromPipeW() throws IOException{
         String lsRes = getOutputString("ls", m);
         String result = getOutputString("ls | wc -w", m);
         Assert.assertEquals(result, "1");
     }

     @Test(timeout = 1000)
     public void testFromPipeM() throws IOException{
         String lsRes = getOutputString("ls", m);
         String result = getOutputString("ls | wc -m", m);
         Assert.assertEquals(result, ""+(lsRes.length()+2));
     }

     @Test(timeout = 1000)
     public void testFromPipeInvalidOptions() throws IOException{
         try{
             String result = getOutputString("ls | wc -pqr", m);
         }catch (RuntimeException e){
             Assert.assertEquals(e.getMessage(), "wc: Invalid Options");
         }
     }

 }
