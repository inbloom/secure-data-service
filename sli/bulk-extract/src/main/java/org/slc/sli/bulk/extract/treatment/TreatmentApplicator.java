package org.slc.sli.bulk.extract.treatment;

import java.util.List;

import org.slc.sli.domain.Entity;

public class TreatmentApplicator implements Treatment {
    List<Treatment> treatments;

    @Override
    public Entity apply(Entity entity) {
        Entity treated = entity;
        for (Treatment treatment : treatments) {
            treated = treatment.apply(treated);
        }
        return treated;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }
    
}
