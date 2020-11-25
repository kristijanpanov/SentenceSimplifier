package org.group.sensim.possequences.eval;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.reader.JsonReader;
import org.group.sensim.possequences.POSMarker;
import org.group.sensim.possequences.eval.reader.NifReader;
//import org.group.sensim.possequences.POSMarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class serves to evauluate the correctness of mapping POS-sequence to an entity.
 */
public class EvaluationSequenceSelectorAnnotatedData {
    public final static Logger log = LogManager.getLogger(EvaluationSequenceSelectorAnnotatedData.class);

    public static void main(String[] args) {
        //Evaluation_1: 3 combinations in order to perform 'leave-one-out cross' validation. Gerbil-annotated resources
//             processFirstLeaveOneOutCrossValidation();
//             processSecondLeaveOneOutCrossValidation();
//             processThirdLeaveOneOutCrossValidation();

//        processAdditionalLeaveOneOutCrossValidation();

        //Evaluation_2: Take the results from google-json-datasets as basis (assume correct),
        processJsonEvaluation();


    }

    private static void processJsonEvaluation() {
        List<String> posTagsFiles = new ArrayList<>();
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        posTagsFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");

        POSMarker posMarker = new POSMarker(posTagsFiles);

        //initialize test-ttl file
        List<String> testJsonFile = new ArrayList<>();
        testJsonFile.add("./src/main/resources/eval/20130403-institution_500.json");
        testJsonFile.add("./src/main/resources/eval/20130403-place_of_birth_500.json");
        testJsonFile.add("./src/main/resources/eval/20131104-place_of_death_500.json");

        processEvaluationJson(posMarker, testJsonFile);
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
        testTtlFile.add("./src/main/resources/eval/ReutersTest.ttl");
        //testTtlFile.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");

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
                        log.info("Increasing FN : Because the entities are contained: " + entities);
                    }
                    //it should not be relevant --> FP // not interesting case.
                }
            });
        }
        evalDataMgr.printResults();
    }

    /**
     * Process evaluation (pre-experiment).
     * Base-results: majority votes from the judges.
     * Actual-result: posMarker.checkSentenceRelevance
     *
     * @param posMarker    - the posMarker, which checks the relevance of a sentence with already pre-loaded pos-sequences.
     * @param testJsonFile - list of ttl-files. On these files is the test executed.
     */
    private static void processEvaluationJson(POSMarker posMarker, List<String> testJsonFile) {
        JsonReader jsonReader = new JsonReader();
        EvaluationDataManager evalDataMgr = new EvaluationDataManager();

        for (String testFile : testJsonFile) {
            Map<String, Boolean> snippetsWithRelevance = null;
            try {
                snippetsWithRelevance = jsonReader.extractSnippetsWithRelevance(testFile);

                for (String snippet : snippetsWithRelevance.keySet()) {

                    //this gives as the majority of judges have voted for presence of a relation, actually in every doc is an entity present (subj and obj)
                    //boolean snippetRelevant = snippetsWithRelevance.get(snippet);
                    boolean snippetRelevant = true;
                    boolean sentenceDeclaredAsRelevant = posMarker.checkSentenceRelevance(snippet);
                    log.info("Sentence declared as relevant in json: " + snippetRelevant);
                    log.info("Sentence declared as relevant from system: " + sentenceDeclaredAsRelevant);

                    if (sentenceDeclaredAsRelevant) {
                        //it should be relevant --> TP
                        if (snippetRelevant) {
                            evalDataMgr.incrementTP();
                        }
                        //it should not be relevant --> FP
                        else {
                            evalDataMgr.incrementFP();
                        }
                    } else {
                        //it should be relevant --> FN
                        if (snippetRelevant) {
                            evalDataMgr.incrementFN();
                            log.info("Sentence marked as irrelevant but json-snippet has entities.");
                        } else {
                            //it should not be relevant --> TN // not interesting case.
                            evalDataMgr.incrementTN();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            evalDataMgr.printResults();
        }
    }
}
