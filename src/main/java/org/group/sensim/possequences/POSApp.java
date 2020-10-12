package org.group.sensim.possequences;

import edu.stanford.nlp.trees.Tree;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.SentenceSimplifier;
import org.group.sensim.eval.reader.NifReader;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class POSApp {

    private static POSMarker posMarker = new POSMarker();
    public final static Logger log = LogManager.getLogger(POSApp.class);

    /**
     * Used to find out POS-tags, which point to an entity.
     * @param args
     * @throws MalformedURLException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
        // String testFile = "./src/main/resources/eval/ReutersTest.ttl";
        String testFile = "./src/main/resources/eval/RSS-500.ttl";
        extractPOSfromTtl(testFile);

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
        PrintWriter writer = new PrintWriter("marking_entityToPOS.txt", "UTF-8");

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
