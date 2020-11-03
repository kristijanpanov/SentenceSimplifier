package org.group.sensim;

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
        ss.simplifyFactualComplexSentenceAditional("Geographers study not only the physical details of the environment but also its impact on human and wildlife ecologies, weather and climate patterns, economics, and culture.\n");


    }
}
