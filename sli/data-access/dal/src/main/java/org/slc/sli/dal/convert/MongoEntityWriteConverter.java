package org.slc.sli.dal.convert;

import org.slc.sli.domain.MongoEntity;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;

public class MongoEntityWriteConverter implements
		Converter<MongoEntity, DBObject> {

	public DBObject convert(MongoEntity e) {
		return e.toDBObject();
	}

}
