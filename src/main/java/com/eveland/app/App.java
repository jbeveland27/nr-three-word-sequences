/**
 * 
 */
package com.eveland.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * When given text(s), will return a list of the 100 most common three word sequences.
 * 
 * Input is accepted from StdIn or from file arguments.
 *       
 * @author justineveland
 */
public class App {
    public static final String WORD_PATTERN = "((\\w+'\\w+)|(\\w+-?\\w+)|(\\w+))";
    
    // Note: This flag is intentionally left mutable so it can be set from within jUnit tests
    public static boolean PROCESS_WHOLE_FILE = false; 
    
    public static void main(String[] args) {

        // Process stdin from pipe
        if (args == null || args.length == 0) {
            processStdIn();
        } else if (args != null && args.length > 0) {
            // Process file args
            for (String file : args) {
                processFile(file);
            }
        } else {
            System.out.println("No input detected. Exiting."); 
        }
    }
    
    /**
     * Processes StdIn and displays an output table containing the top
     * three word phrases.
     */
    private static void processStdIn() {
        System.out.println("Processing stdin...");
        
        if (PROCESS_WHOLE_FILE) {
            processStdInWholeFile();
        } else {
            processStdInLineByLine();
        }
    }
    
    /**
     * Processes the <param>fileName</param> and displays an output table containing the top
     * three word phrases in the file.
     * @param fileName Name of the file to be processed.
     * @param wholeFile Flag for switching the processing scheme between wholeFile and lineByLine.
     */
    private static void processFile(String fileName) {
        System.out.println("Processing: " + fileName);

        if (PROCESS_WHOLE_FILE) {
            processWholeFile(fileName);
        } else {
            processFileLineByLine(fileName);
        }
    }
    
    /**
     * Prints output table of the top <param>limit</param> phrases.
     * @param phrases The {@code Map} containing the phrases and their frequencies.
     * @param limit Number of rows to limit the output to.
     * @param inputStream Name of the source input stream (i.e. the fileName or StdIn).
     */
    private static void printTopPhrases(Map<String, Integer> phrases, int limit, String inputStream) {
        if (phrases.size() > 0) {
            System.out.println("Phrase                                   | Count  ");
            System.out.println("==================================================");
            
            phrases.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(100)
                .forEach(entry -> {
                    System.out.println(String.format("%-40s | %d",entry.getKey(), entry.getValue()));
                });
            
            System.out.println("==================================================");
        } else {
            System.out.println("==================================================");
            System.out.println("No three word phrases detected.");
            System.out.println("==================================================");
        }
        System.out.println("Finished Processing => " + inputStream + System.getProperty("line.separator"));
    }
    
    /**
     * Processes the file line by line and outputs the most common three word sequences. This is 
     * more memory-efficient than processing the whole file into memory (and is the only way
     * to process arbitrarily large files). 
     * @param fileName Name of the file that's to be processed
     */
    private static void processStdInLineByLine() {
        InputStreamReader isReader = new InputStreamReader(System.in);
        try (BufferedReader br = new BufferedReader(isReader)) {
            Map<String, Integer> phrases = processContent(br);

            // Output
            printTopPhrases(phrases, 100, "StdIn");
        } catch (IOException e) {
            System.err.println("File not found.");
            e.printStackTrace();
        }
    }
    
    /**
     * Processes the file line by line and outputs the most common three word sequences. This is 
     * more memory-efficient than processing the whole file into memory (and is the preferred way
     * to process arbitrarily large files). 
     * @param fileName Name of the file that's to be processed
     */
    private static void processFileLineByLine(String fileName) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            Map<String, Integer> phrases = processContent(br);

