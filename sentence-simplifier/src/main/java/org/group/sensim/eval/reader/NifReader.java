package org.group.sensim.eval.reader;


import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.RelationImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.group.sensim.eval.Triple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class NifReader {

    private static final Log log = LogFactory.getLog(NifReader.class);
    private TurtleNIFParser turtleNIFParser;
    private final List<Document> nifDocs;

    public NifReader() {
        turtleNIFParser = new TurtleNIFParser();
        nifDocs = new ArrayList<>();
    }

    public static void main(String[] args) {
        NifReader nf = new NifReader();
        String testFile = "./src/main/resources/org.group.sensim.eval/ReutersTest.ttl";
        nf.readData(testFile);
        nf.printDocuments();
        //TODO readDataWithListOfFiles should be possible
    }

    /**
     * Reads nif/ttl files and saves the Documents in a List.
     *
     * @param files - Array of paths of files to be read, starting from project root-folder.
     */
    public void readData(String[] files) {
        for (String file : files) {
            readData(file);
        }
    }

    /**
     * Reads nif/ttl file and saves the Documents in a List.
     *
     * @param file - The path of a file to be read, starting from project root-folder.
     * @return List of parsed Documents from the file.
     */
    public List<Document> readData(String file) {
        log.info(" -----> read input ttl-file...");
        turtleNIFParser = new TurtleNIFParser();
        File testFile = new File(new File(file).getAbsolutePath());

        List<String> lines = null;
        try {
            lines = Files.readAllLines(testFile.toPath());
        } catch (final IOException e) {
            log.error(e.getLocalizedMessage(), e);
            log.error(testFile.toPath());
        }

        assert lines != null;
        log.info(file + " has [" + lines.size() + "] lines.");
        nifDocs.addAll(turtleNIFParser.parseNIF(String.join(" ", lines)));
        log.info("<----- ...read input ttl-file");
        log.info(nifDocs.size() + " Documents have been parsed.");

        return nifDocs;
    }

    /**
     * Prints all saved documents to this class to the console with
     * additional information like: size, entities and the text.
     */
    private void printDocuments() {
        printNifDocuments(nifDocs);
    }

    /**
     * Prints all documents from the Document List to the console and
     * adds additional information like: size, entities and the text.
     *
     * @param docs - List<Documents> the parsed nif documents to be printed.
     */
    public static void printNifDocuments(List<Document> docs) {
        if (docs.size() == 0) {
            log.info("There are no documents to be printed.");
            return;
        }

        log.info(docs.size() + " document(s) found.");
        log.info("----------> iterating through documents:");
        for (Document doc : docs) {
            printNifDocument(doc);
        }

        log.info("<---------- iterating through response documents.");
    }

    /**
     * Prints single document to the console and
     * adds additional information like: size, entities and the text.
     *
     * @param doc - Documents the parsed nif documents to be printed.
     */
    public static void printNifDocument(Document doc) {
        log.info("Text:\n" + doc.getText());
        log.info(doc.getMarkings().size() + " Entities have been found in document with URI: " + doc.getDocumentURI());
        //log.info("Info: (startIndex, length, pointingResource)");

        int entityCounter = 0;
        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof NamedEntity)) {
                continue;
            }
            final NamedEntity ne = (NamedEntity) marking;
//              final Set<String> uris = ne.getUris();
            final String word = doc.getText().substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
            entityCounter++;

            log.info(String.format("%d . %-73s| NER: [%s]}", entityCounter, ne, word));
        }
    }

    /**
     * Prints single document to the console and
     *
     * @param doc - the parsed nif document to be printed.
     */
    public static void printNifDocumentRelations(Document doc) {
        log.info("Text:\n" + doc.getText());
        log.info("Searching for predicates/relations in document with URI: " + doc.getDocumentURI());

        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof RelationImpl)) {
                continue;
            }
            final RelationImpl rel = (RelationImpl) marking;
            log.info("Triple [nif-subj: " + rel.getSubject().toString() + ", nif-pred: " + rel.getPredicate().toString() + ", nif-obj: " +  rel.getObject().toString() + "]");
         }
    }

    /**
     * Extracts the relations from a document and adds them  into a List of triples <s,p,o>.
     *
     * @param doc - the document to extract relations from.
     * @return List<Triple> list of extracted triples. Empty list, if no relations are present.
     */
    public static List<Triple> extractTriples(Document doc) {
        List<Triple> nifTriples = new ArrayList<>();
        String subj = "";
        String pred = "";
        String obj = "";

        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof RelationImpl)) {
                continue;
            }

            final RelationImpl rel = (RelationImpl) marking;

            subj = rel.getSubject().toString().substring(rel.getSubject().toString().indexOf("uri=[")+5, rel.getSubject().toString().length() - 2);
            pred = rel.getPredicate().toString().substring(rel.getPredicate().toString().indexOf("uri=[")+5, rel.getPredicate().toString().length() - 2);
            obj = rel.getObject().toString().substring(rel.getObject().toString().indexOf("uri=[")+5, rel.getObject().toString().length() - 2);
            log.info("extracting Triple: [nif-subj: " + subj + ", nif-pred: " + pred + ", nif-obj: " + obj + "]");

            nifTriples.add(new Triple(subj, pred, obj));
        }

        return nifTriples;
    }



    /**
     * Creates a HashMap of Entity name with its' starting position in the document.
     *
     * @param doc - the Document from which the entities will be extracted.
     * @return Map<String,Integer> - Key:=Starting position, Value:=Entity
     */
    public static Map<Integer, String> getEntities(Document doc){
        Map<Integer, String> entityMap = new HashMap<Integer, String>();
        log.info(doc.getMarkings().size() + " Entities has been found in document with URI: " + doc.getDocumentURI());

        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof NamedEntity)) {
                continue;
            }
            final NamedEntity ne = (NamedEntity) marking;
            final Integer startingPosition = Integer.valueOf( ne.getStartPosition() );
            final String word = doc.getText().substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());

            entityMap.put(startingPosition, word);
        }

        return entityMap;
    }

    /**
     * Returns all entity URLs found in the document.
     *
     * @param doc - the Document from which the entities will be extracted.
     * @return Set<String> - set of URLs of entities.
     */
    public static Set<String> getEntityURLs(Document doc){
        log.info(doc.getMarkings().size() + " Entities has been found in document with URI: " + doc.getDocumentURI());
        Set<String> uniqueURLs = new HashSet<String>();
        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof NamedEntity)) {
                continue;
            }
            final NamedEntity ne = (NamedEntity) marking;
            uniqueURLs.addAll(ne.getUris());
        }
        return uniqueURLs;
    }

    /**
     * Returns all entity words found in the document.
     *
     * @param doc - the Document from which the entities will be extracted.
     * @return Set<String> - set of entity words.
     */
    public static Set<String> getEntityWords(Document doc){
        log.info(doc.getMarkings().size() + " Entities has been found in document with URI: " + doc.getDocumentURI());
        Set<String> uniqueWords = new HashSet<String>();
        for (Marking marking : doc.getMarkings()) {
            if (!(marking instanceof NamedEntity)) {
                continue;
            }
            final NamedEntity ne = (NamedEntity) marking;
            final String word = doc.getText().substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
            uniqueWords.add(word);
        }
        log.info("Unique entities in the document: " + uniqueWords.toString());
        return uniqueWords;
    }



    public List<Document> getParsedNifDocuments() {
        return nifDocs;
    }

}
