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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POSMarker {

    public final static Logger log = LogManager.getLogger(POSMarker.class);

    /*
    Pos tags, which may represent an entity.
    The list is created based on a tested data with marked entities.
     */
    private final List<String> relevantPosTags;

    /*
    Pos tags, that are not considered as entity.
     */
    private final List<String> excludedPosTags;

    public POSMarker() {
        //BasicConfigurator.configure();
        String propertiesFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "factual-statement-extractor.properties";
        GlobalProperties.loadProperties(propertiesFile);
        excludedPosTags = initExcludedTags();
        relevantPosTags = initRelevantTags();
    }


    private List<String> initExcludedTags() {
        List<String> excludedPosTags = new ArrayList<String>();
        excludedPosTags.add("DT");
        excludedPosTags.add("TO");
        excludedPosTags.add("IN");
        excludedPosTags.add("CC");
        excludedPosTags.add("VBZ");
        excludedPosTags.add(".");
        excludedPosTags.add("PRP");
        excludedPosTags.add("VB");

        return excludedPosTags;
    }

    /**
     * Initialize relevant POS-tags: tags pointing to an entity.
     * The List was created after finding out which tags-point to an entity.
     * The List may be extended.
     * @return
     */
    private List<String> initRelevantTags() {
        List<String> relevantPosTags = new ArrayList<String>();
        relevantPosTags.add("NNP");
        relevantPosTags.add("NNPS");
        return relevantPosTags;
    }

    /**
     * Extracts the POS-tags from a sentence and returns it as a Tree, containing all Tags.
     *
     * @param sentence - the sentence to extract POS-tags from.
     * @return - Tree, containing the POS-tags.
     */
    public Tree extractPOStags(String sentence) {
        ParseResult parseResult;
        parseResult = AnalysisUtilities.getInstance().parseSentence(sentence);
        return parseResult.parse;
    }

    public boolean checkSentenceRelevance(String sentence){
        return checkSentenceRelevance(extractPOStags(sentence));
    }

    public boolean checkSentenceRelevance(Tree posTree){
        Map<String, String> wordPos = mapWordPOS(posTree);
        String posTag;

        for (String word : wordPos.keySet()) {
            posTag = wordPos.get(word);
            if (relevantPosTags.contains(posTag)) {
                return true;
            }
        }

        return false;
    }



    /**
     * Prints the POS-tags from posTree, which point to an entity.
     * Used to find out these tags and create the list: relevantPosTags.
     *
     * @param posTree - the Tree containing all POS-tags from the sentence.
     * @param entities - Map containing the Entities with a name (extracted from ttl file.)
     * @param writer - PrintWriter, which defines in which file to write the results.
     * @return
     */
    public String extractPOSofEntities(Tree posTree, Map<Integer, String> entities, PrintWriter writer) {
        Map<String, String> wordPos = mapWordPOS(posTree);
        String posTag;
        String entityPos = "";
        for (String entity : entities.values()) {
            for (String word : wordPos.keySet()) {
                posTag = wordPos.get(word);
                if (!excludedPosTags.contains(posTag))
                    if (entity.contains(word)) {
                        entityPos = "The entity: [" + entity + "] contains (" + word + ") with the tag: " + wordPos.get(word);
                        writer.println(entityPos);
                    }
            }
        }
        return entityPos;
    }

    /**
     * Saves word and its pos-tag <word, posTag> from the tagged tree into a hashmap.
     *
     * @param posTree - the tree containing every pos tag from the sentence.
     * @return HashMap <word(String), posTag(String)>
     */
    private Map<String, String> mapWordPOS(Tree posTree) {
        //TODO change to list, because Map does not allow duplicates?
        //used only for creating initially list of pos-tags.
        Map<String, String> entityPos = new HashMap<String, String>();
        String tag = "";
        for (Tree word : posTree.getLeaves()) {
            //Tree parent = word.parent(posTree.firstChild());
            //last two tags (testing)
            //tag = parent.parent(posTree.firstChild()).label().toString();
            tag = word.parent(posTree.firstChild()).label().toString();
            entityPos.put(word.toString(), tag);
        }
        return entityPos;
    }
}
