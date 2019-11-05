/**
 * 
 */
package com.eveland.app;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test jUnit class for {@link com.eveland.app.App}
 * 
 * 
 * 
 * Correct highest counts for pb_moby_dick.txt:
 *   "the sperm whale" => Total: 87
 *   -- the sperm whale - 69
 *   -- the\nsperm whale - 8
 *   -- the sperm\n whale - 10
 *      
 *   "of the whale" => Total: 79
 *   -- of the whale - 67
 *   -- of\nthe whale - 5
 *   -- of the\nwhale - 7
 * 
 * @author justineveland
 */
public class AppTest {

    private static final String RESOURCES_FOLDER = "src/test/resources/";
    private static final String RESULTS_FOLDER = "src/test/resources/results/";
    
    private static final String EMPTY_FILE_TXT = RESOURCES_FOLDER + "empty_file.txt";
    private static final String COUNTS_TXT = RESOURCES_FOLDER + "counts.txt";
    private static final String MOBY_DICK_TXT = RESOURCES_FOLDER + "pg_moby_dick.txt";
    private static final String WAR_AND_PEACE_TXT = RESOURCES_FOLDER + "war-and-peace.txt";
    
    private static final String EMPTY_FILE_RESULTS_TXT = RESULTS_FOLDER + "empty_file_results.txt";
    private static final String COUNTS_RESULTS_TXT = RESULTS_FOLDER + "counts_results.txt";
    private static final String MOBY_DICK_STDIN_RESULTS_TXT = RESULTS_FOLDER + "pg_moby_dick_stdin_results.txt";
    private static final String MOBY_DICK_ARGV_RESULTS_TXT = RESULTS_FOLDER + "pg_moby_dick_argv_results.txt";
    private static final String TWO_FILE_ARGV_RESULTS_TXT = RESULTS_FOLDER + "two_file_argv_results.txt";
    
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
	/*
	 * Sanity Check Tests to make sure file IO with StdIn and command line args is working as expected
	 */
	@Test
	public void testMain_UsingStdIn_ProcessLineByLine() throws IOException {
	    String[] args = new String[0];
	    final InputStream original = System.in;
	    final FileInputStream fips = new FileInputStream(new File(MOBY_DICK_TXT));
	    System.setIn(fips);
	    App.PROCESS_WHOLE_FILE = false;
	    App.main(args);
	    System.setIn(original);
	    
	    String resultFile = new String(Files.readAllBytes(Paths.get(MOBY_DICK_STDIN_RESULTS_TXT)), StandardCharsets.UTF_8);
	    assertEquals(resultFile, outContent.toString());
	}
	
