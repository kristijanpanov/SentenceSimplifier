package org.group.sensim.eval;


import org.aksw.fox.binding.FoxResponse;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.group.sensim.CorefResolutor;
import org.group.sensim.SentenceSimplifier;
import org.group.sensim.eval.reader.NifReader;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class EvaluationProcessing {
    private static final Log log = LogFactory.getLog(EvaluationProcessing.class);

    final static int processNumberDocs = 130; //TODO delete this one in the future. Used for testing.
    static int counterProcessedDocs = 0;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        NifReader nf = new NifReader();
        String testFile = "./src/main/resources/eval/ReutersTest.ttl";
        List<Document> docs = nf.readData(testFile);
        SentenceSimplifier ss = SentenceSimplifier.getInstance();

        //evaluationRefDatasetWithFox(docs, ss);
        evaluationFoxSimplRefDataset(docs, ss);
    }

    private static void evaluationFoxSimplRefDataset(List<Document> docs, SentenceSimplifier ss) throws FileNotFoundException, UnsupportedEncodingException {

        EvaluationDataManager evalManager = new EvaluationDataManager();
        PrintWriter writer = new PrintWriter("results_Fox.txt", "UTF-8");
       // CorefResolutor coref = new CorefResolutor();

        docs.forEach(basisDoc -> {
            try {
                FoxResponse basisFoxResponse = processDocumentWithFox(basisDoc);
                //evaluateNER(basisDoc, foxResponse, evalManager);
                String simpleSentences = ss.simplifyFactualComplexSentence(basisDoc.getText());
                System.out.println("SIMPLE SENTENCES: " + simpleSentences + "\n extracting coref...");
              //  coref.getCoref(simpleSentences);

                FoxResponse simplifiedFoxResponse = FoxBinding.sendRequest(simpleSentences);

//                FoxBinding.printFormattedResponse(basisFoxResponse);
//                FoxBinding.printFormattedResponse(simplifiedFoxResponse);


                writer.println( formatResults(basisDoc.getText(), simpleSentences, basisFoxResponse, simplifiedFoxResponse ) );

                counterProcessedDocs++;
                if(processNumberDocs <= counterProcessedDocs) {
                    writer.close();

                    //evalManager.printResults();
                    System.exit(1); //process only the first document... //TODO remove this one.
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        writer.close();
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
                evaluateNER(basisDoc, foxResponse, evalManager);
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

    /**
     * @param basisDoc
     * @param foxResponse
     */
    private static void evaluateNER(Document basisDoc, FoxResponse foxResponse, EvaluationDataManager evalManager) {
        log.info("---------> comparing basisDoc with foxResponseDoc");
        NifReader.printNifDocument(basisDoc);
        FoxBinding.printFormattedResponse(foxResponse);
        Map<Integer, String> basisEntities = NifReader.getEntities(basisDoc);
        Map<Integer, String> foxEntities = FoxBinding.getEntities(foxResponse);


        //count TP, FP
        for (Integer posFoxEntity : foxEntities.keySet()) {
            System.out.print("evaluateNER--> fox Entity on position: " + posFoxEntity);
            if (basisEntities.containsKey(posFoxEntity)) {
                if (basisEntities.get(posFoxEntity).equals(foxEntities.get(posFoxEntity))) {
                    System.out.println(" [" + basisEntities.get(posFoxEntity) + "] --> TP");
                    evalManager.incrementTP();
                } else
                    System.out.println(basisEntities.get(posFoxEntity) + " != " + foxEntities.get(posFoxEntity) + " --> FP");
                    evalManager.incrementFP();
            } else {
                System.out.println(" Entity [" + foxEntities.get(posFoxEntity) + "] was not found in the basis document. --> FP");
                evalManager.incrementFP();
            }
        }

        //count FN
        for (Integer posEntityBasis : basisEntities.keySet()) {
            if (!foxEntities.containsKey(posEntityBasis)) {
                System.out.println("evaluateNER--> basisE Entity: [" + basisEntities.get(posEntityBasis) + "] --> FN");
                evalManager.incrementFN();
            } else {
                //position of entity found, but entity different
                if (!basisEntities.get(posEntityBasis).equals(foxEntities.get(posEntityBasis))) {
                    System.out.println(basisEntities.get(posEntityBasis) + " != " + foxEntities.get(posEntityBasis) + " --> FN");
                    evalManager.incrementFN();
                }
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
