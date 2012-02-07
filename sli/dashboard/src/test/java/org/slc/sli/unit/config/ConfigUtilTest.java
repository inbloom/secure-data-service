package org.slc.sli.unit.config;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.Field;

/**
 * Unit tests for the ConfigUtil helper class.
 *
 */
public class ConfigUtilTest {

    @Before
    public void setup() {

    }

    @Ignore
    @Test
    public void testFromXMLString1() {

        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                         + "<viewConfigSet>\n"
                         + "    <viewConfig name=\"listOfStudents\">\n"
                         + "        <displaySet displayName=\"\">\n"
                         + "            <field value=\"stud.studentInfo.name\" format=\"firstLast\" displayName=\"Student\"/>\n"
                         + "        </displaySet>\n"
                         + "    </viewConfig>\n"
                         + "</viewConfigSet>\n";

        ViewConfigSet configs = null;
        try {
            configs = ConfigUtil.fromXMLString(xmlString);
        } catch (Exception e) {
            System.out.println(e);
        }
        ViewConfig config = configs.getViewConfig().get(0);
        assertEquals(1, config.getDisplaySet().size());
        assertEquals(1, config.getDisplaySet().get(0).getField().size());
    }

    //@Test
    public void testToXMLString1() {

        ViewConfigSet configs = new ViewConfigSet();
        ViewConfig view = new ViewConfig();
        view.setName("listOfStudents");
        configs.getViewConfig().add(view);

        DisplaySet displaySet = new DisplaySet();
        displaySet.setDisplayName("");
        view.getDisplaySet().add(displaySet);

        Field field = new Field();
        field.setValue("stud.studentInfo.name");
        field.setFormat("firstLast");
        field.setDisplayName("Student");
        displaySet.getField().add(field);

        String xmlString = null;
        try {
            xmlString = ConfigUtil.toXMLString(configs);
        } catch (Exception e) {
            System.out.println(e);
        }
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                   + "<viewConfigSet>\n"
                   + "    <viewConfig name=\"listOfStudents\">\n"
                   + "        <displaySet displayName=\"\">\n"
                   + "            <field value=\"stud.studentInfo.name\" format=\"firstLast\" displayName=\"Student\"/>\n"
                   + "        </displaySet>\n"
                   + "    </viewConfig>\n"
                   + "</viewConfigSet>\n",
                     xmlString);

    }
}
