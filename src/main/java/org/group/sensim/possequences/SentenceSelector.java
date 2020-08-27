package org.group.sensim.possequences;

import org.aksw.fox.binding.FoxResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.FoxBinding;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class serves to select the relevant sentences.
 * Relevant sentence := a sentence containing at least one entity.
 *
 */
public class SentenceSelector {

    private static POSMarker posMarker;
    public final static Logger log = LogManager.getLogger(SentenceSelector.class);

    public SentenceSelector() {
        posMarker = new POSMarker();
    }

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
        SentenceSelector selector = new SentenceSelector();
        //selector.isSentenceRelevant("Nicola Tesla is the all-time best scientist of the whole World.");


        // -----> TODO 1. slegni datasets (newsela, wikismall/large), 2. iterate through them and select sentences as relevant/unrelevant.
        //TODO ... 2.1 simplify with HiS, then 2.2 mark as Relevant/notRelevant
        //TODO ... 2.0 simplify na drug nacin, 2.1. HiS (optional), 2.2. mark relevance
        String corpusFile = "./src/main/resources/datasets/simpleWiki_.ori.test.dst";
        List<String> sentences = extractLinesFromFile(corpusFile);
        PrintWriter writer = new PrintWriter("resultsFox_RELEVANT_ONLY_simpleWiki.txt", "UTF-8");

        for (String sentence : sentences){
            if( selector.isSentenceRelevant(sentence) ) {
                FoxResponse response = FoxBinding.sendRequest(sentence);
                if(response.getEntities().size() > 0 ) {
                    printToFile(writer, response.toString());
                }
            }
        }
        writer.close();
    }

    public static List<String> extractLinesFromFile(String file) {
        File testFile = new File(new File(file).getAbsolutePath());

        List<String> lines = null;
        try {
            lines = Files.readAllLines(testFile.toPath());
        } catch (final IOException e) {
            log.error(e.getLocalizedMessage(), e);
            log.error(testFile.toPath());
        }

        return lines;
    }

    /**
     * TODO move method to different destination.
     *
     * Prints string line to a given file.
     *
     * @param writer
     * @param line
     */
    public static void printToFile(PrintWriter writer, String line){
        writer.println(line);
    }



    /**
     * Checks if a sentence contains an entity. If yes, marked as relevant and return true.
     *
     * @param sentence - the sentence to be checked.
     * @return true, if relevant.
     */
    private boolean isSentenceRelevant(String sentence) {
        boolean relevant = false;
        log.info("Checking relevance of sentence [" + sentence + "] ...");
        relevant = posMarker.checkSentenceRelevance(sentence);
        log.info("... finished.  --------> Relevant: [" + relevant + "]");

        return relevant;
    }
}
