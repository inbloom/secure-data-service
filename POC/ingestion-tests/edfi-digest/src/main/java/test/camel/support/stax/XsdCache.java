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

package test.camel.support.stax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;

/**
 * @author okrook
 *
 */
public final class XsdCache {
    private Map<String, XsdTypeHolder> typeCache;

    private XsdCache() {
        typeCache = new HashMap<String, XsdTypeHolder>();
    }

    public static XsdCache init(XSSchema schema) {
        XsdCache cache = new XsdCache();

        cache.build(schema);

        return cache;
    }

    private void build(XSSchema schema) {
        Iterator<XSElementDecl> elements = schema.iterateElementDecls();

        build(elements, typeCache);
    }

    private void build(Iterator<XSElementDecl> elements, Map<String, XsdTypeHolder> parent) {
        while (elements.hasNext()) {
            XSElementDecl element = elements.next();
            XsdTypeHolder th = build(element);

            parent.put(element.getName(), th);
        }
    }

    private XsdTypeHolder build(XSElementDecl element) {
        XSType type = element.getType();

        if (type.isComplexType()) {
            return build(type.asComplexType());
        } else if (type.isSimpleType()) {
            return build(type.asSimpleType());
        }

        return null;
    }

    private XsdTypeHolder build(XSComplexType type) {
        XsdTypeHolder th = new XsdTypeHolder(type);

        XSParticle particle = type.getContentType().asParticle();

        if (particle != null) {
            build(particle, th);
        }

        return th;
    }

    private void build(XSParticle particle, XsdTypeHolder parent) {
        XSTerm term = particle.getTerm();

        if (term.isModelGroup()) {
            for (XSParticle child : particle.getTerm().asModelGroup().getChildren()) {
                build(child, parent);
            }

        } else if (term.isElementDecl()) {
            XSElementDecl element = term.asElementDecl();
            XsdTypeHolder child = build(element);

            child.isCollection = particle.isRepeated();

            parent.children.put(element.getName(), child);
        }
    }

    private XsdTypeHolder build(XSSimpleType type) {
        return new XsdTypeHolder(type);
    }

    public XsdTypeHolder getTypeForElement(String elementName) {
        return typeCache.get(elementName);
    }

    /**
     * @author okrook
     *
     */
    class XsdTypeHolder {
        private XSType type;
        private Map<String, XsdTypeHolder> children = new HashMap<String, XsdCache.XsdTypeHolder>();
        private boolean isCollection;

        public XsdTypeHolder(XSType type) {
            this.type = type;
        }

        public XSType getType() {
            return type;
        }

        public boolean hasChildrenCollection() {
            if (type.isComplexType()) {
                XSParticle particle = type.asComplexType().getContentType().asParticle();

                if (particle != null) {
                    return particle.isRepeated();
                }
            }

            return false;
        }

        public boolean isCollection() {
            return isCollection;
        }

        public XsdTypeHolder getTypeForChildElement(String elementName) {
            return children.get(elementName);
        }
    }
}
