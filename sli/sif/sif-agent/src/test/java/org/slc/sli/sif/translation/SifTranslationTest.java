package org.slc.sli.sif.translation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for the configured SIF->SLI translation.
 * @author jtully
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/test-applicationContext.xml" })
public class SifTranslationTest {

    @Autowired
    private SifTranslationManager translationManager;

    @Test
    public void shouldTranslateSifInfoToSchool() {
         //Todo add integrated unit tests here
         System.out.println(translationManager);
    }
}
