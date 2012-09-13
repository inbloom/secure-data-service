package org.slc.sli.dal.adapter.transform;

import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/12/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Transform {

    public Entity transformWrite(Entity toTransform);

    public Entity transformRead(Entity toTransform);

    public boolean isTransformable(String type, int fromVersion, int toVersion);
}
