/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
