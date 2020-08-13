package org.group.sensim.possequences;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Tree;
import org.aksw.fox.data.Entity;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.AnalysisUtilities;
import org.group.sensim.GlobalProperties;
import org.group.sensim.ParseResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POSMarker {

    public final static Logger log = LogManager.getLogger(POSMarker.class);

    public POSMarker() {
        //BasicConfigurator.configure();
        String propertiesFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "factual-statement-extractor.properties";
        GlobalProperties.loadProperties(propertiesFile);
    }

    public Tree extractPOStags(String sentence) {
        ParseResult parseResult;
        parseResult = AnalysisUtilities.getInstance().parseSentence(sentence);

        return parseResult.parse;
    }


    public void printPOSofEntities(Tree posTree, List<Entity> entities) {
        Map<String, String> wordPos = mapWordPOS(posTree);

        for (Entity entity : entities) {
            for (String word : wordPos.keySet()) {
                if (entity.getText().contains(word)) {
                    System.out.println("The entity: + [" + entity.getText() + "] contains (" + word + ") with the tag: " + wordPos.get(word) );
                }
            }
        }
    }

    /**
     * Puts (word, posTag) from the tagged tree into a hashmap.
     *
     * @param posTree - the tree containing every pos tag from the sentence.
     * @return HashMap <word(String), posTag(String)>
     */
    private Map<String, String> mapWordPOS(Tree posTree) {
        Map<String, String> entityPos = new HashMap<String, String>();
        String tag = "";
        for (Tree word : posTree.getLeaves()) {
            Tree parent = word.parent(posTree.firstChild());
            tag = parent.parent(posTree.firstChild()).label().toString();
            tag += " " + parent.label().toString();

            entityPos.put(word.toString(), tag);
        }

        return entityPos;
    }


}
