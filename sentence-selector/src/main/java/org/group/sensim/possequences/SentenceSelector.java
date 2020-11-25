package org.group.sensim.possequences;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;

/**
 * !Currently unused.!
 *
 * (This class serves to select the relevant sentences.
 * Relevant sentence := a sentence containing at least one entity.)
 */
public class SentenceSelector {

    private static POSMarker posMarker;
    public final static Logger log = LogManager.getLogger(SentenceSelector.class);

    public SentenceSelector() {
        posMarker = new POSMarker();
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
