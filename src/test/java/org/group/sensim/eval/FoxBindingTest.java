package org.group.sensim.eval;

import org.aksw.fox.binding.FoxResponse;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

/**
 * Tests the functionality of crucial methods in FoxBinding.
 */
public class FoxBindingTest {


    /**
     * For this test an internet connection is needed.
     */
    @Test
    public void testSendRequest(){
        FoxResponse response = null;
        try {
             response = FoxBinding.sendRequest("Putin was born in Russia.");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail("Connection to FOX has failed.");
        }

        assertTrue(response != null);
        assertFalse(response.getEntities().isEmpty());
    }
}
