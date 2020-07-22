package org.group.sensim.eval.reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.BasicConfigurator;

/**
 * Reads nif .ttl files from the dataset. Uses JENA!
 *
 * Needed to evaluate the Heilman and Smith's algorithm against entities extraction with FOX.
 */
public class TTLReader {
    private static final Log log = LogFactory.getLog(TTLReader.class);
    private static int sentenceNum;

    public TTLReader() {
        sentenceNum = 0;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        log.debug("Prepare to read .ttl file...");

        Model reader = RDFDataMgr.loadModel("eval/ReutersTest.ttl");
        StmtIterator iterP = reader.listStatements();

        while (iterP.hasNext()) {
            Triple triple = iterP.next().asTriple();
            String url = triple.getPredicate().toString();
            //System.out.println(url);

            extractIsString(triple, url);
        }

        // RDFDataMgr.write(System.out, model, RDFFormat.NT);
        log.debug("... finished.");
    }

    /**
     *
     */
    private static void extractIsString(Triple triple, String url) {

        if (url.endsWith("#isString")) {
            sentenceNum++;
            System.out.println(sentenceNum + ". " + triple.getObject());
            // ResultSet types = executeQuery(getTypeQuery(resourceURI));

        }
    }

//    private static ParameterizedSparqlString getTypeQuery(String resourceTarget) {
//        ParameterizedSparqlString qs = new ParameterizedSparqlString(
//                "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
//                        + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "select distinct ?type  { \n"
//                        + "<" + resourceTarget + "> rdf:type ?type . \n"
//                        + "?type rdfs:isDefinedBy <http://dbpedia.org/ontology/> }");
//        return qs;
//    }


}
