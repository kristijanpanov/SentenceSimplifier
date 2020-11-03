package org.group.sensim.possequences.eval;

import edu.stanford.nlp.trees.Tree;
import org.aksw.fox.binding.FoxResponse;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.jena.rdfxml.xmloutput.impl.Basic;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.FoxBinding;
import org.group.sensim.possequences.POSMarker;
import org.group.sensim.possequences.eval.reader.NifReader;
//import org.group.sensim.possequences.POSMarker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class serves to evauluate the correctness of mapping POS-sequence to an entity.
 */
public class EvaluationSequenceSelector {
    public final static Logger log = LogManager.getLogger(EvaluationSequenceSelector.class);

    public static void main(String[] args) {
        //Evaluation_1: 3 combinations in order to perform 'leave-one-out cross' validation. Terbil-annotated resources
        //     processFirstLeaveOneOutCrossValidation();
        //     processSecondLeaveOneOutCrossValidation();
        //     processThirdLeaveOneOutCrossValidation();
        // processAdditional...()

        //Evaluation_2: Take the results from FOX as basis (assume correct), instead of gerbil-annotated resources.
        processFirstEvaluationBasedOnFOX();

    }

    /**
     * First combination for the 'leave-one-out' cross validation.
     * POS-Sequences:
     * trained on (extracted from): RSS-500.ttl, ReutersTest.ttl
     * tested on: oke-challenge2018-training.ttl
     */
    private static void processFirstLeaveOneOutCrossValidation() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        testTtlFile.add("./src/main/resources/eval/oke-challenge2018-training.ttl");
        //testTtlFile.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");

        processEvaluation(posMarker, testTtlFile);
    }

    /**
     * First combination for the 'leave-one-out' cross validation.
     * POS-Sequences:
     * trained on (extracted from): RSS-500.ttl, oke-challenge2018-training.ttl
     * tested on: ReutersTest.ttl
     */
    private static void processSecondLeaveOneOutCrossValidation() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        testTtlFile.add("./src/main/resources/eval/ReutersTest.ttl");
        //testTtlFile.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");

        processEvaluation(posMarker, testTtlFile);
    }

    /**
     * First combination for the 'leave-one-out' cross validation.
     * POS-Sequences:
     * trained on (extracted from): ReutersTest.ttl, oke-challenge2018-training.ttl
     * tested on: RSS-500.ttl
     */
    private static void processThirdLeaveOneOutCrossValidation() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        testTtlFile.add("./src/main/resources/eval/RSS-500.ttl");
        testTtlFile.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");

        processEvaluation(posMarker, testTtlFile);
    }

    /**
     * First combination for the 'leave-one-out' cross validation.
     * POS-Sequences:
     * trained on (extracted from): ReutersTest.ttl, oke-challenge2018-training.ttl, RSS-500.ttl
     * tested on: dbpedia-spotlight-nif.ttl
     */
    private static void processAdditionalLeaveOneOutCrossValidation() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        //testTtlFile.add("./src/main/resources/eval/RSS-500.ttl");
        testTtlFile.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");

        processEvaluation(posMarker, testTtlFile);
    }

    /**
     * Process main evaluation. POS-sequences extracted from the gerbil-datasets(ttl, but pre-saved as txt) and tested on gerbil-datasets (ttl).
     *
     * @param posMarker    - the posMarker, which checks the relevance of a sentence with already pre-loaded pos-sequences.
     * @param testTtlFiles - list of ttl-files. On these files is the test executed.
     */
    private static void processEvaluation(POSMarker posMarker, List<String> testTtlFiles) {
        NifReader nf = new NifReader();
        EvaluationDataManager evalDataMgr = new EvaluationDataManager();

        for (String testFile : testTtlFiles) {
            List<Document> docs = nf.readData(testFile);

            docs.forEach(basisDoc -> {
                //String simpleSentences = ss.simplifyFactualComplexSentence(basisDoc.getText());

                Map<Integer, String> entities = NifReader.getEntities(basisDoc);
                NifReader.printNifDocument(basisDoc);

                log.info("Extracting POS tags from: [" + basisDoc.getText() + "]");
                String sentPosTags = posMarker.extractPOStags(basisDoc.getText());

                boolean sentenceDeclaredAsRelevant = posMarker.checkSentenceRelevance(basisDoc.getText());
                log.info("Sentence declared as relevant: " + sentenceDeclaredAsRelevant);

                if (sentenceDeclaredAsRelevant) {
                    //it should be relevant --> TP
                    if (entities.keySet().size() > 0) {
                        evalDataMgr.incrementTP();
                    }
                    //it should not be relevant --> FP
                    else {
                        evalDataMgr.incrementFP();
                    }
                } else {
                    //it should be relevant --> FN
                    if (entities.keySet().size() > 0) {
                        evalDataMgr.incrementFN();
                    }

                    //it should not be relevant --> FP // not interesting case.
                }
            });
        }
        evalDataMgr.printResults();
    }

    private static void processFirstEvaluationBasedOnFOX() {
        //initialize pre-saved pos-sequences
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");
        POSMarker posMarker = new POSMarker(posTagsFiles);

        List<String> testFiles = new ArrayList<>();
        testFiles.add("./src/main/resources/datasets/simpleWiki_.ori.test.dst");

        processFOXEvaluation(posMarker, testFiles);

    }

    private static void processFOXEvaluation(POSMarker posMarker, List<String> testFiles) {
        EvaluationDataManager evalDataMgr = new EvaluationDataManager();

        BufferedReader reader;
        for (String testFile : testFiles) {
            try {
                log.info("Testing file: " + testFile);
                reader = new BufferedReader(new FileReader(testFile));
                String sentence = reader.readLine();
                while (sentence != null) {
                    sentence = reader.readLine();
                    if (sentence != null) {
                        FoxResponse foxResponse = FoxBinding.sendRequest(sentence);
                        boolean sentenceFOXRelevant = (foxResponse.getEntities().size() > 0);

                        boolean sentenceSystemRelevant = posMarker.checkSentenceRelevance(sentence);
                        log.info("Sentence declared as relevant: " + sentenceSystemRelevant);

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
                            }
                            else{
                                //it should not be relevant --> TN // not interesting case.
                                evalDataMgr.incrementTN();
                            }
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.info("Testing file: " + testFile + " finished. ");

        }
        evalDataMgr.printResults();

    }


}
