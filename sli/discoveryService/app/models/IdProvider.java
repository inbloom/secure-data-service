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


package models;

import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

/**
 * Identity Provider model used for realm selection.
 * Contains fields to redirect to the provider.
 */
@Entity
public class IdProvider extends Model {

    @Required
    public String name;
    @Required
    public String domain;
    @Required
    public String redirect;

    public IdProvider(String name, String domain, String redirect) {
        this.name = name;
        this.domain = domain;
        this.redirect = redirect;
    }
    
    public String toString() {
        return name + " - " + domain;
    }

}
