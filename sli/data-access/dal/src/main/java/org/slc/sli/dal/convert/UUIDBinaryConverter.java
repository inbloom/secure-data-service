package org.slc.sli.dal.convert;

import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

public class UUIDBinaryConverter implements Converter<Binary, String> {

    @Override
    public String convert(Binary source) {
        return MongoIdConverter.binaryToUUIDString( source );
    }
    
}
