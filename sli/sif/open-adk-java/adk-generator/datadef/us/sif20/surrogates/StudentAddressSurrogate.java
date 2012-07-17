package openadk.library.impl.surrogates;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.ADK;
import openadk.library.ADKParsingException;
import openadk.library.Element;
import openadk.library.ElementDef;
import openadk.library.SIFElement;
import openadk.library.SIFFormatter;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.common.Address;
import openadk.library.common.CommonDTD;
import openadk.library.student.StudentAddress;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentPersonal;
import openadk.library.tools.xpath.SIFElementPointer;
import openadk.util.XMLWriter;

/**
 * A surrogate for reading the StudentAddress element and rendering the AddressList element for legacy agents
 * 
 * @author Stephen Miller
 * 
 */
public class StudentAddressSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

    public StudentAddressSurrogate(ElementDef def) {
        super(def);
    }

    // write it out in 1.5r1 format
    public void renderRaw(XMLWriter writer, SIFVersion version, Element o, SIFFormatter formatter) {

        if (!(o instanceof SIFElement)) {
            Logger.getLogger("openadk.library.StudentAddressSurrogate").warning("StudentAddressSurrogate got an unacceptable element of type " + o.getClass() + "/" + o.getElementDef().name() + "(" + o + ") in renderRaw");
            return;
        }

        SIFElement element = (SIFElement) o;

        for (SIFElement address : element.getChildList(CommonDTD.ADDRESS)) {
            writer.tab();
            // FIXME: Should this be the default? Probably not...
            writer.write("<StudentAddress PickupOrDropoff=\"NA\" DayOfWeek=\"NA\">");
            writer.write("\r\n");
            writer.indent(1);
            writer.tab();
            SIFWriter addressWriter = new SIFWriter(writer, true);
            addressWriter.suppressNamespace(true);
            addressWriter.write(version, address);
            writer.indent(-1);
            writer.tab();
            writer.write("</StudentAddress>");
            writer.write("\r\n");
        }
    }

    public boolean readRaw(XMLStreamReader reader, SIFVersion version, SIFElement parent, SIFFormatter formatter) throws ADKParsingException {
        if (!reader.getLocalName().equals("StudentAddress")) {
            return false;
        }
        try {
            StudentPersonal studentPersonal = (StudentPersonal) parent;
            StudentAddressList addressList = (StudentAddressList) studentPersonal.getChild(StudentDTD.STUDENTPERSONAL_ADDRESSLIST);
            if (addressList == null) {
                addressList = new StudentAddressList();
                studentPersonal.setAddressList(addressList);
            }

            StudentAddressPullParser parser = new StudentAddressPullParser();
            StudentAddress studentAddress = (StudentAddress) parser.parseOneElementFromStream(reader, version, ADK.DTD(), null, 0);
            Address address = studentAddress.getAddress();
            studentAddress.removeChild(address);
            addressList.add(address);
            if (reader.hasNext()) {
                reader.nextTag();
            }
        } catch (Exception e) {
            throw new ADKParsingException("Could not read StudentAddress: " + e.getMessage(), null, e);
        }

        return true;
    }

    @Override
    public String toString() {
        return "StudentAddressSurrogate{}";
    }

    /**
     * Creates the Child as the result of an XPath Expression and returns the NodePointer wrapping the child
     */
    public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context) {
        StudentPersonal studentPersonal = (StudentPersonal) parentPointer.getValue();
        StudentAddressList addressList = studentPersonal.getAddressList();
        if (addressList == null) {
            addressList = new StudentAddressList();
            studentPersonal.setAddressList(addressList);
        }
        return new SIFElementPointer(parentPointer, addressList, version);
    }

    /**
     * Creates a NodePointer that "wraps" the specified ADK 2.0 element, but appears to JXPath as if it uses the SIF 1.5
     * path.
     * 
     * XPathSurrogates always represent a surrogate around an ADK 2.0 Simple Field
     * 
     */
    public NodePointer createNodePointer(NodePointer parent, Element sourceElement, SIFVersion version) {
        return new SIFElementPointer(parent, (SIFElement) sourceElement, version);
    }

    public String getPath() {
        return "StudentAddress";
    }

}
