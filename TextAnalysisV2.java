import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextAnalysisV2 {

    // Predefined exclusions: articles, prepositions, pronouns, conjunctions, modals
    private static final Set<String> EXCLUSIONS = new HashSet<>(Arrays.asList(
            // Articles
            "a","an","the",
            // Conjunctions
            "and","or","but","nor","yet","so","for",
            // Prepositions
            "in","on","at","by","for","from","to","of","with","about","into","onto","upon",
            "over","under","above","below","before","after","since","during","through",
            "across","between","among","against","towards","around","without","within",
            "as","like","than","out",
            // Pronouns
            "i","me","my","mine","myself","we","us","our","ours","ourselves","you","your",
            "yours","yourself","yourselves","he","him","his","himself","she","her","hers",
            "herself","it","its","itself","they","them","their","theirs","themselves",
            "this","that","these","those","who","whom","whose","which","what","ye",
            // Auxiliaries + Modals
            "is","am","are","was","were","be","been","being","do","does","did","done",
            "doing","have","has","had","having","shall","should","will","would","can",
            "could","may","might","must","ought",
            // Demonstratives/others
            "there","here","where","when","why","how","up","more","old","if",
            // High frequency neutral words
            "all","one","now","then","some","no","not","s","t"
    ));

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        // Load file
        String text = Files.readString(Paths.get("/Users/srik22/Downloads/moby.txt"));

        // Regex for words: only letters (ignores numbers/punctuation)
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");

        // Extract, normalize, filter, and collect frequencies
        Map<String, Long> freqMap = wordPattern.matcher(text)
                .results()
                .map(match -> match.group().toLowerCase())// lowercase
                .map(word -> word.endsWith("'s") ? word.substring(0, word.length() - 2) : word) // strip 's
                .filter(word -> !EXCLUSIONS.contains(word))// exclude common words
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        // Total word count (excluding filtered words)
        long totalWordCount = freqMap.values().stream().mapToLong(Long::longValue).sum();

        // Top 5 most frequent words
        List<Map.Entry<String, Long>> top5 = freqMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(5)
                .toList();

        // Unique words in alphabetical order
        List<String> uniqueWords = freqMap.keySet().stream()
                .sorted()
                .limit(50) // only first 50
                .toList();

        long endTime = System.currentTimeMillis();
        long processingTimeSec = (endTime - startTime);

        // === OUTPUT ===
        System.out.println("=== Text Analysis Report ===");
        System.out.println("Total Words (after exclusions): " + totalWordCount);
        System.out.println("\nTop 5 Words:");
        top5.forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));

        System.out.println("\nUnique Words (first 50 alphabetically):");
        uniqueWords.forEach(System.out::println);

        System.out.println("\nProcessing time: " + processingTimeSec + "ms");

        // === Repo name format ===
        if (top5.size() >= 5) {
            String repoName = top5.get(0).getKey() + "_" +
                    top5.get(4).getKey() + "_" +
                    processingTimeSec + "ms";
            System.out.println("\nSuggested Repo Name: " + repoName);
        }
    }
}
