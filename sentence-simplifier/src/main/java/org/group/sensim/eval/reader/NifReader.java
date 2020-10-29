package org.group.sensim.eval.reader;


import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * Creates a HashMap of Entity name with its' starting position in the document.
     *
     * @param doc - the Document from which the entities will be extracted.
     * @return Map<String,Integer> - Key:=Entity, Value:=Starting position
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



    public List<Document> getParsedNifDocuments() {
        return nifDocs;
    }

}
