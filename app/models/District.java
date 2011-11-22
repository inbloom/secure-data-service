package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class District extends Model {
    public String state;
    public String district;

    @ManyToOne
    public IdProvider idp;

    public District(String state, String district, IdProvider idp) {
        this.state = state;
        this.district = district;
        this.idp = idp;
    }
}
