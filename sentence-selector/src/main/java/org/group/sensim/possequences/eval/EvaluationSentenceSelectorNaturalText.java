package org.group.sensim.possequences.eval;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.possequences.POSMarker;
import org.group.sensim.possequences.eval.reader.NifReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluationSentenceSelectorNaturalText {

    public final static Logger log = LogManager.getLogger(EvaluationSentenceSelectorNaturalText.class);

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

}
