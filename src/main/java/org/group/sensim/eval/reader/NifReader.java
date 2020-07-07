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
import java.util.List;
import java.util.Set;


public class NifReader {

    private static final Log log = LogFactory.getLog(TTLReader.class);
    private final TurtleNIFParser turtleNIFParser = new TurtleNIFParser();


    public NifReader() {
        //todo add TurtleNifParser here and use it with instance of NifReader
    }

    public static void main(String[] args) {

        String testFile = "ReutersTest.ttl";
        readData(testFile);
        //TODO readDataWithListOfFiles should be possible
    }

    /*
    Reads ttl file and prints some nif values.
     */
    private static void readData(String file) {
        log.info(" -----> read input ttl-file...");
        final TurtleNIFParser turtleNIFParser = new TurtleNIFParser();
        File testFile = new File(new File("./src/main/resources/ReutersTest.ttl").getAbsolutePath());
        final List<Document> nifdocs = new ArrayList<>();

        List<String> lines = null;
        try {
            lines = Files.readAllLines(testFile.toPath());
        } catch (final IOException e) {
            log.error(e.getLocalizedMessage(), e);
            log.error(testFile.toPath());
        }

        log.info("Reuters test file with " + lines.size() + " lines.");
        nifdocs.addAll(turtleNIFParser.parseNIF(String.join(" ", lines)));

        log.info("<----- ...read input ttl-file");
        log.info(nifdocs.size() + " Documents has been created.");

        log.info("Iterating through the first document:");
        for (Document doc : nifdocs) {
            System.out.println("Doc. URI: " + doc.getDocumentURI());
            System.out.println("Text:\n" + doc.getText());
            System.out.println("Markings (" + doc.getMarkings().size() + "):\n" + doc.getMarkings());
            System.out.println("Translation:");

            log.info(doc.getMarkings().size() + " Entities has been found in the document with URI: " + doc.getDocumentURI());
            System.out.println("Info: (startIndex, length, pointingResource)");

            int entityCounter = 0;
            for (Marking marking : doc.getMarkings()) {


                if (!(marking instanceof NamedEntity)) {
                    continue;
                }

                final NamedEntity ne = (NamedEntity) marking;
                entityCounter++;
                System.out.println(entityCounter + ". " + ne);

//                final Set<String> uris = ne.getUris();
                final String word = doc.getText().substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
                System.out.println("NER (string): [" + word + "]");

            }

            break;
        }
    }
}
