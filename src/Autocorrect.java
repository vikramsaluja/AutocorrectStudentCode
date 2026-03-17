import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Vikram Saluja
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */

    // Array of all valid dictionary words
    private String[] words;
    // Maximum allowed edit distance from the tester
    private int threshold;

    // Constructor to initialize dictionary and threshold
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;

    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        // ArrayList that stores valid suggestions with the word and distance
        ArrayList<Pair> matches = new ArrayList<>();

        // Cap the threshold so no word with a distance greater than or equal to 4 is suggested
        int newThreshold = threshold;
        if (newThreshold > 3) {
            newThreshold = 3;
        }


        // Loop through every dictionary word
        for (String word : words) {

            // Compute the edit distance between dictionary word and typed word
            int distance = editDistance(word, typed);

            // Only consider words within the allowed distance
            if (distance <= newThreshold) {

                // Only apply the 2 letter sequence check for longer words
                if (typed.length() > 4 && word.length() > 4) {
                    // Only add if the words share a common sequence of 2 letters
                    if (hasCommonSequence(typed, word)) {
                        matches.add(new Pair(word, distance));
                    }
                } else {
                    // Skip the sequence check for shorter words
                    matches.add(new Pair(word, distance));
                }
            }
        }

        // First sort alphabetically
        matches.sort(Comparator.comparing(Pair::getWord));
        // Then sort by the edit distance using built java method
        matches.sort(Comparator.comparingInt(Pair::getDistance));

        // Convert ArrayList in array of strings
        String[] result = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i).getWord();
        }

        return result;
    }

    public int editDistance(String dictionaryWord, String typedWord) {

        int rows = dictionaryWord.length();
        int cols = typedWord.length();

        // Table[i][j] represents edit distance between first i chars of dictionary word and first j of typed word
        int[][] table = new int[rows + 1][cols + 1];

        // Base case so converting from empty string for all deletions
        for (int dictionaryIndex = 0; dictionaryIndex <= rows; dictionaryIndex++) {
            table[dictionaryIndex][0] = dictionaryIndex;
        }

        // Base case so converting from empty string for all insertions
        for (int typedIndex = 0; typedIndex <= cols; typedIndex++) {
            table[0][typedIndex] = typedIndex;
        }

        // Fill in the table using tabulation
        for (int dictionaryIndex = 1; dictionaryIndex <= rows; dictionaryIndex++) {
            for (int typedIndex = 1; typedIndex <= cols; typedIndex++) {

                // Check if characters match
                int substitutionCost;
                if (dictionaryWord.charAt(dictionaryIndex - 1) == typedWord.charAt(typedIndex - 1)) {
                    substitutionCost = 0;
                } else {
                    substitutionCost = 1;
                }

                // Compute the cost of either a deletion, insertion, or substitution
                int delete = table[dictionaryIndex - 1][typedIndex] + 1;
                int insert = table[dictionaryIndex][typedIndex - 1] + 1;
                int substitute = table[dictionaryIndex - 1][typedIndex - 1] + substitutionCost;

                // take the minimum of the three options
                int best = delete;
                if (insert < best) best = insert;
                if (substitute < best) best = substitute;

                table[dictionaryIndex][typedIndex] = best;
            }
        }

        // Final answer is at the bottom right of the table
        return table[rows][cols];
    }


    // Helper method that checks if two words share at least 1 consecutive sequence of 2 letters
    private boolean hasCommonSequence(String typedWord, String dictionaryWord) {

        // Loop through all 2 letter sequences in typed word
        for (int typedIndex = 0; typedIndex < typedWord.length() - 1; typedIndex++) {

            String typedSequence = typedWord.substring(typedIndex, typedIndex + 2);
            // Compare against all 2 letter sequences in typed word
            for (int dictionaryIndex = 0; dictionaryIndex < dictionaryWord.length() - 1; dictionaryIndex++) {

                String dictionarySequence = dictionaryWord.substring(dictionaryIndex, dictionaryIndex + 2);

                // If any sequences match then return true
                if (typedSequence.equals(dictionarySequence)) {
                    return true;
                }
            }
        }

        // If not matches sequences of 2 letters are found then return false
        return false;
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}