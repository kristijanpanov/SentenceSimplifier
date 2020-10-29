package org.group.sensim.possequences;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


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
    private MaxentTagger tagger;


    public POSMarker() {
        BasicConfigurator.configure();
        excludedPosTags = initExcludedTags();
        relevantPosTags = initSavedRelevantTags();
        tagger = new MaxentTagger("./sentence-selector/src/main/resources/english-left3words-distsim.tagger");

        if (relevantPosTags.size() < 2) {
            relevantPosTags = initDefaultRelevantTags();
        }
    }

    public POSMarker(List<String> relevantPosTagsFiles) {
        BasicConfigurator.configure();
        relevantPosTags = new ArrayList<String>();
        relevantPosTags = initSavedRelevantTags(relevantPosTagsFiles);
        excludedPosTags = initExcludedTags();
        tagger = new MaxentTagger("./sentence-selector/src/main/resources/english-left3words-distsim.tagger");

        if (relevantPosTags.size() < 2) {
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
        excludedPosTags.add("IN NN");
        excludedPosTags.add("DT NN");
        excludedPosTags.add("JJ");
        excludedPosTags.add(".");
        excludedPosTags.add("CC NN");
        excludedPosTags.add("NN NN");
        excludedPosTags.add("IN DT NN");


        return excludedPosTags;
    }


    private List<String> initSavedRelevantTags() {

        List<String> resourceFiles = new ArrayList<String>();

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
        for (String posSeqF : resourceFiles) {
            try {
                log.info("Loading pre-saved POS-sequences from: " + posSeqF);
                reader = new BufferedReader(new FileReader(
                        posSeqF));
                String posSeq = reader.readLine();

                while (posSeq != null) {
                    //  posSeqReader
                    posSeq = reader.readLine();
                    if (!relevantPosTags.contains(posSeq) && posSeq != null && posSeq.length() < 19) {
                        relevantPosTags.add(posSeq);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("POS-sequences from: " + posSeqF + " have been loaded.");
        }

        for (String posS : relevantPosTags) {
            log.info("Relevant POStags: " + posS);
        }

        return relevantPosTags;
    }


    /**
     * Initialize relevant POS-tags: tags pointing to an entity.
     * The List was created after finding out which tags-point to an entity.
     * The List may be extended.
     *
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
    public String extractPOStags(String sentence) {
        String tSentence = tagger.tagString(sentence);
        String posTags = "";
        log.info("Tagged Sentence: " + tSentence);

        for (String wordPos : Arrays.asList(tSentence.split(" ", -1))) {
            if (wordPos.length() > 1) {
                posTags += wordPos.substring(wordPos.lastIndexOf("_") + 1) + " ";
            }
        }
        log.info("POS tags: " + posTags.substring(0, posTags.length() - 1));
        return posTags;
    }

    /**
     * Extracts the POS-tags from a sentence and returns it as a Tree, containing all Tags.
     *
     * @param sentence - String, containing the sentence to extract POS-tags from, represented as tree..
     * @return - Tree, containing the POS-tags.
     */
    public List<WordTagPair> extractPOStagsAsList(String sentence) {

        List<WordTagPair> listWordTag = new ArrayList<WordTagPair>();
        String tSentence = tagger.tagString(sentence);

        List<String> converedToList = Arrays.asList(tSentence.split(" ", -1));

        for (String wordPos : converedToList) {
            if (wordPos.length() > 1) {
//                System.out.println("wordPOS :  " + wordPos);
//                System.out.println("word: " + wordPos.substring(0, wordPos.indexOf("_")));
//                System.out.println("tag: " + wordPos.substring(wordPos.lastIndexOf("_") + 1, wordPos.length()));
                WordTagPair wordTag = new WordTagPair(wordPos.substring(0, wordPos.indexOf("_")), wordPos.substring(wordPos.lastIndexOf("_") + 1));
                listWordTag.add(wordTag);
            }
        }

        return listWordTag;
    }


    public String printPOSofSentence(String sentence) {
        log.info("Sentence: " + sentence);
        String tagged = tagger.tagString(sentence);
        log.info("Tagged Sentence: " + tagged);
        return tagged;
    }


    public boolean checkSentenceRelevance(String document) {

        String docPosTags = extractPOStags(document);


        log.info("Checking relevance...");
        for (String posSeq : relevantPosTags) {

            if (docPosTags.contains(posSeq)) {
                if (excludedPosTags.contains(posSeq)) {
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
     * @param sentence - String, the sentence to be extracted pos-tags from.
     * @param entities - Map containing the Entities with a name (extracted from ttl file.)
     * @param writer - PrintWriter, which defines in which file to write the results.
     * @return
     */
    public String extractPOSofEntities(String sentence, Map<Integer, String> entities, PrintWriter writer) {
        List<WordTagPair> listWordPos = extractPOStagsAsList(sentence);

        String posTag;
        String entityPos = "";
        for (String entity : entities.values()) {
            entityPos = "";
            //log.info("entity: [" + entity + "]");

            for (WordTagPair wordTag : listWordPos) {
                //log.info(wordTag);
                posTag = wordTag.getPosTag();

                if (entity.contains(wordTag.getWord())) {
                    entityPos += posTag + " ";
                }
            }

            if (entityPos.length() > 0) {
                entityPos = entityPos.substring(0, entityPos.length()-1);
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
//    private Map<String, String> mapWordPOS(Tree posTree) {
//        //TODO change to list, because Map does not allow duplicates?
//        //used only for creating initially list of pos-tags.
//        Map<String, String> entityPos = new HashMap<String, String>();
//        String tag = "";
//        for (Tree word : posTree.getLeaves()) {
//            //Tree parent = word.parent(posTree.firstChild());
//            //last two tags (testing)
//            //tag = parent.parent(posTree.firstChild()).label().toString();
//            tag = word.parent(posTree.firstChild()).label().toString();
//            entityPos.put(word.toString(), tag);
//        }
//        return entityPos;
//    }
}
