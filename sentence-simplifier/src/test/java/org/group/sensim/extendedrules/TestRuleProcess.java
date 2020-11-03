package org.group.sensim.extendedrules;

import edu.stanford.nlp.trees.Tree;
import junit.framework.TestCase;
import org.group.sensim.AnalysisUtilities;
import org.group.sensim.GlobalProperties;
import org.group.sensim.Question;
import org.group.sensim.SentenceSimplifier;

import java.util.Collection;
import java.util.List;

public class TestRuleProcess extends TestCase {

    SentenceSimplifier simp;


    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    protected void setUp() {
        simp = new SentenceSimplifier();
        simp.setBreakNPs(true);
        simp.setExtractFromVerbComplements(true);
        GlobalProperties.setDebug(true);
    }

    /**
     * Tears down the test fixture.
     * (Called after every test case method.)
     */
    protected void tearDown() {
        simp = null;
    }

    /**
     * Test sentences which simplify 'not-only-but' structure.
     */
    public void testSplitNotOnlyBut() {
        String sentence;
        List<String> simplifiedSents;


        /* not-only, but-also */
        sentence = "His books are not only instructions but also aesthetic treatises based on the spiritual culture of Japan.";
        simplifiedSents = simp.simplifyFactualComplexSentenceAditional(sentence);
        assertTrue(simplifiedSents.contains("His books are instructions."));
        assertTrue(simplifiedSents.contains("His books are aesthetic treatises based on the spiritual culture of Japan."));

        sentence = "Geographers study not only the physical details of the environment but also its impact on human and wildlife ecologies, weather and climate patterns, economics, and culture.";
        simplifiedSents = simp.simplifyFactualComplexSentenceAditional(sentence);
        assertTrue(simplifiedSents.contains("Geographers study the physical details of the environment."));
        assertTrue(simplifiedSents.contains("Geographers study its impact on human and wildlife ecologies, weather and climate patterns, economics, and culture."));

        sentence = "The STM can be used not only in ultra high vacuum but also in air and various other liquid or gas ambients, and at temperatures ranging from near 0 Kelvin to a few hundred degrees CelsiusC.";
        simplifiedSents = simp.simplifyFactualComplexSentenceAditional(sentence);
        assertTrue(simplifiedSents.contains("The STM can be used in ultra high vacuum."));
        assertTrue(simplifiedSents.contains("The STM can be used in air and various other liquid or gas ambients, and at temperatures ranging from near 0 Kelvin to a few hundred degrees CelsiusC."));


        /* not-only, but */
//        sentence = "In botany, an herbarium is the building where the specimens are stored, or the scientific institute that not only stores but researches these specimens.";
//        simplifiedSents = simp.simplifyFactualComplexSentenceAditional(sentence);
//        assertTrue(simplifiedSents.contains(" first "));
//        assertTrue(simplifiedSents.contains(" second "));

        //This makes it not only easier to learn, but means it can be typed using a normal keyboard.


    }
}
