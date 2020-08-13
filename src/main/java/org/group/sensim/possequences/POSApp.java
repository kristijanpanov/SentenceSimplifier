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
import java.net.MalformedURLException;
import java.util.List;

public class POSApp {

    private static POSMarker posMarker = new POSMarker();
    public final static Logger log = LogManager.getLogger(POSApp.class);

    public static void main(String[] args) throws MalformedURLException {

        String sentence = "Nikola Tesla moved to North America.";
        FoxResponse response;

        NifReader nf = new NifReader();
        String testFile = "./src/main/resources/eval/ReutersTest.ttl";
        List<Document> docs = nf.readData(testFile);
        SentenceSimplifier ss = SentenceSimplifier.getInstance();

        docs.forEach(basisDoc -> {
            try {
                Tree posTree;

                String simpleSentences = ss.simplifyFactualComplexSentence(basisDoc.getText());
                FoxResponse simplifiedFoxResponse = FoxBinding.sendRequest(simpleSentences);

                log.info("Extracting POS tags from: [" + simpleSentences + "]");
                posTree = posMarker.extractPOStags(simpleSentences);
                log.info("POS Tags: [" + posTree.toString() + "]");

                posMarker.printPOSofEntities(posTree, simplifiedFoxResponse.getEntities());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });

//        log.info("Extracting POS tags from: [" + sentence + "]");
//        posTree = posMarker.extractPOStags(sentence);
//        log.info("POS Tags: [" + posTree.toString() + "]");
//
//        response = FoxBinding.sendRequest(sentence);
//
//        posMarker.printPOSofEntities(posTree, response.getEntities());
    }
}
