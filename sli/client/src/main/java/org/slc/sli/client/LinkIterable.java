package org.slc.sli.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterator over links
 * 
 * @author nbrown
 * 
 * @param <T>
 */
public class LinkIterable<T> implements Iterable<T> {
    public static final String LINK_HEADER = "Link";
    private static final Logger LOG = LoggerFactory.getLogger(LinkIterable.class);
    private static final Format DEFAULT_FORMAT = Format.JSON; // TODO do we really need to support
    private final Iterator<String> linkIter;
    private final Class<T> resourceClass;
    private final Client client;
    private final SliParser sliParser;
    private T last = null;
    private Iterator<T> currentItr = null;
    
    public LinkIterable(ClientResponse response, String linkType, Class<T> resourceClass, Client client,
            SliParser sliParser) {
        super();
        List<String> linkHeaders = response.getHeaders().get(LinkIterable.LINK_HEADER);
        List<String> links = getLinks(linkHeaders, linkType);
        this.linkIter = links.iterator();
        this.resourceClass = resourceClass;
        this.client = client;
        this.sliParser = sliParser;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new LinkIterator();
    }
    
    private class LinkIterator implements Iterator<T> {
        private T nextValue = null;
        
        @Override
        public boolean hasNext() {
            return peek() != null;
        }
        
        @Override
        public T next() {
            T next = peek();
            nextValue = null;
            return next;
        }
        
        T peek() {
            if (nextValue != null) {
                return nextValue;
            }
            if (currentItr != null && currentItr.hasNext()) {
                T next = currentItr.next();
                if (last == null || !last.equals(next)) {
                    nextValue = next;
                }
            } else {
                currentItr = null;
                while (nextValue == null && linkIter.hasNext()) {
                    String path = linkIter.next();
                    WebResource webResource = client.resource(path);
                    ClientResponse resp = webResource.accept(DEFAULT_FORMAT.getMediaType()).head();
                    LOG.debug("Response to get on next link is {}", resp);
                    if (resp.getStatus() == 204) {
                        currentItr = (new LinkIterable<T>(resp, resourceClass.getSimpleName(), resourceClass, client,
                                sliParser)).iterator();
                        nextValue = currentItr.next();
                    } else if (resp.getStatus() < 300) {
                        try {
                            nextValue = sliParser.parse(webResource.get(String.class), DEFAULT_FORMAT, resourceClass);
                        } catch (UniformInterfaceException e) {
                            LOG.error("UniformInterfaceException {} while getting the resource {}",
                                    new String[] {e.getMessage(), path});
                        }
                    } else {
                        // that link is gone, need to try again
                        LOG.debug("Couldn't find next link at {}, response was {}",
                                new String[] {path, Integer.toString(resp.getStatus())});
                    }
                }
            }
            last = nextValue;
            return nextValue;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    /**
     * Parse the link header
     * 
     * @param linkHeader
     *            the link header to parse
     * @return a two element array, with the first member the link, the second the rel type
     */
    private String[] parseLink(String linkHeader) {
        String[] splitLink = linkHeader.split(";");
        if (splitLink.length < 2) {
            return new String[] {"", ""};
        }
        String[] parsedLink = new String[2];
        String linkPart = splitLink[0].trim();
        parsedLink[0] = linkPart.length() == 0 ? "" : splitLink[0].substring(1, linkPart.length() - 1);
        String relPart = splitLink[1].trim();
        parsedLink[1] = relPart.startsWith("rel=") ? relPart.substring(4) : "";
        return parsedLink;
        
    }
    
    private List<String> getLinks(List<String> linkHeaders, String linkType) {
        if (linkHeaders == null) {
            return new ArrayList<String>(0);
        }
        List<String> links = new ArrayList<String>(linkHeaders.size());
        for (String linkHeader : linkHeaders) {
            String[] parsedLink = parseLink(linkHeader);
            if ("*".equals(linkType) || parsedLink[1].equalsIgnoreCase(linkType)) {
                links.add(parsedLink[0]);
            }
        }
        return links;
    }
    
}
