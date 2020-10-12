package org.group.sensim.possequences.relation;

import edu.stanford.nlp.trees.Tree;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.group.sensim.possequences.POSMarker;
import org.group.sensim.possequences.SentenceSelector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serves to update the POS-tags which correspond to an verb-phrase ( := relation between entities ).
 *
 * Initially using the Patty-relation-paraphrases list to extract the POS-Sequences of its relations.
 *
 */
public class RelationPOSExtractor {
    public final static Logger LOG = LogManager.getLogger(SentenceSelector.class);

    protected Map<String, Set<String>> relations = null;
    protected Map<String, Set<String>> paraphrasesIndex = null;
    protected Map<String, String> ptbToUniPos = null;


    public static void main(String[] args){
        final String pattyParaphrases = "./src/main/resources/patty/dbpedia-relation-paraphrases-patty.txt";
        final String posTagMap = "./src/main/resources/patty/en-ptb.map.txt";

        LOG.debug("Reading relations from patty file...");
        RelationPOSExtractor rExtractor = new RelationPOSExtractor(pattyParaphrases, posTagMap);
        LOG.debug("...done");

        rExtractor.writePOSPatternToFile(rExtractor, "./src/main/resources/patty/patty_relation_to_POS.txt");
    }

    /**
     * Writes unique pos-patterns to a file which point to a relation.
     * @param rExtractor
     * @param fileName
     */
    private void writePOSPatternToFile(RelationPOSExtractor rExtractor, String fileName) {
        LOG.debug("Extracting POS-patterns of patty-relations relations...");
        List<String> uniquePatterns = extractUniqueRelationPOSpattern(rExtractor);
        LOG.debug("Extracting POS-patterns of patty-relations finished.");

        LOG.debug("Writing relation POS-patterns to file...");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");

            for (String pattern : uniquePatterns) {
                writer.println(pattern);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        LOG.debug("Writing relation POS-patterns to file finished.");
    }

    private List<String> extractUniqueRelationPOSpattern(RelationPOSExtractor rExtractor) {
        List<String> uniquePatterns = new ArrayList<String>();

        //for now do not consider all patty relations (izbegnuvam kompliciranio proces translating pos-tags)
        Set<String> relationsNoTags = rExtractor.removeTags();
        //Set<String> relationsNoTags = rExtractor.selectEntriesWithTags();


        Tree verbPhrase;
        StringBuilder pattern;

        int counter = 0;

        for (String relNoTag : relationsNoTags) {
            counter++;
            //TODO unlimited in the future.
            if (counter > 1500 ){
                continue;
            }

            pattern = new StringBuilder();

            POSMarker posMarker = new POSMarker();
            verbPhrase = posMarker.extractPOStags(relNoTag);
            System.out.println(relNoTag);
            for (Tree posTag : verbPhrase.getLeaves()) {
                pattern.append(posTag.parent(verbPhrase.firstChild()).label().toString() + " ");
                System.out.print(posTag.parent(verbPhrase.firstChild()).label().toString() + " ");
            }

            if (!uniquePatterns.contains(pattern.toString())) {
                uniquePatterns.add(pattern.toString());
            }

            System.out.println();
            System.out.println();
        }


        counter = 0;
        for( String up : uniquePatterns ){
            System.out.print(++counter + ". " );
            System.out.println(up);
        }

        return uniquePatterns;
    }

    private Set<String> selectEntriesWithTags() {
        Set<String> relationsNoTags = new HashSet();

        for (String relName : relations.keySet() ){
            for ( String rel : relations.get(relName)){

                int countTags = StringUtils.countMatches(rel, "[[");
                System.out.println("Number of matches: " + countTags);
                int indexOfTag = 0;

                for (int i = 0; i < countTags; i++){
                    String tag = "";
                    System.out.println("relation to be processed: " + rel);
//                    System.out.println("position of [[ :" + rel.indexOf("[[", indexOfTag));
//                    System.out.println("position of ]] :" +  rel.indexOf("]]", indexOfTag));
                    tag = rel.substring(rel.indexOf("[[", indexOfTag)+2, rel.indexOf("]]", indexOfTag));
                    System.out.println("the part [[ to ]] : " + tag);
                    System.out.println("tag translated to: " + paraphrasesIndex.get(tag));
                    indexOfTag = rel.indexOf("]]")+2;

                    //TODO some patty tags map to multiple stanford-tags.
                    //In this case create all combinations.
                    //left for the future.

                    relationsNoTags.add(rel);
                }

            }
        }

        return relationsNoTags;

    }


    private Set<String> removeTags() {
        Set<String> relationsNoTags = new HashSet();

        for (String relName : relations.keySet() ){
            for ( String rel : relations.get(relName)){
                if (rel.contains("[")){
                    continue;
                    //TODO for now deleting entries(relations) with tags.
                    //TODO In the future I replace the patty-pos tag with the stanford-pos-tag. Withelp of the map-file.
                }
                relationsNoTags.add(rel);
            }
        }

        return relationsNoTags;
    }

    private void printExtractedPattyRel() {
        for( String s : relations.keySet() ) {
            System.out.print(s +  ": ");
            System.out.println(relations.get(s).toString());
            System.out.println();
        }
//        //testing
//        for( String s2 : ptbToUniPos.keySet() ) {
//            System.out.print(s2 +  ": ");
//            System.out.println(ptbToUniPos.get(s2).toString());
//            System.out.println();
//
//        }
    }


    public RelationPOSExtractor(final String paraphrasesPattyFile, final String posTagMapFile){
        relations = readPattyRelations(paraphrasesPattyFile);
        ptbToUniPos = readPosTagMapFile(posTagMapFile);
    }


    private Map<String, Set<String>> readPattyRelations(String paraphrasesPattyFile) {
        if (relations == null) {
            relations = new ConcurrentHashMap<>();
            paraphrasesIndex = new ConcurrentHashMap<>(); //todo change name to relationIndex...
            try (final Stream<String> stream = Files.lines(Paths.get(paraphrasesPattyFile))) {

                // all lines in the file
                final List<String> list = stream//
                        .skip(1)//
                        .collect(Collectors.toList());

                // add pattern to data
                list.stream().map(line -> line.split("\t"))//
                        .forEach(split -> {
                            String pattern = split[1];
                            if (pattern.endsWith(";")) {
                                pattern = pattern.substring(0, pattern.length() - 1);
                            }

                            relations.computeIfAbsent(split[0], k -> new HashSet<>());
                            paraphrasesIndex.computeIfAbsent(pattern, k -> new HashSet<>());

                            relations.get(split[0]).add(pattern);
                            paraphrasesIndex.get(pattern).add(split[0]);
                        });

            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        return relations;

    }

    private Map<String, String> readPosTagMapFile(final String posTagMapFile) {
        final Map<String, String> ptbToUniPos = new HashMap<>();
        try (final Stream<String> stream = Files.lines(Paths.get(posTagMapFile))) {
            stream//
                    .collect(Collectors.toList())//
                    .stream().map(line -> line.split("\t"))//
                    .forEach(split -> {
                        if (split.length > 1) {
                            ptbToUniPos.put(split[0], split[1]);
                        }
                    });
        } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return ptbToUniPos;
    }

}