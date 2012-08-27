package org.slc.sli.dal;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 8/24/12
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MongoStat {
    private int dbHitCount;

    public MongoStat(int dbHitCount) {
        this.dbHitCount = dbHitCount;
    }

    public MongoStat() {
        this.dbHitCount = 0;
    }

    public int getDbHitCount() {
        return dbHitCount;
    }

    public void clear() {
        dbHitCount = 0;
    }
    public void increamentHitCount(){
        dbHitCount ++;
    }
}
