package org.group.sensim.possequences;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


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



//    public static void main(String[] args) {
//        //TODO for now testing simplification. In the future move this in mainApp.
//        SentenceSelector selector = new SentenceSelector();
//        SentenceTransformer sTransf = new SentenceTransformer();
//        SentenceSimplifier ss = SentenceSimplifier.getInstance();
//        //Map<Question, String> questionSentence = new HashMap<>();
//
//        String corpusFile = "./src/main/resources/datasets/PWKP_108016_SimpleComplexSentencesPair";
//        List<String> sentences = extractLinesFromFile(corpusFile);
//
//        NifReader nf = new NifReader();
//        String testFile = "./src/main/resources/org.group.sensim.eval/ReutersTest.ttl";
//        List<Document> docs = nf.readData(testFile);
//
//        docs.forEach(basisDoc -> {
//            Map<Question, String> questionSentence = ss.simplifyFactualComplexSentenceAsTree(basisDoc.getText());
//            for (Question q : questionSentence.keySet()) {
//                //if sentence Relevant, then further processing.
//                if (selector.isSentenceRelevant(q)) {
//                    //further simplifications happen here. This method should return something :)
//                    sTransf.transformer(q.getIntermediateTree());
//                }
//            }
//        });
//
//    }

//        for (String sentence : sentences){
//            if (sentence.length()>0) {
//                questionSentence = ss.simplifyFactualComplexSentenceAsTree(sentence);
//
//                for (Question q  : questionSentence.keySet()){
//                    //if sentence Relevant, then further processing.
//                    if (selector.isSentenceRelevant(q)){
//
//                        //further simplifications happen here. This method should return something :)
//                        sTransf.transformer(q.getIntermediateTree());
//
//                    }
//                }
//            }
//        }




    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
//        SentenceSimplifier ss = SentenceSimplifier.getInstance();
//        ss.simplifyFactualComplexSentenceAditional("Geographers study not only the physical details of the environment but also its impact on human and wildlife ecologies, weather and climate patterns, economics, and culture.");


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
    public static void printToFile(PrintWriter writer, String line) {
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
        // relevant = posMarker.checkSentenceRelevance(sentence);
        log.info("... finished.  --------> Relevant: [" + relevant + "]");

        return relevant;
    }

//    /**
//     * Checks if a sentence contains an entity. If yes, marked as relevant and return true.
//     *
//     * @param q - the sentence to be checked.
//     * @return true, if relevant.
//     */
//    private boolean isSentenceRelevant(Question q) {
//        boolean relevant = false;
//        log.info("Checking relevance of sentence from tree [" + AnalysisUtilities.getCleanedUpYield(q.getIntermediateTree()) + "] ...");
//        relevant = posMarker.checkSentenceRelevance(q.getIntermediateTree());
//        log.info("... finished.  --------> Relevant: [" + relevant + "]");
//
//        return relevant;
//    }


}
