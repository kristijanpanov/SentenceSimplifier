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
        ss.simplifyFactualComplexSentenceAditional("Here is my first sentence. In the second study, whose principal author is Dr. Steven Rosenberg of the National Cancer Institute, researchers administered IL-2 to 157 cancer patients and found a 33 pct remission rate in cancers of the kidney, a 27 pct rate in melanomas and a 15 pct rate in cancers of the colon and rectum. And this is my last sentence.");
    }
}