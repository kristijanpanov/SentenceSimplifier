package org.group.sensim.eval;

import org.aksw.fox.binding.FoxApi;
import org.aksw.fox.binding.FoxParameter;
import org.aksw.fox.binding.FoxResponse;
import org.aksw.fox.binding.IFoxApi;
import org.aksw.fox.data.Entity;
import org.aksw.fox.data.RelationSimple;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class FoxBinding {

    public final static Logger LOG = LogManager.getLogger(FoxBinding.class);

//    static FoxBinding fb = new FoxBinding();
//
//    public static void main(final String[] a) throws Exception {
//        String input = "Michael Jackson was born in the USA.";
//        FoxResponse response = fb.sendRequest(input);
//        printFormattedResponse(response);
//
//    }

    /**
     * Connects to the server and sends request input sentence to be processed.
     *
     * @param input - the sentence to extract the entities and relations from.
     * @return FoxResponse - fox response as Classes.
     */
    public static FoxResponse sendRequest(String input) throws MalformedURLException {

        LOG.info("Sending request to FOX: [" + input + "] ...");
        final IFoxApi fox = new FoxApi()//
                .setApiURL(new URL("https://fox.demos.dice-research.org/fox")) //probiere das mal.
                .setTask(FoxParameter.TASK.RE)
                .setOutputFormat(FoxParameter.OUTPUT.TURTLE)
                .setLang(FoxParameter.LANG.EN)
                .setInput(input)//
                // .setLightVersion(FoxParameter.FOXLIGHT.ENBalie)//
                .send();
        LOG.info("Response received.");

        //final String plainContent = fox.responseAsFile();
        //List<Document> docs = parseResponseContentNif(plainContent);
        //NifReader.printNifDocuments(docs);

        final FoxResponse response = fox.responseAsClasses();

        return response;
    }

    /**
     * Prints the entities and the relations from the response into console.
     *
     * @param response - the response to be printed
     */
    public static void printFormattedResponse(FoxResponse response) {
        List<Entity> entities = response.getEntities();
        List<RelationSimple> relations = response.getRelations();

        LOG.info("Iterating through FOX entities: ----> [" + entities.size() + "]");
        for (Entity e : entities) {
            LOG.info("Entity: [" + e.getText() + "] - start position: " + e.getIndices() + " -  URI: " + e.getUri());
        }

        LOG.info("Iterating through FOX relations: ----> [" + relations.size() + "]");
        for (RelationSimple rel : relations) {
            LOG.info(rel);
        }
    }

    /**
     * Creates a HashMap of Entity name with its' starting position in the document.
     *
     * @param foxResponse - the FoxResponse from which the entities will be extracted.
     * @return Map<String, Integer> - Key:=Entity, Value:=Starting position
     */
    public static Map<Integer, String> getEntities(FoxResponse foxResponse) {
        LOG.info(foxResponse.getEntities().size() + " Entities has been found in the FOX response.");
        Map<Integer, String> entityMap = new HashMap<Integer, String>();
        List<Entity> entities = foxResponse.getEntities();

        String entityName;
        Integer startPos;
        for (Entity e : entities) {
            entityName = e.getText();
            startPos = e.getIndices().iterator().next();
            entityMap.put(startPos, entityName);
//          endless loop:
//            while (e.getIndices().iterator().hasNext()) {
//                startPos = e.getIndices().iterator().next();
//                entityMap.put(entityName, startPos);
//            }
        }
        return entityMap;
    }

    /**
     * Returns all entity URLs found in the response.
     *
     * @param foxResponse -  the FoxResponse from which the entities will be extracted.
     * @return Set<String> - set of URLs of entities.
     */
    public static Set<String> getEntityURLs(FoxResponse foxResponse){
        LOG.info(foxResponse.getEntities().size() + " Entities has been found in the FOX response.");

        List<Entity> entities = foxResponse.getEntities();
        Set<String> uniqueURLs = new HashSet<String>();

        for (Entity e : entities) {
            uniqueURLs.add(e.getUri());
        }

        return uniqueURLs;
    }

    /**
     * Returns all entity words found in the response.
     *
     * @param foxResponse -  the FoxResponse from which the entities will be extracted.
     * @return Set<String> - set of entity words.
     */
    public static Set<String> getEntityWord(FoxResponse foxResponse) {

        List<Entity> entities = foxResponse.getEntities();
        LOG.info(entities.size() + " Entities has been found in the FOX response.");

        Set<String> uniqueWords = new HashSet<String>();

        for (Entity e : entities) {
            uniqueWords.add(e.getText());
        }

        return uniqueWords;
    }

    /**
     * Uses the Turtle-Nif Parser, parses the response and saves it into a list of Documents.
     *
     * @param plainContent - Response plainContent from fox in a NIF format.
     * @return List<Documents> the parsed nif documents in a list.
     */
    private List<Document> parseResponseContentNif(String plainContent) {
        TurtleNIFParser turtleNIFParser = new TurtleNIFParser();
        List<Document> docs = new ArrayList<Document>();

        LOG.info("Parsing response content...");
        docs.addAll(turtleNIFParser.parseNIF(plainContent));
        LOG.info("Parsing finished.: ");

        return docs;
    }

    /**
     * Writes the data (as file) returned from fox into a file.
     *
     * @param data - the data returned from fox.
     * @throws IOException
     */
    private static void writeResults(String data) throws IOException {
        File foxResults = new File(".//fox_results2.txt");

        // Create the file
        if (foxResults.createNewFile()) {
            System.out.println("File [" + foxResults.getName() + "] is created!");
        } else {
            System.out.println("File [" + foxResults.getName() + "] foxResultsalready exists.");
        }

        // Write Content
        FileWriter writer = new FileWriter(foxResults);
        writer.write(data);
        writer.close();
    }
}
