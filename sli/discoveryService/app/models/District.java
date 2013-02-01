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

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

/**
 * District model used for realm selection.
 * Links a state/district to an identity provider.
 */
@Entity
public class District extends Model {

    @Required
    public String state;

    public String district;

    @Required
    @ManyToOne
    public IdProvider idp;

    public District(String state, String district, IdProvider idp) {
        this.state = state;
        this.district = district;
        this.idp = idp;
    }
    
    public String toString() {
        return district.isEmpty() ? state : state + " - " + district;
    }
}
