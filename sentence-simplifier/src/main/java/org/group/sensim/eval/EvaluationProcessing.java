package org.group.sensim.eval;


import org.aksw.fox.binding.FoxResponse;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.group.sensim.SentenceSimplifier;
import org.group.sensim.eval.reader.NifReader;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class EvaluationProcessing {
    private static final Log log = LogFactory.getLog(EvaluationProcessing.class);

    //delete this one in the future. Used for testing small amounts of processed files.
    final static int processNumberDocs = 1220;
    static int counterProcessedDocs = 0;

    public EvaluationProcessing(){
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        EvaluationProcessing ep = new EvaluationProcessing();

        //evaluationRefDatasetWithFox(docs, ss);
        ep.evaluationEntitites();
        //evaluationRelationExtraction(docs, ss);
    }

    private void evaluationEntitites( ) {
        NifReader nf = new NifReader();

        List<String> testFiles = new ArrayList<String>();
        testFiles.add("./src/main/resources/eval/ReutersTest.ttl");
//        testFiles.add("./src/main/resources/eval/RSS-500.ttl");
//        testFiles.add("./src/main/resources/eval/oke-challenge2018-training.ttl");


        for (String testFile : testFiles) {
            List<Document> docs = nf.readData(testFile);
            compareBasisWithFOXoriginal(docs);
            compareBasisWithFOXsimplified(docs);
        }

        //1. compare Entities: basis x original-FOX
        //2. compare Entities: basis x simplified-FOX
        //3. compare Entities: basis x selected+simplified-FOX


    }

    private void compareBasisWithFOXoriginal(List<Document> docs) {
        log.info("Starting evaluation [ basis : fox-original ]");
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

    private void compareBasisWithFOXsimplified(List<Document> docs) {
        SentenceSimplifier ss = SentenceSimplifier.getInstance();
        EvaluationDataManager evalManagerBasisSimplified= new EvaluationDataManager();
        log.info("Starting evaluation [ basis : fox-simplified ]");
        docs.forEach(basisDoc -> {
            try {
                String simpleSentences = String.join(" ", ss.simplifyFactualComplexSentenceAditional(basisDoc.getText()));

                FoxResponse simplifiedFoxResponse = FoxBinding.sendRequest(simpleSentences);
                evaluateNE(basisDoc, simplifiedFoxResponse, evalManagerBasisSimplified);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        evalManagerBasisSimplified.printResults();
    }

    private static String formatResults(String basisText, String simplText, FoxResponse basisFoxResponse, FoxResponse simplifiedFoxResponse) {
        String formatted = "\nBasis: " + basisText + "\nSimplf:" + simplText + "\n\n";

        formatted += "Entities:\nBasis: [" + basisFoxResponse.getEntities().size() + "] --- " + basisFoxResponse.getEntities()
                + "\nSimplf: [" + simplifiedFoxResponse.getEntities().size() + "] --- " + simplifiedFoxResponse.getEntities();

        formatted += "\nRelations:\nBasis: [" + basisFoxResponse.getRelations().size() + "] --- " + basisFoxResponse.getRelations()
                + "\nSimplf: [" + simplifiedFoxResponse.getRelations().size() + "] --- " + simplifiedFoxResponse.getRelations();

        return formatted;
    }

    private static void evaluationRefDatasetWithFox(List<Document> docs, SentenceSimplifier ss) {
        EvaluationDataManager evalManager = new EvaluationDataManager();
        docs.forEach(basisDoc -> {
            try {
                FoxResponse foxResponse = processDocumentWithFox(basisDoc);
                evaluateNE(basisDoc, foxResponse, evalManager);
                ss.simplifyFactualComplexSentence(basisDoc.getText());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
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

        //FoxBinding.printFormattedResponse(foxResponse);
        log.info(" <--------- FOX: extracting NER/RE from Doc with Uri: " + doc.getDocumentURI());

        return foxResponse;
    }

    /** Comparing basisDoc with foxResponseDoc against Entities per document.
     *
     * @param basisDoc
     * @param foxResponse
     */
    private static void evaluateNE(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing basisDoc with foxResponseDoc");
        NifReader.printNifDocument(basisDoc);
        FoxBinding.printFormattedResponse(foxResponse);
        Set<String> basisEntityWords = NifReader.getEntityWords(basisDoc);
        Set<String> foxEntityWords = FoxBinding.getEntityWord(foxResponse);

        //count TP, FP
        //TODO compare the links of the entities and consider an entity only once (ignore multiple occurrences).

        for (String foxWord : foxEntityWords){
            //System.out.println("foxUri: " + foxWord);
            // ----> TP
            if (basisEntityWords.contains(foxWord)) {
                System.out.println("TP <--- basis contains the fox word: " + foxWord);
                evalManager.incrementTP();
            }
            // ----> FP
            else{
                System.out.println("FP <----  basis does NOT contain fox word: " + foxWord);
                evalManager.incrementFP();
            }
        }

        for (String baseWord : basisEntityWords){
            //System.out.println("baseUrl: " + baseWord);
            // ----> FN
            if (!foxEntityWords.contains(baseWord)){
                System.out.println("FN <---- fox does not contain basis word: " + baseWord);
                evalManager.incrementFN();
            }
        }

        System.out.println();
        log.info("<--------- comparing basisDoc with foxResponseDoc");

        counterProcessedDocs++;

        if(processNumberDocs <= counterProcessedDocs) {
            evalManager.printResults();

            System.exit(1); //process only the first document... //TODO remove this one.
        }
    }
}
