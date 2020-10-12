package org.group.sensim.possequences.relation;

import edu.stanford.nlp.trees.Tree;
import org.aksw.fox.binding.FoxResponse;
import org.apache.jena.atlas.io.AWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.AnalysisUtilities;
import org.group.sensim.eval.FoxBinding;
import org.group.sensim.possequences.POSMarker;
import org.group.sensim.possequences.SentenceSelector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SentenceTransformer {

    public final static Logger LOG = LogManager.getLogger(SentenceTransformer.class);
    private static List<String> relPOSpatterns;


    public SentenceTransformer() {
        relPOSpatterns = loadPOSPatterns();
    }


    private List<String> loadPOSPatterns() {
        List<String> posPattern = new ArrayList<String>();
        try {
            File myObj = new File("./src/main/resources/patty/patty_relation_to_POS.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                posPattern.add(myReader.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return posPattern;
    }


    //prima Tree od SS.
    public void transformer(Tree tree){
        //TODO hier list other methods, that simplify sentences...
        LOG.info("i am in Transformer.  Sentence: " + AnalysisUtilities.getCleanedUpYield(tree));

        simplifyOnRelationsPattern(tree);

    }

    private void simplifyOnRelationsPattern(Tree tree) {
        //tuka davam tree
        //proveruva nekoja struktura na recenicata so TREGEX
        //proveruva dali ima nekoj pattern-match..
        //dodava novi recenici --> subject --patern-o--- object.

        POSMarker posMarker = new POSMarker();
        String sentence = AnalysisUtilities.getCleanedUpYield(tree);
        String posTags = posMarker.printPOSofSentence(tree);

        LOG.info("Testing against patterns...NNP VBD DT NNP and similiar.. ");

        try {
            FoxResponse response = FoxBinding.sendRequest(sentence);

            if (response.getRelations().size() > 0) {
                if (posTags.contains("NNP VBD DT NNP") || posTags.contains("NNPS VBD DT NNP") || posTags.contains("NNP VBD DT NNPS") || posTags.contains("NNPS VBD DT NNPS")
                        || posTags.contains("NNP VBZ RB NNP") || posTags.contains("NNPS VBZ RB NNP") || posTags.contains("NNP VBZ RB NNPS") || posTags.contains("NNPS VBZ RB NNPS")) {
                    LOG.info("The sentence from above contains the full pattern: NNP VBD DT NNP or NNP VBZ RB NNP");
                } else if (posTags.contains("NNP VBD DT") || posTags.contains("NNPS VBD DT") || posTags.contains("VBD DT NNPS") || posTags.contains("VBD DT NNP")
                        || posTags.contains("NNP VBZ RB") || posTags.contains("NNPS VBZ RB") || posTags.contains("VBZ RB NNPS") || posTags.contains("VBZ RB NNPS")) {
                    LOG.info("POTENTIAL for further simplification: The sentence from above contains partially pattern: NNP VBD DT ___ or NNP VBZ RB__ and opposite way.");
                }

                for (String pattern : relPOSpatterns ){
                    if (posTags.contains(pattern)){
                        LOG.info("Contains pattern: " + pattern);
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
