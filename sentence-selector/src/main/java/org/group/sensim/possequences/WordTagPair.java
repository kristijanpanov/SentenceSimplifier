package org.group.sensim.possequences;

/**
 * This class serves to create a structure for a Word with its POS-tag.
 */
public class WordTagPair {
    private String word;
    private String posTag;


    WordTagPair(String word, String posTag) {
        this.word = word;
        this.posTag = posTag;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }


    public String getPosTag() {
        return posTag;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "WordTagPair{" +
                "word='" + word + '\'' +
                ", posTag='" + posTag + '\'' +
                '}';
    }
}
