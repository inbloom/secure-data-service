package models;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class IdProvider extends Model {

    public String state;
    public String district;
    public String domain;
    public String redirect;

    public IdProvider(String state, String district, String domain, String redirect) {
        this.state = state;
        this.district = district;
        this.domain = domain;
        this.redirect = redirect;
    }

}