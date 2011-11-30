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
