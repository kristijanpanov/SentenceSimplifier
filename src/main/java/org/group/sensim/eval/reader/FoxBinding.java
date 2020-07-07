package org.group.sensim.eval.reader;

import org.aksw.fox.binding.*;
import org.aksw.fox.data.Entity;
import org.aksw.fox.data.RelationSimple;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.management.relation.Relation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FoxBinding {

    public final static Logger LOG = LogManager.getLogger(FoxBinding.class);
    static FoxBinding fb = new FoxBinding();
    private FoxResponse response;

    public static void main(final String[] a) throws Exception {
        String input = "Barack Obama was born in America.";
        fb.createFoxConnection2(input);
    }

    private void createFoxConnectionnnection(String input) throws MalformedURLException {
        final IFoxApi fox = new FoxApi();
        fox.setApiURL(new URL("https://fox.demos.dice-research.org/#!/demofox"));

        fox.setTask(FoxParameter.TASK.NER);
        fox.setOutputFormat(FoxParameter.OUTPUT.TURTLE);
        fox.setLang(FoxParameter.LANG.DE);
        fox.setInput("Die Universität Leipzig liegt in Sachsen.");
        // fox.setLightVersion(FoxParameter.FOXLIGHT.DEBalie);
        LOG.info(fox.send().responseAsClasses().toString());

    }


    /**
     * Connects to the server.
     */
    public void createFoxConnection2(String input) throws MalformedURLException {

        final IFoxApi fox = new FoxApi()//
                .setApiURL(new URL("https://fox.demos.dice-research.org/index.html#!/demo"))
                .setTask(FoxParameter.TASK.RE)//
                .setOutputFormat(FoxParameter.OUTPUT.TURTLE)//
                .setLang(FoxParameter.LANG.EN)//
                .setInput("Barack Obama was married to Michel Obama.")//
                // .setLightVersion(FoxParameter.FOXLIGHT.ENBalie)//
                .send();


//        LOG.info("sleep 10s");
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        final String plainContent = fox.responseAsFile();
        final FoxResponse response = fox.responseAsClasses();
        // Set<Entity> entities = response.getEntities();
        // Set<Relation> relations = response.getRelations();

        LOG.info(plainContent);
        LOG.info(response);
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
