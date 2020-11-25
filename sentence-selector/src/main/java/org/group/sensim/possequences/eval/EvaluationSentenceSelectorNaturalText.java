package org.group.sensim.possequences.eval;

import org.aksw.fox.binding.FoxResponse;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.FoxBinding;
import org.group.sensim.possequences.POSMarker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationSentenceSelectorNaturalText {

    public final static Logger log = LogManager.getLogger(EvaluationSentenceSelectorNaturalText.class);

    final static int processNumberDocs = 1000;
    static int counterProcessedDocs = 0;

    public static void main(String[] args) {
        processEvaluationSentenceSelector();
    }

    /**
     * First combination for the 'leave-one-out' cross validation.
     * POS-Sequences:
     * trained on (extracted from): RSS-500.ttl, ReutersTest.ttl
     * tested on: oke-challenge2018-training.ttl
     */
    private static void processEvaluationSentenceSelector() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        testTtlFile.add("./src/main/resources/datasets/SimpleComplexSentencesPair_wiki");
        processFOXEvaluation(posMarker, testTtlFile);
    }

    /**
     * Runs the evaluation with FOX as base-rasult.
     *
     * @param posMarker - the posMarker which marks the sentences as candidates/relevant.
     * @param testFiles - dataset-file.
     */
    private static void processFOXEvaluation(POSMarker posMarker, List<String> testFiles) {
        EvaluationDataManager evalDataMgr = new EvaluationDataManager();
        int countProcessedSentences = 0;
        BufferedReader reader;
        for (String testFile : testFiles) {
            try {
                log.info("Testing file: " + testFile);
                reader = new BufferedReader(new FileReader(testFile));
                String sentence = reader.readLine();
                while (sentence != null) {
                    sentence = reader.readLine();
                    if (sentence != null && sentence.length() > 1) {
                        countProcessedSentences++;
                        if (countProcessedSentences < 6000){ //skip first 1000
                            continue;
                        }

                        FoxResponse foxResponse = FoxBinding.sendRequest(sentence);
                        boolean sentenceFOXRelevant = (foxResponse.getEntities().size() > 0);
                        boolean sentenceSystemRelevant = posMarker.checkSentenceRelevance(sentence);

                        log.info("Sentence declared as relevant from FOX: " + sentenceFOXRelevant);
                        log.info("Sentence declared as relevant from System: " + sentenceSystemRelevant);

                        if (sentenceSystemRelevant) {
                            //it should be relevant --> TP
                            if (sentenceFOXRelevant) {
                                evalDataMgr.incrementTP();
                            }
                            //it should not be relevant --> FP
                            else {
                                evalDataMgr.incrementFP();
                            }
                        } else {
                            //it should be relevant --> FN
                            if (sentenceFOXRelevant) {
                                evalDataMgr.incrementFN();
                                log.info("Sentence marked as irrelevant but FOX has found entities.");
                            }
                            else{
                                //it should not be relevant --> TN // not interesting case.
                                evalDataMgr.incrementTN();
                            }
                        }
                        counterProcessedDocs++;
                    }
                    if(counterProcessedDocs > processNumberDocs) {
                        evalDataMgr.printResults();
                        System.exit(1); //process only the first few documents...
                    }
                }
                reader.close();
            } catch (IOException e) {
                log.info("Exception Occured. Current results: " + countProcessedSentences);
                evalDataMgr.printResults();
                e.printStackTrace();
            }
            log.info("Testing file: " + testFile + " finished. Processed sentences: " + countProcessedSentences);
        }
        evalDataMgr.printResults();
    }

}