	@Test
    public void testMain_UsingStdIn_ProcessWholeFile() throws IOException {
        String[] args = new String[0];
        final InputStream original = System.in;
        final FileInputStream fips = new FileInputStream(new File(MOBY_DICK_TXT));
        System.setIn(fips);
        App.PROCESS_WHOLE_FILE = true;
        App.main(args);
        System.setIn(original);
        
        String resultFile = new String(Files.readAllBytes(Paths.get(MOBY_DICK_STDIN_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
    public void testMain_UsingFileArgs_WithEmptyFile_ProcessLineByLine() throws IOException {
        String[] args = new String[] {EMPTY_FILE_TXT};
        App.PROCESS_WHOLE_FILE = false;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(EMPTY_FILE_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
    public void testMain_UsingFileArgs_WithEmptyFile_ProcessWholeFile() throws IOException {
        String[] args = new String[] {EMPTY_FILE_TXT};
        App.PROCESS_WHOLE_FILE = true;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(EMPTY_FILE_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
    public void testMain_UsingFileArgs_WithCountsFile_ProcessLineByLine() throws IOException {
        String[] args = new String[] {COUNTS_TXT};
        App.PROCESS_WHOLE_FILE = false;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(COUNTS_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
    public void testMain_UsingFileArgs_WithCountsFile_ProcessWholeFile() throws IOException {
        String[] args = new String[] {COUNTS_TXT};
        App.PROCESS_WHOLE_FILE = true;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(COUNTS_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }

	@Test
    public void testMain_UsingFileArgs_WithSingleFile_ProcessLineByLine() throws IOException {
        String[] args = new String[] {MOBY_DICK_TXT};
        App.PROCESS_WHOLE_FILE = false;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(MOBY_DICK_ARGV_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
    public void testMain_UsingFileArgs_WithSingleFile_ProcessWholeFile() throws IOException {
        String[] args = new String[] {MOBY_DICK_TXT};
        App.PROCESS_WHOLE_FILE = true;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(MOBY_DICK_ARGV_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	
	@Test
	public void testMain_UsingFileArgs_WithTwoFiles_ProcessLineByLine() throws IOException {
	    String[] args = new String[] {MOBY_DICK_TXT, WAR_AND_PEACE_TXT};
	    App.PROCESS_WHOLE_FILE = false;
	    App.main(args);
	    String resultFile = new String(Files.readAllBytes(Paths.get(TWO_FILE_ARGV_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
	}
	
	@Test
    public void testMain_UsingFileArgs_WithTwoFiles_ProcessWholeFile() throws IOException {
        String[] args = new String[] {MOBY_DICK_TXT, WAR_AND_PEACE_TXT};
        App.PROCESS_WHOLE_FILE = true;
        App.main(args);
        String resultFile = new String(Files.readAllBytes(Paths.get(TWO_FILE_ARGV_RESULTS_TXT)), StandardCharsets.UTF_8);
        assertEquals(resultFile, outContent.toString());
    }
	    
	/*
	 * Tests for some different Strings to ensure regex is working as expected
	 */
	@Test
	public void emptyString_ShouldReturnEmpty() {
	    String testString = "";
        String successString = "";
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
    public void singleWord_ShouldReturnWord() {
        String testString = "test";
        String successString = "test";
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
    }
	
	@Test
    public void singleWordWithSingleQuote_ShouldReturnWord() {
        String testString = "Whale's";
        String successString = "whale's";
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
    }
	
	@Test
	public void wordWithSingleHyphen_ShouldReturnOneWord() {
	    String testString = "In various sorts of whales, they form such " + 
	            "irregular combinations; or, in the case of any one of them detached, " + 
	            "such an irregular isolation; as utterly to defy all general " + 
	            "methodization formed upon such a basis. On this rock every one of the " + 
	            "whale-naturalists has split.";
	    String successString = ("In various sorts of whales they form such " + 
	            "irregular combinations or in the case of any one of them detached " + 
	            "such an irregular isolation as utterly to defy all general " + 
	            "methodization formed upon such a basis On this rock every one of the " + 
	            "whale-naturalists has split").toLowerCase();
	    List<String> matches = App.getMatchesAsList(testString);
	    assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void wordWithDoubleHyphen_ShouldReturnTwoWords() {
	    String testString = "But it may possibly be conceived that, in the internal parts of the " + 
	            "whale, in his anatomy--there, at least, we shall be able to hit the " + 
	            "right classification.";
        String successString = ("But it may possibly be conceived that in the internal parts of the " + 
                "whale in his anatomy there at least we shall be able to hit the " + 
                "right classification").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void wordWithSingleQuotes_ShouldReturnOneWord() {
	    String testString = "He piled upon " + 
	            "the whale's white hump the sum of all the general rage and hate felt " + 
	            "by his whole race from Adam down; and then, as if his chest had been a " + 
	            "mortar, he burst his hot heart's shell upon it.";
        String successString = ("He piled upon " + 
                "the whale's white hump the sum of all the general rage and hate felt " + 
                "by his whole race from Adam down and then as if his chest had been a " + 
                "mortar he burst his hot heart's shell upon it").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void lineWithDoubleQuotes_ShouldReturnWords() {
	    String testString = "The Romish " + 
	            "mass for the dead begins with \"Requiem eternam\" (eternal rest), whence " + 
	            "REQUIEM denominating the mass itself, and any other funeral music.";
        String successString = ("The Romish " + 
                "mass for the dead begins with Requiem eternam eternal rest whence " + 
                "REQUIEM denominating the mass itself and any other funeral music").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void lineWithSemiColons_ShouldReturnWords() {
	    String testString = "But even assuming all this to be true; " + 
	            "yet, were it not for the whiteness, you would not have that intensified " + 
	            "terror.";
        String successString = ("But even assuming all this to be true " + 
                "yet were it not for the whiteness you would not have that intensified " + 
                "terror").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void lineWithAsterisks_ShouldReturnWords() {
	    String testString = "*** START OF THIS PROJECT GUTENBERG EBOOK MOBY DICK; OR THE WHALE ***";
        String successString = ("START OF THIS PROJECT GUTENBERG EBOOK MOBY DICK OR THE WHALE").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
	public void lineWithSentencePunctuation_ShouldReturnWords() {
	    String testString = "\"'Stern all!' exclaimed the mate, as upon turning his head, he saw the " + 
	            "distended jaws of a large Sperm Whale close to the head of the boat, " + 
	            "threatening it with instant destruction;--'Stern all, for your lives!'\" " + 
	            "--WHARTON THE WHALE KILLER.";
        String successString = ("Stern all exclaimed the mate as upon turning his head he saw the " + 
                "distended jaws of a large Sperm Whale close to the head of the boat " + 
                "threatening it with instant destruction Stern all for your lives " + 
                "WHARTON THE WHALE KILLER").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
	}
	
	@Test
    public void lineWithTwoParagraphs_ShouldReturnWords() {
        String testString = "\"The Whale is harpooned to be sure; but bethink you, how you would " + 
                "manage a powerful unbroken colt, with the mere appliance of a rope tied " + 
                "to the root of his tail.\" --A CHAPTER ON WHALING IN RIBS AND TRUCKS. " + 
                "\n" + 
                "\"On one occasion I saw two of these monsters (whales) probably male and " + 
                "female, slowly swimming, one after the other, within less than a stone's " + 
                "throw of the shore\" (Terra Del Fuego), \"over which the beech tree " + 
                "extended its branches.\" --DARWIN'S VOYAGE OF A NATURALIST.";
        String successString = ("The Whale is harpooned to be sure but bethink you how you would " + 
                "manage a powerful unbroken colt with the mere appliance of a rope tied " + 
                "to the root of his tail A CHAPTER ON WHALING IN RIBS AND TRUCKS " +  
                "On one occasion I saw two of these monsters whales probably male and " + 
                "female slowly swimming one after the other within less than a stone's " + 
                "throw of the shore Terra Del Fuego over which the beech tree " + 
                "extended its branches DARWIN'S VOYAGE OF A NATURALIST").toLowerCase();
        List<String> matches = App.getMatchesAsList(testString);
        assertEquals(successString, String.join(" ", matches));
    }
}
