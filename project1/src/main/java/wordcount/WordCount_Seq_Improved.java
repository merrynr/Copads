package wordcount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCount_Seq_Improved {

    /**
     * Read and parse all reviews
     * @param dataset_file
     * @return list of reviews
     */
    private static List<AmazonFineFoodReview> read_reviews(String dataset_file) {
        List<AmazonFineFoodReview> allReviews = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataset_file))){
            String reviewLine = null;
            // read the header line
            reviewLine = br.readLine();

            //read the subsequent lines
            while ((reviewLine = br.readLine()) != null) {
                allReviews.add(new AmazonFineFoodReview(reviewLine));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return allReviews;
    }

    /**
     * Write the list of words and their counts to given file
     * @param wordcount
     */
    private static void write_word_count( Map<String, Integer> wordcount, String results_file){
        String resultLine;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(results_file))){
            for(String word : wordcount.keySet()) {
                resultLine = word + " : " + wordcount.get(word);
                bw.write(resultLine);
                bw.newLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the list of words and their counts
     * @param wordcount
     */
    private static void print_word_count( Map<String, Integer> wordcount){
        for(String word : wordcount.keySet()){
            System.out.println(word + " : " + wordcount.get(word));
        }
    }

    /**
     * Emit 1 for every word and store this as a <key, value> pair
     * @param allReviews
     * @return
     */
    private static List<KV<String, Integer>> map(List<AmazonFineFoodReview> allReviews) {
        List<KV<String, Integer>> kv_pairs = new ArrayList<KV<String, Integer>>();

        for(AmazonFineFoodReview review : allReviews) {
            Pattern pattern = Pattern.compile("([a-zA-Z]+)");
            Matcher matcher = pattern.matcher(review.get_Summary());

            while(matcher.find())
                kv_pairs.add(new KV(matcher.group().toLowerCase(), 1));
        }
        return kv_pairs;
    }


    /**
     * count the frequency of each unique word
     * @param kv_pairs
     * @return a list of words with their count
     */
    private static Map<String, Integer> reduce(List<KV<String, Integer>> kv_pairs) {
        Map<String, Integer> results = new HashMap<>();

        for(KV<String, Integer> kv : kv_pairs) {
            if(!results.containsKey(kv.getKey())) {
                results.put(kv.getKey(), kv.getValue());
            } else{
                int init_value = results.get(kv.getKey());
                results.replace(kv.getKey(), init_value, init_value+kv.getValue());
            }
        }
        return results;
    }


    public static void countWords(String inputFile, String outputFile) {
        List<AmazonFineFoodReview> allReviews = read_reviews(inputFile);

        System.out.println("Finished reading all reviews, now performing word count...");

        MyTimer myMapTimer = new MyTimer("map operation");
        myMapTimer.start_timer();
        List<KV<String, Integer>> kv_pairs = map(allReviews);
        myMapTimer.stop_timer();


        MyTimer myReduceTimer = new MyTimer("reduce operation");
        myReduceTimer.start_timer();
        Map<String, Integer> results = reduce(kv_pairs);
        myReduceTimer.stop_timer();
        myReduceTimer.print_elapsed_time();

        write_word_count(results, outputFile);

        myMapTimer.print_elapsed_time();
        myReduceTimer.print_elapsed_time();
    }

}
