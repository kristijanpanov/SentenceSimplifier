package org.group.sensim;

import org.group.sensim.possequences.POSMarker;

import java.util.List;

/**
 * Main App - connects sentence simplifier and sentence selector.
 */
public class App {

    public App(){
    }

    public static void main(String[] args){
        App app = new App();
        app.runSimplification();
    }

    public void runSimplification(){
        SentenceSimplifier ss = new SentenceSimplifier();
        ss.simplifyFactualComplexSentenceAditional("Vaughn Anthony, a recording artist and brother of Grammy winner John Legend, speak to kids and staff at the Boston Project Ministries.");
    }

    /**
     * Simplifies given document of string.
     * @param doc - the document for simplifying
     * @return - simplified document
     */
    public String simplifyDocument(String doc){
        SentenceSimplifier ss = new SentenceSimplifier();
        List<String> simplified = ss.simplifyFactualComplexSentenceAditional("Out of this, traders in France received 34,500 tonnes, in the U.K. 37,800, in West-Germany 20,000, in Belgium 18,500, in Spain 5,800 and in Denmark 1,750 tonnes .");
        String resultSentence = "";

        for(String s : simplified){
            resultSentence += s + " ";
        }

        if (resultSentence.length()>1){
            return resultSentence;
        }
        return doc;

    }

    /**
     * Simplifies given document of string, only when entity present.
     * @param doc - the document for simplifying
     * @return - simplified document
     */
    public String simplifyDocumentIfEntityPresent(String doc){
        POSMarker posMarker = new POSMarker();
        String simplified = "";
        if( posMarker.checkSentenceRelevance(doc)){
            simplified = simplifyDocument(doc);
        }

        return simplified;
    }

}