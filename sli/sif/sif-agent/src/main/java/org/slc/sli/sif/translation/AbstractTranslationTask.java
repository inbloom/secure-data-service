/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.sif.translation;

import java.util.List;

import openadk.library.SIFDataObject;

import org.apache.commons.lang.ClassUtils;

import org.slc.sli.sif.domain.slientity.SliEntity;

/**
 * An implementation for translation of a SIF LEAInfo
 * to an SLI LEAEntity.
 *
 * @author slee
 *
 * @param <T>, the SIF type being translated
 * @param <E>, the SLI return type
 */
public abstract class AbstractTranslationTask<T extends SIFDataObject, E extends SliEntity> implements
        TranslationTask<E> {
    private Class<T> sifPrototype;

    /**
     * Defines two types, which will take part conversion.
     *
     * @param prototypeA
     *            type source
     * @param prototypeB
     *            type destination
     */
    public AbstractTranslationTask(Class<T> sifPrototype) {
        this.sifPrototype = sifPrototype;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> translate(SIFDataObject sifData, String zoneId) throws SifTranslationException {
        Class<?> wrappedSifClass = ClassUtils.primitiveToWrapper(sifData.getClass());

        if (!sifPrototype.equals(wrappedSifClass)) {
            throw new SifTranslationException("Unsupported SIF data type");
        }
        return doTranslate((T) sifData, zoneId);
    }

    public abstract List<E> doTranslate(T sifData, String zoneId);
}
