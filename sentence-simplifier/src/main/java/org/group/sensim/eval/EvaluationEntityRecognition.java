package org.group.sensim.eval;


import org.aksw.fox.binding.FoxResponse;

import org.aksw.fox.data.RelationSimple;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.group.sensim.SentenceSimplifier;
import org.group.sensim.eval.reader.JsonReader;
import org.group.sensim.eval.reader.NifReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;


public class EvaluationEntityRecognition {
    private static final Log log = LogFactory.getLog(EvaluationEntityRecognition.class);

    //Used for testing small amounts of processed files.
    final static int processNumberDocs = 101222;
    static int counterProcessedDocs = 0;

    public EvaluationEntityRecognition() {
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        EvaluationEntityRecognition ep = new EvaluationEntityRecognition();

        ep.evaluationEntitites();

    }

    /**
     * Document me TODO
     */
    private void evaluationEntitites() {
        NifReader nf = new NifReader();

        List<String> testFiles = new ArrayList<String>();
        //testFiles.add("./src/main/resources/eval/ReutersTest.ttl");
        testFiles.add("./src/main/resources/eval/oke-challenge2018-training.ttl");
        //testFiles.add("./src/main/resources/eval/RSS-500.ttl");

        for (String testFile : testFiles) {
            List<Document> docs = nf.readData(testFile);
            //compareEntityBasisWithFOXoriginal(docs);
            compareEntityBasisWithFOXsimplified(docs);
        }

        //1. compare Entities: basis x FOX_input:original-text
        //2. compare Entities: basis x FOX_input:simplified-text
        //3. compare Entities: basis x selected+simplified-FOX
    }

    private void compareEntityBasisWithFOXoriginal(List<Document> docs) {
        log.info("Starting evaluation NE: [ basis : fox-original ]");
        EvaluationDataManager evalManagerBasisOriginal = new EvaluationDataManager();

        docs.forEach(basisDoc -> {
            try {
                FoxResponse basisFoxResponse = processDocumentWithFox(basisDoc);
                evaluateNE(basisDoc, basisFoxResponse, evalManagerBasisOriginal);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        evalManagerBasisOriginal.printResults();
    }

    private void compareEntityBasisWithFOXsimplified(List<Document> docs) {
        SentenceSimplifier ss = SentenceSimplifier.getInstance();
        //EvaluationDataManager evalManagerBasisOrg = new EvaluationDataManager();//TODO brisi
        EvaluationDataManager evalManagerBasisSimplified = new EvaluationDataManager();
        log.info("Starting evaluation NE: [ basis : fox-simplified ]");
        docs.forEach(basisDoc -> {
            try {
                //log.info("Extracting NE/RE from original sentence: ..."); //TODO delete, used for comparison (simplf, vs not simplf.)
                //FoxResponse orgFoxResponse = FoxBinding.sendRequest(basisDoc.getText());//TODO delete
                String simpleSentences = String.join(" ", ss.simplifyFactualComplexSentenceAditional(basisDoc.getText()));
                //log.info("Extracting NE/RE from simplified sentence: ...");//TODO delete
                FoxResponse simplifiedFoxResponse = FoxBinding.sendRequest(simpleSentences);
                //valuateNE(basisDoc, orgFoxResponse, evalManagerBasisOrg); //TODO delete
                evaluateNE(basisDoc, simplifiedFoxResponse, evalManagerBasisSimplified);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        evalManagerBasisSimplified.printResults();
    }

    /**
     * Comparing basisDoc with foxResponseDoc against Entities per document.
     *
     * @param basisDoc    - the base document
     * @param foxResponse - response received from FOX containing entities and relations
     */
    private static void evaluateNE(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing basisDoc with foxResponseDoc");
        //NifReader.printNifDocument(basisDoc);
        FoxBinding.printFormattedResponse(foxResponse);
        Set<String> basisEntityWords = NifReader.getEntityWords(basisDoc);
        Set<String> foxEntityWords = FoxBinding.getEntityWord(foxResponse);

        for (String foxWord : foxEntityWords) {
            //System.out.println("foxUri: " + foxWord);
            // ----> TP: entity found by fox and entity marked in the database.
            if (basisEntityWords.contains(foxWord)) {
                log.info("TP <--- basis contains the fox word: " + foxWord);
                evalManager.incrementTP();
            }
            // ----> FP: entity found by fox, but entity not marked in the database.
            else {
                log.info("FP <----  basis does NOT contain fox word: " + foxWord);
                evalManager.incrementFP();
            }
        }

        for (String baseWord : basisEntityWords) {
            //System.out.println("baseUrl: " + baseWord);
            // ----> FN: entity not found by fox, but entity marked in the database.
            if (!foxEntityWords.contains(baseWord)) {
                log.info("FN <---- fox does not contain basis word: " + baseWord);
                evalManager.incrementFN();
            }
        }

        System.out.println();
        log.info("<--------- comparing basisDoc with foxResponseDoc");

        counterProcessedDocs++;
        if (processNumberDocs <= counterProcessedDocs) {
            evalManager.printResults();
            System.exit(1); //process only the first document...
        }

    }


    /**
     * Extracts NER/RE with FOX from a document in a ttl-nif format.
     * Returns a Fox-Response in a TTL format.
     *
     * @param doc - the Document ttl-nif to extract ner/re from.
     * @return FOX-Response (TTL Format) containing the extracted NER/RE.
     * @throws MalformedURLException when FOX has problems with the binding.
     */
    private static FoxResponse processDocumentWithFox(Document doc) throws MalformedURLException {
        String docText = doc.getText();

        log.info(" ---------> FOX: extracting NER/RE from Doc with Uri: " + doc.getDocumentURI());
        FoxResponse foxResponse = FoxBinding.sendRequest(docText);
        //FoxResponse foxResponse = FoxBinding.sendRequest("Arsene Wenger was appointed as Arsenal manager on 1 October 1996 from Japanese club Nagoya Grampus Eight.");
        //FoxBinding.printFormattedResponse(foxResponse);
        log.info(" <--------- FOX: extracting NER/RE from Doc with Uri: " + doc.getDocumentURI());

        return foxResponse;
    }

    private static String formatResults(String basisText, String simplText, FoxResponse basisFoxResponse, FoxResponse simplifiedFoxResponse) {
        String formatted = "\nBasis: " + basisText + "\nSimplf:" + simplText + "\n\n";

        formatted += "Entities:\nBasis: [" + basisFoxResponse.getEntities().size() + "] --- " + basisFoxResponse.getEntities()
                + "\nSimplf: [" + simplifiedFoxResponse.getEntities().size() + "] --- " + simplifiedFoxResponse.getEntities();

        formatted += "\nRelations:\nBasis: [" + basisFoxResponse.getRelations().size() + "] --- " + basisFoxResponse.getRelations()
                + "\nSimplf: [" + simplifiedFoxResponse.getRelations().size() + "] --- " + simplifiedFoxResponse.getRelations();

        return formatted;
    }

//    private static void evaluationRefDatasetWithFox(List<Document> docs, SentenceSimplifier ss) {
//        EvaluationDataManager evalManager = new EvaluationDataManager();
//        docs.forEach(basisDoc -> {
//            try {
//                FoxResponse foxResponse = processDocumentWithFox(basisDoc);
//                evaluateNE(basisDoc, foxResponse, evalManager);
//                ss.simplifyFactualComplexSentence(basisDoc.getText());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        });
//    }


}
