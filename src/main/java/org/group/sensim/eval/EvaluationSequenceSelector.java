package org.group.sensim.eval;

import edu.stanford.nlp.trees.Tree;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.reader.NifReader;
import org.group.sensim.possequences.POSApp;
import org.group.sensim.possequences.POSMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**\
 * This class serves to evauluate the correctness of mapping POS-sequence to an entity.
 */
public class EvaluationSequenceSelector {
    public final static Logger log = LogManager.getLogger(EvaluationSequenceSelector.class);

    public static void main(String[] args){

        processFirstLeaveOneOutCrossValidation();

        //TODO create analog to first
//        processSecondLeaveOneOutCrossValidation();
//        processThirdLeaveOneOutCrossValidation();
    }

    private static void processFirstLeaveOneOutCrossValidation() {
        POSMarker posMarker = new POSMarker();

        //initialize pre-saved pos-sequences
        List<String> resourceFiles = new ArrayList<>();
        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");
        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        posMarker.initSavedRelevantTags(resourceFiles);

        //initialize test-ttl file
        List<String> testTtlFile = new ArrayList<>();
        testTtlFile.add("./src/main/resources/eval/oke-challenge2018-training.ttl");

        processEvaluation(posMarker, testTtlFile);
    }


    private static void processEvaluation(POSMarker posMarker, List<String> testTtlFiles) {
        NifReader nf = new NifReader();

        for (String testFile : testTtlFiles) {
            List<Document> docs = nf.readData(testFile);

            docs.forEach(basisDoc -> {
                Tree posTree;
                //String simpleSentences = ss.simplifyFactualComplexSentence(basisDoc.getText());

                Map<Integer, String> entities = NifReader.getEntities(basisDoc);
                NifReader.printNifDocument(basisDoc);

                log.info("Extracting POS tags from: [" + basisDoc.getText() + "]");
                posTree = posMarker.extractPOStags(basisDoc.getText());
                log.info("POS Tags: [" + posTree.toString() + "]");
                log.info("POS Tree size: " + posTree.size());

                //tree is not too large and could be parsed --> only then consider the results in the evaluation.
                if (posTree.size() > 3){
                    log.info("Checking relevance of the sentence...");
                    if (posMarker.checkSentenceRelevance(basisDoc.getText())) {
                        log.info("The sentence was marked as relevant!");
                    }
                    else
                        log.info("The sentence was marked as IRRelevant");


                }




               // String entityPos = posMarker.extractPOSofEntities(posTree, entities, writer);
            });


        }

    }


}
