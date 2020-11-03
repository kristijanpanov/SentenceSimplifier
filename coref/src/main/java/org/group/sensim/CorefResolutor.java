package org.group.sensim;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class CorefResolutor {
    private StanfordCoreNLP pipeline;

    public CorefResolutor() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
        pipeline = new StanfordCoreNLP(props);
    }

    public String getCoref(String sentences) {
        String corefChain = "";
        Annotation document = new Annotation(sentences);
        this.pipeline.annotate(document);
        System.out.println("---");
        System.out.println("coref chains");
        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
            System.out.println("\t" + cc);
            System.out.println("mentions MAP: " + cc.getMentionMap());
            System.out.println("mentionsInTextual Order: " + cc.getMentionsInTextualOrder());
            System.out.println("mentionsRepresentattive : " + cc.getRepresentativeMention().mentionSpan);
            corefChain += "\t" + cc;
        }
//        for (CoreMap sent : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//            System.out.println("---");
//            System.out.println("mentions");
//            for (Mention m : sent.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
//                System.out.println("\t" + m);
//            }
//        }

        return corefChain;
    }
}
