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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EvaluationRelationExtraction {
    private static final Log log = LogFactory.getLog(EvaluationEntityRecognition.class);
    final static int processNumberDocs = 101222;
    static int counterProcessedDocs = 0;

    public EvaluationRelationExtraction(){
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        EvaluationRelationExtraction er = new EvaluationRelationExtraction();

        //er.evaluationRelationExtractionNif();
        er.evaluationRelationExtractionJson();
    }

    /**
     * Processes evaluation from Nif file.
     */
    private void evaluationRelationExtractionNif() {
        NifReader nf = new NifReader();
        List<String> testFiles = new ArrayList<>();
        testFiles.add("./src/main/resources/eval/oke-challenge-2018-relation.ttl");

        for (String testFile : testFiles) {
            List<Document> docs = nf.readData(testFile);
            //compareRelationBasisWithFOXoriginal(docs);
            //compareRelationBasisWithFOXsimplified(docs);
        }
    }

    /**
     * Process RE evaluation from JSON file.
     *
     * Experiment 2; main evaluation
     *
     * Compares RE from original text aganst RE from simplified text.
     * KE tools: FOX and Stanford.
     */
    private void evaluationRelationExtractionJson() {
        JsonReader jsonReader = new JsonReader();
        List<String> testFiles = new ArrayList<>();
        //testFiles.add("./src/main/resources/eval/20131104-place_of_death_500.json");
        //testFiles.add("./src/main/resources/eval/20130403-place_of_birth_500.json");
        testFiles.add("./src/main/resources/eval/20130403-institution_500.json");
        Map<String, Boolean> textRelevanceMap = new HashMap<>();

        for (String testFile : testFiles) {
            try {
                textRelevanceMap =  jsonReader.extractSnippetsWithRelevance(testFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //eval with FOX
            //compareRelationJSONBasisWithFOXoriginal(textRelevanceMap);
            //compareRelationJSONBasisWithFOXsimplified(textRelevanceMap);

            //eval with Stanford (via FoxBinding)
            //compareRelationJSONBasisWithStanfordOriginal(textRelevanceMap);
            compareRelationJSONBasisWithStanfordSimplified(textRelevanceMap);
        }

    }

    /**
     * Compares relations from dataset (docs) against FOX-relations.
     * FOX input: original text with HS algo (extended)
     * @param docs - the document to extract entities from.
     */
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
    /** Comparing basisDoc (format .ttl) with foxResponseDoc against relations per document.
     *
     * @param basisDoc - the base document
     * @param foxResponse - response received from FOX containing entities and relations
     */
    private void evaluateRelationExtraction(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing against relations: basisDoc with foxResponse");
        //NifReader.printNifDocumentRelations(basisDoc);
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
        //common case: Fox has found no relations;
        else
        {
            for (Triple bTriple : basisTriples){
                log.info("FN <---- fox does not contain triple: " + bTriple);
                evalManager.incrementFN();
            }
        }
    }

    /**
     * Compares relations from dataset (docs) against FOX-relations.
     * FOX input: original text with HS algo (extended)
     * @param textRelevanceMap - Map of text and its relevance.
     */
    private void compareRelationJSONBasisWithFOXoriginal(Map<String, Boolean> textRelevanceMap) {
        log.info("Starting evaluation relation-extraction JSON: [ basis : fox-original ]");
        EvaluationDataManager evalManagerBasisOriginal = new EvaluationDataManager();

        for (String text : textRelevanceMap.keySet()){
            try {
                FoxResponse basisFoxResponse = FoxBinding.sendRequest(text);
                evaluateJSONRelationExtraction(basisFoxResponse, evalManagerBasisOriginal, textRelevanceMap.get(text));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        evalManagerBasisOriginal.printResults();
        log.info("End of evaluation relation-extraction JSON: [ basis : fox-original ]");
    }

    /**
     * Compares relations from dataset (docs) against FOX-relations.
     * FOX input: simplified text with HS algo (extended)
     * @param textRelevanceMap - Map of text and its relevance.
     */
    private void compareRelationJSONBasisWithFOXsimplified(Map<String, Boolean> textRelevanceMap) {
        log.info("Starting evaluation relation-extraction JSON: [ basis : fox-simplified ]");
        EvaluationDataManager evalManagerBasisOriginal = null;
        evalManagerBasisOriginal = new EvaluationDataManager();
        SentenceSimplifier ss = SentenceSimplifier.getInstance();

        for (String text : textRelevanceMap.keySet()){
            try {
                String simpleSentences = String.join(" ", ss.simplifyFactualComplexSentenceAditional(text));
                FoxResponse basisFoxResponse = FoxBinding.sendRequest(simpleSentences);
                evaluateJSONRelationExtraction(basisFoxResponse, evalManagerBasisOriginal, textRelevanceMap.get(text));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        evalManagerBasisOriginal.printResults();
        log.info("End of evaluation relation-extraction JSON: [ basis : fox-simplified ]");
    }

    /**
     * Compares relations from dataset (docs) against Stanford-relations.
     * Stanford input: original text with HS algo (extended)
     * @param textRelevanceMap - Map of text and its relevance.
     */
    private void compareRelationJSONBasisWithStanfordOriginal(Map<String, Boolean> textRelevanceMap) {
        log.info("Starting evaluation relation-extraction JSON:[ basis : Stanford ] input-original ");
        EvaluationDataManager evalManagerBasisOriginal = new EvaluationDataManager();

        for (String text : textRelevanceMap.keySet()){
            try {
                FoxResponse basisFoxResponse = FoxBinding.sendStanfordRequest(text);
                evaluateJSONRelationExtraction(basisFoxResponse, evalManagerBasisOriginal, textRelevanceMap.get(text));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        evalManagerBasisOriginal.printResults();
        log.info("End of evaluation relation-extraction JSON: [ basis : Stanford ] input-original");
    }

    /**
     * Compares relations from dataset (docs) against Stanford-relations.
     * Stanford input: simplified text with HS algo (extended)
     * @param textRelevanceMap - Map of text and its relevance.
     */
    private void compareRelationJSONBasisWithStanfordSimplified(Map<String, Boolean> textRelevanceMap) {
        log.info("Starting evaluation relation-extraction JSON: [ basis : Stanford ] input-simplified ");
        EvaluationDataManager evalManagerBasisOriginal = null;
        evalManagerBasisOriginal = new EvaluationDataManager();
        SentenceSimplifier ss = SentenceSimplifier.getInstance();

        for (String text : textRelevanceMap.keySet()){
            try {
                String simpleSentences = String.join(" ", ss.simplifyFactualComplexSentenceAditional(text));
                FoxResponse basisFoxResponse = FoxBinding.sendRequest(simpleSentences);
                evaluateJSONRelationExtraction(basisFoxResponse, evalManagerBasisOriginal, textRelevanceMap.get(text));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        evalManagerBasisOriginal.printResults();
        log.info("End of evaluation relation-extraction JSON: [ basis : Stanford ] input-simplified");
    }

    /**
     * Runs the main comparing and counts TP, FP, FN...
     *
     * @param foxResponse - response from FOX (can be Stanford) containing the entities/relations.
     * @param evalManagerBasisOriginal - evaluation data menager, needed for counting the results.
     * @param relationPresentInBase - boolean result of the majority votings for a snippet of text.
     */
    private void evaluateJSONRelationExtraction(FoxResponse foxResponse, EvaluationDataManager evalManagerBasisOriginal, Boolean relationPresentInBase) {
        List<Triple> foxTriples = extractFoxTriples(foxResponse);
        FoxBinding.printFormattedResponse(foxResponse);
        counterProcessedDocs++;
        log.info("Processing document number: " + counterProcessedDocs);
        //has fox found a relation
        if (foxTriples.size() > 0) {
            boolean foxRelationTypeCorrect = false;
            for (Triple foxTriple : foxTriples) {
                //FoxTypes: place_of_birth := birthPlace,
                // place_of_death := deathPlace,
                // institution := education (evidences concerning attending or graduating from an institution)
                if (foxTriple.getPredicate().contains("education")) {
                    foxRelationTypeCorrect = true;
                    break;
                }
            }
            if (relationPresentInBase) {
                if (foxRelationTypeCorrect) {
                    log.info("TP <---- fox found a relation and relation present in base.");
                    evalManagerBasisOriginal.incrementTP();
                } else {
                    log.info("FN <---- none of the relations found by fox has the correct type. A relation is present in base.");
                    evalManagerBasisOriginal.incrementFN();
                }
            } else {
                //fox has found a relation of the type, but the judgers says there is no relation of this type present in the snippet
                if (foxRelationTypeCorrect){
                    log.info("FP <---- fox found a relation of right type and relation is not present in base.");
                    evalManagerBasisOriginal.incrementFP();
                }
                //fox has found some other not-relevant relations
                else{
                    //counting not relevant.
                }
            }
        } else {
            if (relationPresentInBase) {
                log.info("FN <---- fox did not find a relation, but relation present in base.");
                evalManagerBasisOriginal.incrementFN();
            }
        }

        if(processNumberDocs <= counterProcessedDocs) {
            evalManagerBasisOriginal.printResults();
            System.exit(1); //process only the first few documents...
        }
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

    /**
     * Extracts relation-triplets from FOX response and reteurns them in a list.
     * @param foxResponse - response containing the relations.
     * @return
     */
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

}