            // Output
            printTopPhrases(phrases, 100, fileName);
        } catch (IOException e) {
            System.err.println("File not found.");
            e.printStackTrace();
        }
    }
    
    /**
     * Convenience method used to process data line by line and return a {@code Map} containing every 
     * three word phrase and it's frequency. 
     *   
     * @param br BufferedReader to use in reading through the input text
     * @return A {@code Map} of three word phrases and their frequency
     * @throws IOException When BufferedReader is unable to read from the input stream
     */
    private static Map<String, Integer> processContent(BufferedReader br) throws IOException {
        Map<String, Integer> phrases = new HashMap<String, Integer>();
        String currentLine = null;

        List<String> matches = new LinkedList<String>();
        while ((currentLine = br.readLine()) != null) {

            // skip newlines/empty lines
            if (currentLine.trim().length() == 0) {
                continue;
            }

            matches.addAll(getMatchesAsLinkedList(currentLine));

            while (matches.size() >= 3) {
                insertPhrase(phrases, getPhrase(matches.get(0), matches.get(1), matches.get(2)));
                matches.remove(0);
            }
        }

        return phrases;
    }
    
    /** 
     * Convenience method for inserting phrases and counts into {@Code Map}.
     * @param phrases The {@Code Map} the <param>phrase</param> is being inserted into.
     * @param phrase The phrase to be inserted.
     */
    private static void insertPhrase(Map<String, Integer> phrases, String phrase) {
        if (phrases.containsKey(phrase)) {
            phrases.put(phrase, phrases.get(phrase) + 1);
        } else {
            phrases.put(phrase, 1);
        }
    }
    
    /**
     * Convenience method for getting three word phrases to insert into {@Code Map}.
     * @param word1 First word in phrase.
     * @param word2 Second word in phrase.
     * @param word3 Third word in phrase.
     * @return The three-word phrase.
     */
    private static String getPhrase(String word1, String word2, String word3) {
        StringBuilder phrase = new StringBuilder();
        return phrase.append(word1).append(" ").append(word2).append(" ").append(word3).toString();
    }
    
    /**
     * Processes a line of text and returns a {@code List} containing only valid words 
     * (removes invalid punctuation).
     * 
     * Assumes valid words are of the form:
     * - only letters
     * - contraction (letters + apostrophe + (optional) more letters
     * - letters + hyphen + letters
     * @param line Line of text to be processed
     * @return List of words that matched as valid
     */
    public static List<String> getMatchesAsList(String line) {
        List<String> matches = new ArrayList<String>();
        Pattern p = Pattern.compile(WORD_PATTERN);
        Matcher m = p.matcher(line);
        while (m.find()) {
            matches.add(m.group().toLowerCase());
        }
        return matches;
    }
    
    /**
     * Processes a line of text and returns a {@code LinkedList} containing only valid words 
     * (removes invalid punctuation). 
     * 
     * Assumes valid words are of the form:
     * - only letters
     * - contraction (letters + apostrophe + (optional) more letters
     * - letters + hyphen + letters
     * @param line Line of text to be processed
     * @return List of words that matched as valid
     */
    public static LinkedList<String> getMatchesAsLinkedList(String line) {
        LinkedList<String> matches = new LinkedList<String>();
        Pattern p = Pattern.compile(WORD_PATTERN);
        Matcher m = p.matcher(line);
        while (m.find()) {
            matches.add(m.group().toLowerCase());
        }
        return matches;
    }
    
    /**
     * Processes the whole file piped in from StdIn by reading whole file content into 
     * memory, then processing the content to output the most common three word sequences.
     */
    public static void processStdInWholeFile() {
        InputStreamReader isReader = new InputStreamReader(System.in);
        try (BufferedReader br = new BufferedReader(isReader)) {
            Map<String, Integer> phrases = processAllContent(br);

            // Output
            printTopPhrases(phrases, 100, "StdIn");
        } catch (IOException e) {
            System.err.println("File not found.");
            e.printStackTrace();
        }
    }
    
    /**
     * Processes the whole file by reading whole file content into memory, then processing the
     * content to output the most common three word sequences.
     * @param fileName Name of the file that's to be processed
     */
    public static void processWholeFile(String fileName) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            Map<String, Integer> phrases = processAllContent(br);

            // Output
            printTopPhrases(phrases, 100, fileName);
        } catch (IOException e) {
            System.err.println("File not found.");
            e.printStackTrace();
        }
    }
    
    /**
     * Convenience method used to process all daata and return a {@code Map} containing every 
     * three word phrase and it's frequency. 
     *   
     * @param br BufferedReader to use in reading the input text
     * @return A {@code Map} of three word phrases and their frequency
     * @throws IOException When BufferedReader is unable to read from the input stream
     */
    private static Map<String, Integer> processAllContent(BufferedReader br) throws IOException {
        Map<String, Integer> phrases = new HashMap<String, Integer>();

        // Read whole file into memory
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String contents = sb.toString().trim();

        // Get list of strings that match our definition of words
        List<String> matches = getMatchesAsList(contents);

        // Process words
        for (int i = 0; i < matches.size() - 2; i++) {
            insertPhrase(phrases, getPhrase(matches.get(i), matches.get(i + 1), matches.get(i + 2)));
        }

        return phrases;
    }
}
