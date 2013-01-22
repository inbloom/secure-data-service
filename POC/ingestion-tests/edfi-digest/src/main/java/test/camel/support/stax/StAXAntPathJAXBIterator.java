package test.camel.support.stax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import org.apache.camel.RuntimeCamelException;

public class StAXAntPathJAXBIterator extends StAXAntPathIterator<List<?>> {
	private final String packageName = "org.ed_fi._0100";
	private final Unmarshaller unmarshaller;

	public StAXAntPathJAXBIterator(XMLEventReader reader, String antPath,
			int group) {
		super(reader, antPath, group);

		JAXBContext jaxb;
		try {
			jaxb = JAXBContext.newInstance(packageName);
			unmarshaller = jaxb.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException();
		}

		
	}

	protected List<?> grabContent() throws XMLStreamException {
		final List<Object> entities = new ArrayList<Object>();

		try {
			boolean retrieved = retrieveContent(new ContentRetriever() {

				@Override
				public void retrieve() throws XMLStreamException {
					Object answer;
					try {
						answer = unmarshaller.unmarshal(getReader());
						if (answer != null
								&& answer.getClass() == JAXBElement.class) {
							JAXBElement<?> jbe = (JAXBElement<?>) answer;
							answer = jbe.getValue();
						}
					} catch (JAXBException e) {
						throw new RuntimeCamelException(e);
					}

					entities.add(answer);
				}
			});

			if (!retrieved) {
				throw new IOException("No more elements");
			}

			return entities;
		} catch (IOException e) {
			return null;
		}
	}
}