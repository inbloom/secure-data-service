package openadk.library.impl.surrogates;

import java.math.BigDecimal;

import javax.xml.datatype.Duration;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import openadk.library.*;
import openadk.library.impl.ElementDefAlias;
import openadk.library.trans.BusRouteInfo;
import openadk.library.trans.DistanceUnit;
import openadk.util.XMLWriter;

/**
 * A surrogate for rendering the RouteDuration and RouteStatus elements, which
 * changed shape between SIF 1.5 and SIF 2.0
 * 
 * @author Andrew
 * 
 */
public class RouteElementSurrogate extends AbstractRenderSurrogate implements RenderSurrogate {

	public RouteElementSurrogate(ElementDef def) {
		super(def);
	}

	public void renderRaw(XMLWriter writer, SIFVersion version, Element o, SIFFormatter formatter) throws SIFException {

		String type = "Total";
		if (fElementDef.name().endsWith("Loaded")) {
			type = "Loaded";
		}

		SIFSimpleType typedValue = o.getSIFValue();

		String elementName = "RouteDuration";
		if (fElementDef != null && fElementDef instanceof ElementDefAlias && ((ElementDefAlias) fElementDef).internalName().startsWith("RouteDistance")) {
			elementName = "RouteDistance";
			SimpleField unit = ((SIFElement) o).getField("Unit");
			if (unit != null && unit.getTextValue().equalsIgnoreCase("km")) {
				// Convert the km value to miles, which is how it is always
				// represented in SIF 1.x
				if (typedValue != null) {
					SIFDecimal kilometers = (SIFDecimal) typedValue;
					BigDecimal rawValue = kilometers.getValue();
					if (rawValue != null) {
						rawValue = rawValue.multiply(new BigDecimal(0.621371192));
						typedValue = new SIFDecimal(rawValue);
					}
				}
			}
		}

		String value = "";
		if (typedValue != null) {
			value = typedValue.toString(formatter);
		}

		if (type != null && value != null) {
			writer.tab();
			writer.write("<");
			writer.write(elementName);
			writer.write(" Type=\"");
			writer.write(type);
			writer.write("\">");
			writer.write(value);
			writer.write("</");
			writer.write(elementName);
			writer.write(">");
			writer.write("\r\n");

		}

	}

	public boolean readRaw(XMLStreamReader reader, SIFVersion version, SIFElement parent, SIFFormatter formatter) throws ADKParsingException {

		String name = reader.getLocalName();

		if (!name.startsWith("RouteD")) {
			return false;
		}

		BusRouteInfo busRoute = (BusRouteInfo) parent;

		String type = reader.getAttributeValue(null, "Type");
		String value = consumeElementTextValue(reader, version);
			// TODO: Better type conversion
			if (type != null && value != null) {
				if (name.equals("RouteDuration")) {
					Duration duration = formatter.toDuration(value);
					if ("Total".equals(type)) {
						busRoute.setRouteDurationTotal(duration);
					} else if ("Loaded".equals(type)) {
						busRoute.setRouteDurationLoaded(duration);
					}
				} else if ( name.equals("RouteDistance")) {
					BigDecimal distance = new BigDecimal(value);
					if ( "Total".equals(type)) {
						busRoute.setRouteDistanceTotal(DistanceUnit.M, distance );
					} else if ( "Loaded".equals(type) ) {
						busRoute.setRouteDistanceLoaded(DistanceUnit.M, distance );
					}
				}
			}
		return true;
	}

	public NodePointer createChild(NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodePointer createNodePointer(NodePointer parent, Element element, SIFVersion version) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() {
		String type = "Total";
		if (fElementDef.name().endsWith("Loaded")) {
			type = "Loaded";
		}

		String elementName = "RouteDuration";
		if (fElementDef.name().startsWith("RouteDistance")) {
			elementName = "RouteDistance";
		}

		return elementName + "[@Type='" + type + "']";

	}

}
