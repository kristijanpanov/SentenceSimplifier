package org.group.sensim.eval;


import org.aksw.fox.binding.FoxResponse;

import org.aksw.fox.data.RelationSimple;
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

    //Used for testing small amounts of processed files.
    final static int processNumberDocs = 22222;
    static int counterProcessedDocs = 0;

    public EvaluationProcessing(){
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        EvaluationProcessing ep = new EvaluationProcessing();

        //ep.evaluationEntitites();
        ep.evaluationRelationExtraction();
    }

    /**
     * Document me TODO
     */
    private void evaluationEntitites( ) {
        NifReader nf = new NifReader();

        List<String> testFiles = new ArrayList<String>();
        //testFiles.add("./src/main/resources/eval/ReutersTest.ttl");
        //testFiles.add("./src/main/resources/eval/RSS-500.ttl");
        //testFiles.add("./src/main/resources/eval/oke-challenge2018-training.ttl");


        for (String testFile : testFiles) {
            List<Document> docs = nf.readData(testFile);
            //compareEntityBasisWithFOXoriginal(docs);
            compareEntityBasisWithFOXsimplified(docs);
        }

        //1. compare Entities: basis x FOX_input:original-text
        //2. compare Entities: basis x FOX_input:simplified-text
        //3. compare Entities: basis x selected+simplified-FOX
    }

    private void evaluationRelationExtraction() {
        NifReader nf = new NifReader();
        List<String> testFiles = new ArrayList<>();
        testFiles.add("./src/main/resources/eval/oke-challenge-2018-relation.ttl");

        for (String testFile : testFiles) {
            List<Document> docs = nf.readData(testFile);
            //compareRelationBasisWithFOXoriginal(docs);
            compareRelationBasisWithFOXsimplified(docs);
        }

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
        EvaluationDataManager evalManagerBasisSimplified = new EvaluationDataManager();
        log.info("Starting evaluation NE: [ basis : fox-simplified ]");
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

    private void compareRelationBasisWithFOXoriginal(List<Document> docs) {
        log.info("Starting evaluation relation-extraction: [ basis : fox-original ]");
        EvaluationDataManager evalManagerBasisOriginal = new EvaluationDataManager();

        docs.forEach(basisDoc -> {
            try {
                FoxResponse basisFoxResponse = processDocumentWithFox(basisDoc);
                evaluateRelationExtraction(basisDoc, basisFoxResponse, evalManagerBasisOriginal);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        evalManagerBasisOriginal.printResults();
        log.info("End of evaluation relation-extraction: [ basis : fox-original ]");
    }

    private void compareRelationBasisWithFOXsimplified(List<Document> docs) {
        log.info("Starting evaluation relation-extraction: [ basis : fox-simplified ]");
        EvaluationDataManager evalManagerBasisSimplified = new EvaluationDataManager();
        SentenceSimplifier ss = SentenceSimplifier.getInstance();

        docs.forEach(basisDoc -> {
            try {
                String simpleSentences = String.join(" ", ss.simplifyFactualComplexSentenceAditional(basisDoc.getText()));
                FoxResponse simplifiedFoxResponse = FoxBinding.sendRequest(simpleSentences);
                evaluateRelationExtraction(basisDoc, simplifiedFoxResponse, evalManagerBasisSimplified);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        evalManagerBasisSimplified.printResults();
        log.info("End of evaluation relation-extraction: [ basis : fox-simplified ]");
    }

    /** Comparing basisDoc with foxResponseDoc against Entities per document.
     *
     * @param basisDoc - the base document
     * @param foxResponse - response received from FOX containing entities and relations
     */
    private static void evaluateNE(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing basisDoc with foxResponseDoc");
        NifReader.printNifDocument(basisDoc);
        FoxBinding.printFormattedResponse(foxResponse);
        Set<String> basisEntityWords = NifReader.getEntityWords(basisDoc);
        Set<String> foxEntityWords = FoxBinding.getEntityWord(foxResponse);

        for (String foxWord : foxEntityWords){
            //System.out.println("foxUri: " + foxWord);
            // ----> TP: entity found by fox and entity marked in the database.
            if (basisEntityWords.contains(foxWord)) {
                log.info("TP <--- basis contains the fox word: " + foxWord);
                evalManager.incrementTP();
            }
            // ----> FP: entity found by fox, but entity not marked in the database.
            else{
                log.info("FP <----  basis does NOT contain fox word: " + foxWord);
                evalManager.incrementFP();
            }
        }

        for (String baseWord : basisEntityWords){
            //System.out.println("baseUrl: " + baseWord);
            // ----> FN: entity not found by fox, but entity marked in the database.
            if (!foxEntityWords.contains(baseWord)){
                log.info("FN <---- fox does not contain basis word: " + baseWord);
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

    /** Comparing basisDoc (format .ttl) with foxResponseDoc against Entities per document.
     *
     * @param basisDoc - the base document
     * @param foxResponse - response received from FOX containing entities and relations
     */
    private void evaluateRelationExtraction(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing against relations: basisDoc with foxResponse");
        NifReader.printNifDocumentRelations(basisDoc);
        FoxBinding.printFormattedResponse(foxResponse);

        List<Triple> basisTriples = NifReader.extractTriples(basisDoc);
        List<Triple> foxOrgTriples = extractFoxTriples(foxResponse);

        if (foxOrgTriples.size()>0) {
            for (Triple fTriple : foxOrgTriples) {
                boolean tripleMatch = false;
                for (Triple bTriple : basisTriples){
                    if (fTriple.getSubject().equals(bTriple.getSubject())
                            && fTriple.getPredicate().equals(bTriple.getPredicate())
                            && fTriple.getObject().equals(bTriple.getObject())){
                        tripleMatch = true;
                        break;
                    }
                }
                //basis and fox contain same relation --> TP
                if (tripleMatch){
                    log.info("TP <---- triple present in basis and recognized by fox: " + fTriple);
                    evalManager.incrementTP();
                }
                //basis does not contain the relation from FOX --> FP
                else {
                    log.info("FP <---- fox-triple is not present in basis: " + fTriple);
                    evalManager.incrementFP();
                }
            }
            //check triples in basis, but not in fox:
            for (Triple bTriple : basisTriples){
                boolean tripleMatch = false;
                for (Triple fTriple : foxOrgTriples) {
                    if (fTriple.getSubject().equals(bTriple.getSubject())
                            && fTriple.getPredicate().equals(bTriple.getPredicate())
                            && fTriple.getObject().equals(bTriple.getObject())){
                        tripleMatch = true;
                        break;
                    }
                }
                // if no match, means basis-triple was not recognized by fox --> FN
                if (!tripleMatch){
                    log.info("FN <---- basis-triple was not recognized by fox: " + bTriple);
                    evalManager.incrementFN();
                }
                //else: this match is already counted as TP in the loop above
            }
        }
        //common case: fox found no relations;
        else
        {
            for (Triple bTriple : basisTriples){
                log.info("FN <---- fox does not contain triple: " + bTriple);
                evalManager.incrementFN();
            }
        }


    }

    private List<Triple> extractFoxTriples(FoxResponse foxResponse) {
        List<Triple> foxTriples = new ArrayList<>();
        String subj;
        String pred;
        String obj;

        for (RelationSimple rel : foxResponse.getRelations()) {
            subj = rel.toString().substring(rel.toString().indexOf("s=")+2, rel.toString().indexOf("p=") - 2);
            pred = rel.toString().substring(rel.toString().indexOf("p=")+2, rel.toString().indexOf("o=") - 2);
            obj = rel.toString().substring(rel.toString().indexOf("o=")+2, rel.toString().indexOf("tool=") - 2);

            foxTriples.add(new Triple(subj, pred, obj));
        }

        return foxTriples;
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
