package org.slc.sli.web.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * In spring 3.0 AnnotationMethodHandlerAdapter with annotation-driven create two different AnnotationMethodHandlerAdapters.
 * Therefore, to override one, we have to use this adapter
 * @author agrebneva
 *
 */
@Component
public class CustomMessageConverterRegister {
    @Autowired
    private AnnotationMethodHandlerAdapter adapter;

    private HttpMessageConverter<?>[] messageConverters;

    public void setMessageConverters(HttpMessageConverter<?>[] messageConverters) {
        this.messageConverters = messageConverters;
    }

    @PostConstruct
    public void bindMessageConverters() {
        adapter.setMessageConverters(messageConverters);
    }
}
