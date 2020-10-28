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
import org.group.sensim.Question;

import java.io.*;
import java.util.*;

public class POSMarker {

    public final static Logger log = LogManager.getLogger(POSMarker.class);

    /*
    Pos tags, which may represent an entity.
    The list is created based on a tested data with marked entities.
     */
    private List<String> relevantPosTags;

    /*
    Pos tags, that are not considered as entity.
     */
    private List<String> excludedPosTags;

    public POSMarker() {
        //BasicConfigurator.configure();
        String propertiesFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "factual-statement-extractor.properties";
        GlobalProperties.loadProperties(propertiesFile);
        excludedPosTags = initExcludedTags();
        relevantPosTags = initSavedRelevantTags();

        if (relevantPosTags.size() < 2 ){
             relevantPosTags = initDefaultRelevantTags();
        }
    }

    public POSMarker(List<String> relevantPosTagsFiles) {
        new POSMarker();
        relevantPosTags = new ArrayList<String>();
        relevantPosTags = initSavedRelevantTags(relevantPosTagsFiles);

        if (relevantPosTags.size() < 2 ){
            relevantPosTags = initDefaultRelevantTags();
        }
    }


    private List<String> initExcludedTags() {
        List<String> excludedPosTags = new ArrayList<String>();
//        excludedPosTags.add("DT");
//        excludedPosTags.add("TO");
//        excludedPosTags.add("IN");
//        excludedPosTags.add("CC");
//        excludedPosTags.add("VBZ");
//        excludedPosTags.add(".");
//        excludedPosTags.add("PRP");
//        excludedPosTags.add("VB");

        excludedPosTags.add("NNS");
        excludedPosTags.add("NN");
        excludedPosTags.add("TO NNS");
        excludedPosTags.add("TO NN");
        excludedPosTags.add("JJ");
        excludedPosTags.add(".");

        return excludedPosTags;
    }


    private List<String> initSavedRelevantTags( ) {

        List<String> resourceFiles = new ArrayList<>();

        //standard: Pointing to LOC, PERSON, ORG
        // here define which files should be loaded the pos-sequences from
        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_RSS-50.txt");
        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_ReutersTes.txt");
        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_oke-challenge2018-trainin.txt");

//        resourceFiles.add("./src/main/resources/possequences/marking_entityToPOS_dbpedia-spotlight-ni.txt");  //special case- different entity types.

        return initSavedRelevantTags(resourceFiles);
    }

    /**
     * Initialize relevant POS-tags: tags pointing to an entity, which were pre-saved with (POSEntityUpdator)
     * The List was created after finding out which tags-point to an entity.
     *
     * @return List of POS-sequences as string, which point to an entity.
     */
    public List<String> initSavedRelevantTags(List<String> resourceFiles) {
        List<String> relevantPosTags = new ArrayList<String>();

        BufferedReader reader;
        for ( String posSeqF : resourceFiles ) {
            try {
                log.info("Loading pre-saved POS-sequences from: " + posSeqF);
                reader = new BufferedReader(new FileReader(
                        posSeqF));
                String posSeq = reader.readLine();

                while (posSeq != null ) {
                    //  posSeqReader
                    posSeq = reader.readLine();
                    if (!relevantPosTags.contains(posSeq) && posSeq != null){
                        relevantPosTags.add(posSeq);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("POS-sequences from: " + posSeqF  + " have been loaded.");
        }

        for (String posS : relevantPosTags){
            log.info("Relevant POStags: " + posS);
        }

        return relevantPosTags;
    }


    /**
     * Initialize relevant POS-tags: tags pointing to an entity.
     * The List was created after finding out which tags-point to an entity.
     * The List may be extended.
     * @return
     */
    private List<String> initDefaultRelevantTags() {
        //TODO insert these in a document. The document should be updated after running the EntityPOSUpdator := POSApp
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

    /**
     * Extracts the POS-tags from a sentence and returns it as a Tree, containing all Tags.
     *
     * @param tree - Tree, containingthe sentence to extract POS-tags from, represented as tree..
     * @return - Tree, containing the POS-tags.
     */
    public Tree extractPOStags(Tree tree) {
         return tree;
    }

    public String printPOSofSentence(String sentence){
        Tree parsedSentence =  extractPOStags(sentence);
        log.info("Sentence: " + sentence );

        String tag = printPOSofSentence(parsedSentence);

        log.info("Tagged Sentence: " + tag);
        return tag;
    }

    public String printPOSofSentence(Tree treeSent){
        log.info("Sentence: " + AnalysisUtilities.getCleanedUpYield(treeSent) );
        String tag = "";
        for (Tree word : treeSent.getLeaves()) {
            tag += word.parent(treeSent.firstChild()).label().toString() + " ";
        }

        log.info("Tagged Sentence: " + tag);
        return tag;
    }


    public boolean checkSentenceRelevance(String sentence){
        return checkSentenceRelevance(extractPOStags(sentence));
    }

    public boolean checkSentenceRelevance(Tree posTree){
        Map<String, String> wordPos = mapWordPOS(posTree);
        String sentencePosTags = "";

        for (String word : wordPos.keySet()) {
            sentencePosTags += wordPos.get(word) + " ";
        }
        log.info("POS TAGS OF THE SENTENCE: " + sentencePosTags);
        for (String posSeq : relevantPosTags){

            if (sentencePosTags.contains(posSeq)) {
                if (excludedPosTags.contains(posSeq)){
                    continue;
                }
                log.info("POS-sequence match: " + posSeq);
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
            entityPos = "";
            for (String word : wordPos.keySet()) {
                posTag = wordPos.get(word);
//                if (!excludedPosTags.contains(posTag))
                    if (entity.contains(word)) {
//                        entityPos = "The entity: [" + entity + "] contains (" + word + ") with the tag: " + wordPos.get(word);
//                        writer.println(entityPos);

                        entityPos += wordPos.get(word) + " ";
                  }
            }
            if (entityPos.length() > 0) {
                writer.println(entityPos);
                log.info("Writing POS-sequence: [" + entityPos + "]");
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
