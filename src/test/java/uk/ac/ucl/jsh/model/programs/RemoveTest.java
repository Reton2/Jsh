
package uk.ac.ucl.jsh.model.programs;

import org.apache.commons.io.FileDeleteStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.jsh.JshTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class RemoveTest extends JshTest {

   @Before
   public void init() throws IOException {
       Path currentDir = Paths.get(System.getProperty("user.dir"));
       Files.createFile(currentDir.resolve("normal.txt"));
       Files.createFile(currentDir.resolve("normal2.txt"));
       Files.createFile(currentDir.resolve("-dir"));
       Files.createFile(currentDir.resolve("notWritable.txt")).toFile().setWritable(false);
       Path first = Files.createDirectory(currentDir.resolve("first"));
       Files.createFile(first.resolve("normal.txt"));
       Files.createFile(first.resolve("normal2.txt"));
       Files.createFile(first.resolve("notWritable.txt")).toFile().setWritable(false);
       Path third = Files.createDirectory(currentDir.resolve("third"));
       Files.createFile(third.resolve("normal.txt"));
       Files.createFile(third.resolve("normal2.txt"));
       Path inside = Files.createDirectory(third.resolve("inner"));
       Files.createFile(inside.resolve("normal.txt"));
       Files.createFile(inside.resolve("normal2.txt"));
       Files.createDirectory(currentDir.resolve("second"));
   }

   @Test
   public void basicTest() throws IOException {

       String output = getOutputString("rm normal.txt");
       assertFalse(checkIfExists("normal.txt"));
       assertEquals("", output);
   }

   @Test
   public void twoFileTest() throws IOException {
       String output = getOutputString("rm -v normal.txt normal2.txt");
       assertFalse(checkIfExists("normal.txt"));
       assertFalse(checkIfExists("normal2.txt"));
       String expected = "rm: normal.txt was successfully deleted" + lineSeparator +
               "rm: normal2.txt was successfully deleted";
       assertEquals(expected, output);
   }

   @Test
   public void tryFileNotWritableTest() throws IOException {
       String output = getOutputString("rm notWritable.txt");
       assertEquals("", output);
       assertTrue(checkIfExists("notWritable.txt"));
   }

   @Test
   public void tryFileNotWritableVerboseTest() throws IOException {
       String output = getOutputString("rm -v notWritable.txt");
       assertEquals("rm: notWritable.txt could not be deleted", output);
       assertTrue(checkIfExists("notWritable.txt"));
   }

   @Test
   public void tryFileForceNotWritableVerboseTest() throws IOException {
       String output = getOutputString("rm -vf notWritable.txt");
       assertFalse(checkIfExists("notWritable.txt"));
       assertEquals("rm: notWritable.txt was successfully deleted", output);
   }

   @Test
   public void tryEmptyDirTest() throws IOException {
       String output = getOutputString("rm -v second");
       assertTrue(checkIfExists("second"));
       assertEquals("rm: second is a directory: not removed", output);
   }

   @Test
   public void dirEmptyOptionTest() throws IOException {
       String output = getOutputString("rm -vr second");
       assertFalse(checkIfExists("second"));
       assertEquals("rm: second was successfully deleted", output);
   }

   @Test
   public void dirEmptyOptionTestWithoutVerbose() throws IOException {
       String output = getOutputString("rm -r second");
       assertFalse(checkIfExists("second"));
       assertEquals("", output);
   }

   @Test
   public void dirNotWritableOptionTest() throws IOException {
       String output = getOutputString("rm -vr first");
       assertEquals("rm: first could not be deleted", output);
       assertTrue(checkIfExists("first"));
   }

   @Test
   public void dirForceHasNotWritableOptionTest() throws IOException {
       String output = getOutputString("rm -vrf first");
       assertFalse(checkIfExists("first"));
       assertEquals("rm: first was successfully deleted", output);
   }

   @Test
   public void dirOptionTest() throws IOException {
       getOutputString("rm -f first/notWritable.txt");
       String output = getOutputString("rm -vr first");
       assertFalse(checkIfExists("first"));
       assertEquals("rm: first was successfully deleted", output);
   }

   @Test
   public void dirStartsWithOptionSymbolTest() throws IOException {
       String output = getOutputString("_rm -dir");
       assertTrue(checkIfExists("-dir"));
       assertEquals("rm: -dir is an invalid option config", output);
   }

   @Test
   public void dirStartsWithOptionSymbolResolvedTest() throws IOException {
       String output = getOutputString("rm -- -dir");
       assertFalse(checkIfExists("-dir"));
       assertEquals("", output);
   }

   @Test
   public void fileDoesNotExistTest() throws IOException {
       String output = getOutputString("rm -v maybe.txt");
       assertFalse(checkIfExists("maybe.txt"));
       assertEquals("rm: maybe.txt does not exist", output);
   }

   @Test
   public void noArgs() throws IOException {
       String output = getOutputString("_rm");
       assertEquals("rm: missing operands", output);
   }

   @Test
   public void noArgsWithOptions() throws IOException {
       String output = getOutputString("_rm -v");
       assertEquals("rm: missing operands", output);
   }

   @Test
   public void doubleDirTest() throws IOException {
       String output = getOutputString("rm -vr third");
       assertFalse(checkIfExists("third"));
       assertEquals("rm: third was successfully deleted", output);
   }

   private boolean checkIfExists(String pathName) {
       return Files.exists(Paths.get(System.getProperty("user.dir")).resolve(pathName));
   }

   @After
   public void delete() throws IOException {
       deleteFile("normal.txt");
       deleteFile("normal2.txt");
       deleteFile("notWritable.txt");
       deleteFile("-dir");
       deleteFile("first/normal.txt");
       deleteFile("first/normal2.txt");
       deleteFile("first/notWritable.txt");
       deleteFile("third/normal.txt");
       deleteFile("third/normal2.txt");
       deleteFile("third/inner/normal.txt");
       deleteFile("third/inner/normal2.txt");
       deleteFile("third/inner");
       deleteFile("third");
       deleteFile("second");
       deleteFile("first");
   }

   private void deleteFile(String pathName) throws IOException {
       FileDeleteStrategy.FORCE.delete((Paths.get(System.getProperty("user.dir")).resolve(pathName)).toFile());
   }

}
