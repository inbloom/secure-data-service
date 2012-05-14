package org.slc.sli.web.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import freemarker.cache.TemplateLoader;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * Wrapper for freemarker template loader that ensured html escaping for each template
 * @author agrebneva
 *
 */
public class HtmlEscapingFreeMarkerTemplateLoader implements TemplateLoader {
    public static final String ESCAPE_START = "<#escape x as x?html>";
    public static final String ESCAPE_END = "</#escape>";

    private final TemplateLoader delegateDelegate;

    public HtmlEscapingFreeMarkerTemplateLoader(TemplateLoader delegate) {
        this.delegateDelegate = delegate;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return delegateDelegate.findTemplateSource(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return delegateDelegate.getLastModified(templateSource);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Reader reader = null;
        try {
            reader = delegateDelegate.getReader(templateSource, encoding);
            return new StringReader(ESCAPE_START + IOUtils.toString(reader) + ESCAPE_END);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        delegateDelegate.closeTemplateSource(templateSource);
    }

    /**
     * Configure for all the template loaders to use escaping wrapper
     * @author agrebneva
     *
     */
    public static class HtmlEscapingFreeMarkerConfigurer extends FreeMarkerConfigurer {
        @Override
        protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {
            return new HtmlEscapingFreeMarkerTemplateLoader(super.getAggregateTemplateLoader(templateLoaders));
        }
    }
}