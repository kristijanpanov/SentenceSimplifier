package possequences;

import edu.stanford.nlp.trees.Tree;
import org.group.sensim.possequences.POSMarker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class POSMarkerTest {

    POSMarker posMarker;

    @Before
    public void setUp(){
        posMarker = new POSMarker();
    }

    @Test
    public void testExtractPOStags(){
        Tree testTreePosTag = posMarker.extractPOStags("This is an example of a sentence.");
        assertFalse("The sentence could not be passed to a stanford-nlp-tree.", testTreePosTag.getLeaves().isEmpty());
    }

    @Test
    public void testSentenceRelevance(){
        assertFalse( posMarker.checkSentenceRelevance("The weather today was rainy.") );
        assertFalse( posMarker.checkSentenceRelevance("What kind of music does he listen is not relevant for the case."));
        assertTrue( posMarker.checkSentenceRelevance("London is the largest city in England."));
        assertTrue( posMarker.checkSentenceRelevance("Angela married to John in year 2012."));
    }

}
