package org.group.sensim.possequences;

import edu.stanford.nlp.trees.Tree;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.eval.reader.NifReader;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Updates the POS-sequences, which point to an entity and saves them into a document.
 * This document of POS-sequences is then used to recognize entities in the future.
 */
public class POSEntityUpdator {

    private static POSMarker posMarker = new POSMarker();
    public final static Logger log = LogManager.getLogger(POSApp.class);

    /**
     * Used to find out POS-sequences, which point to an entity.
     * @param args
     * @throws MalformedURLException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
        //String testFile = "./src/main/resources/eval/RSS-500.ttl";
        // String testFile = "./src/main/resources/eval/ReutersTest.ttl";
        // String testFile = "./src/main/resources/eval/oke-challenge2018-training.ttl";
        // String testFile = "./src/main/resources/eval/dbpedia-spotlight-nif.ttl";

        List<String> resourceFiles = new ArrayList<>();
//        resourceFiles.add("./src/main/resources/eval/ReutersTest.ttl");
//        resourceFiles.add("./src/main/resources/eval/RSS-500.ttl");
//        resourceFiles.add("./src/main/resources/eval/oke-challenge2018-training.ttl");
//        resourceFiles.add("./src/main/resources/eval/dbpedia-spotlight-nif.ttl");
        for (String testF : resourceFiles){
            extractPOSfromTtl(testF);
        }



    }

    /**
     * Extracts POS tags from a given .ttl-file and writes the results in a text file.
     * Used to find out the Tags pointing to an entity.
     *
     * @param ttlFile
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void extractPOSfromTtl(String ttlFile) throws FileNotFoundException, UnsupportedEncodingException {
        NifReader nf = new NifReader();
        List<Document> docs = nf.readData(ttlFile);
        //SentenceSimplifier ss = SentenceSimplifier.getInstance();

        PrintWriter writer = new PrintWriter("src/main/resources/possequences/marking_entityToPOS_" + ttlFile.substring(26, ttlFile.length()-5) + ".txt", "UTF-8");

//        PrintWriter writer = new PrintWriter("src/main/resources/possequences/marking_entityToPOS_RSS.txt", "UTF-8");
//        PrintWriter writer = new PrintWriter("src/main/resources/possequences/marking_entityToPOS_Reuters.txt", "UTF-8");
//        PrintWriter writer = new PrintWriter("src/main/resources/possequences/marking_entityToPOS_dbPedia-spotlight.txt", "UTF-8");
//        PrintWriter writer = new PrintWriter("src/main/resources/possequences/marking_entityToPOS_oke-challenge2018.txt", "UTF-8");

        docs.forEach(basisDoc -> {
            Tree posTree;
            //String simpleSentences = ss.simplifyFactualComplexSentence(basisDoc.getText());

            Map<Integer, String> entities = NifReader.getEntities(basisDoc);
            NifReader.printNifDocument(basisDoc);

            log.info("Extracting POS tags from: [" + basisDoc.getText() + "]");
            posTree = posMarker.extractPOStags(basisDoc.getText());
            log.info("POS Tags: [" + posTree.toString() + "]");

            String entityPos = posMarker.extractPOSofEntities(posTree, entities, writer);
        });

        writer.close();
    }

}
