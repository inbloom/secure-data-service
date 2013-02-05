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


package org.slc.sli.validation.schema;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 *
 * SLI Token Schema which validates string token (enumeration) entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class TokenSchema extends NeutralSchema {

    // Constants
    public static final String TOKENS = "tokens";

    // Constructors
    public TokenSchema() {
        this(NeutralSchemaType.TOKEN.getName());
    }

    public TokenSchema(String xsdType) {
        super(xsdType);
    }

    // Methods
    @Override
    public NeutralSchemaType getSchemaType() {
        return NeutralSchemaType.TOKEN;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public Object convert(Object value) {
        return value;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    @Override
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
        return addError(this.matchesToken(entity), fieldName, entity, getQuotedTokens(), ErrorType.ENUMERATION_MISMATCH,
                errors);
    }

    protected boolean matchesToken(Object entity) {
        String[] tokens = this.getTokens();
        if (tokens != null && tokens.length > 0) {
            // values were sorted before put in properties map (XsdToNeutralSchemaRepository)
            return (Arrays.binarySearch(this.getTokens(), entity) >= 0);
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "PMD.ReturnEmptyArrayRatherThanNull"})
    protected String[] getTokens() {
        List<String> tokens = (List<String>) this.getProperties().get(TOKENS);
        if (tokens != null) {
            return tokens.toArray(new String[0]);
        }
        return null;
    }

    protected String[] getQuotedTokens() {
        // Wrap token in quotes so that the error message is more clear
        String[] quotedTokens = this.getTokens();
        if (quotedTokens != null) {
            for (int i = 0; i < quotedTokens.length; i++) {
                quotedTokens[i] = "'" + quotedTokens[i] + "'";
            }
        }
        return quotedTokens;
    }
}
