package org.group.sensim.possequences;

import edu.stanford.nlp.trees.Tree;
import org.aksw.fox.binding.FoxResponse;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.AnalysisUtilities;
import org.group.sensim.GlobalProperties;
import org.group.sensim.ParseResult;
import org.group.sensim.SentenceSimplifier;
import org.group.sensim.eval.FoxBinding;
import org.group.sensim.eval.reader.NifReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class POSApp {

    private static POSMarker posMarker = new POSMarker();
    public final static Logger log = LogManager.getLogger(POSApp.class);

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException {

        String sentence = "Nikola Tesla moved to North America.";
        FoxResponse response;
        String testFile = "./src/main/resources/eval/ReutersTest.ttl";
        extractPOSfromTtl(testFile);
    }

    //TODO ne gi simplificiri, iskari direkt POS tags recenicite. Vidi na kakov Entity ima kakov POS-Tag.
    // Napisi go tva u edna lista dokument.
    //ako ti se malko primerite, najdi uste eden .ttl dokument.
    //TODO produzi dalje.
    //TODO napisi si points so sakas da gi razgovaras utre na termino.
    private static void extractPOSfromTtl(String ttlFile) throws FileNotFoundException, UnsupportedEncodingException {
        NifReader nf = new NifReader();
        List<Document> docs = nf.readData(ttlFile);
        SentenceSimplifier ss = SentenceSimplifier.getInstance();
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
