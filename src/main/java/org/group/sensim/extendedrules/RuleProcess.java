package org.group.sensim.extendedrules;

import edu.stanford.nlp.ling.LabeledWord;
import org.apache.jena.reasoner.rulesys.Rule;
import org.group.sensim.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the rules, which are additionally implemented.
 *
 * TODO Note: ExtractADJPs should be also in this class.
 */
public class RuleProcess {

    public RuleProcess(){
    }


    public static List<String> splitNotOnlyBut(Question q, String sentence){
        List<String> sentences = new ArrayList<String>();
        List<String> words = new ArrayList<String>();
        List<String> tags = new ArrayList<String>();
        String tmpSentence = "";

        if ( checkSplitableNotOnlyBut(sentence) ){
            int indexNotOnly=9999;
            int indexBut=0;


            for(LabeledWord lword : q.getIntermediateTree().labeledYield()){
                System.out.print("labeledWord: " + lword);
                System.out.println(" tag: " + lword.tag() + "  word: "+ lword.word());
                words.add(lword.word());
                tags.add(lword.tag().toString());
            }

            String beforeNotOnly ="";
            String betweenNotOnlyAndBuy = "";
            String afterBut = "";

            //get position of the 'not only' and 'but'
            for (int i = 0; i < words.size()-1; i++){

                //not only
                if (words.get(i).equals("not") && words.get(i+1).equals("only")){
                    indexNotOnly = i;
                    //snimi verb posle not only, u slucaj da nema posle "but" verb --> dodaj go verbo.
                    i++;
                }
                //but
                else if(words.get(i).equals("but") && indexNotOnly < i ){
                    indexBut = i;
                }
                //after 'but'
                else if(indexBut > indexNotOnly){
                    afterBut += words.get(i) + " ";
                }

                //between 'but' and 'not only'
                else if( indexNotOnly < i){
                    betweenNotOnlyAndBuy += words.get(i) + " ";
                }

                //before 'not only'
                else {
                    beforeNotOnly += words.get(i) + " ";
                }
            }

            //post processing
            if(afterBut.startsWith("also ")){
                afterBut = afterBut.substring(5);
            }


            System.out.println("index not only: " + indexNotOnly);
            System.out.println("index but: " + indexBut);

            System.out.println("before: " + beforeNotOnly);
            System.out.println("between: "+ betweenNotOnlyAndBuy);
            System.out.println("after: " + afterBut);

            sentences.add(beforeNotOnly+betweenNotOnlyAndBuy+".");
            sentences.add(beforeNotOnly+afterBut+".");
        }

        System.out.println("sentences list: " + sentences);
        return sentences;
    }

    /**
     * True: if a sentence contains the string "not only" followed by a "but". Otherwise false.
     * @param sentence
     * @return
     */
    private static boolean checkSplitableNotOnlyBut(String sentence) {
        int indexNotOnly = indexNotOnly = sentence.toLowerCase().indexOf("not only");
        int indexBut = indexBut = sentence.toLowerCase().indexOf(" but", indexNotOnly);

        if (indexBut > indexNotOnly) {
            return true;
        }

        return false;


    }
}














