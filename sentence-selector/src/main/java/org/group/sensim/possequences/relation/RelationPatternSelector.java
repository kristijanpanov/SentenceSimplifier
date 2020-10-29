//package org.group.sensim.possequences.relation;
//
//import org.group.sensim.SentenceSimplifier;
//import org.group.sensim.possequences.POSMarker;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.Scanner;
//
//public class RelationPatternSelector {
//
//    public static void main(String[] args){
//
//        RelationPatternSelector rps = new RelationPatternSelector();
//        POSMarker posMarker = new POSMarker();
//        SentenceSimplifier ss = SentenceSimplifier.getInstance();
//        String simplified;
//        PrintWriter writer = null;
//
//        try {
//            //File myObj = new File("./src/main/resources/org.group.sensim.eval/text_containing_relation.txt");
//            File myObj = new File("./src/main/resources/datasets/PWKP_108016_SimpleComplexSentencesPair");
//            writer = new PrintWriter("./src/main/resources/datasets/PWKP_108016_SimpleComplexSentencesPair_EXPERIMENT", "UTF-8");
//
//            Scanner myReader = new Scanner(myObj);
//            int counter = 0;
//            while (myReader.hasNextLine()) {
//                counter++;
//                if (counter < 100) myReader.nextLine();
//
//                String doc = myReader.nextLine();
//                if(doc.length() == 0) continue;
//
//                //doc = doc.substring(9, doc.length()); // substr TEXT:_1 :
//                System.out.println(doc);
//                simplified = ss.simplifyFactualComplexSentence(doc);
//
//                writer.println(simplified);
//                writer.println( posMarker.printPOSofSentence( simplified ) );
//            }
//
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } finally {
//            writer.close();
//        }
//
//
//
//
//
//
//
//    }
//
//    public RelationPatternSelector(){
//
//    }
//
//
//}
