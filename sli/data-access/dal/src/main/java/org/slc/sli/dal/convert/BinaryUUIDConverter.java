package org.slc.sli.dal.convert;

import java.util.UUID;

import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class BinaryUUIDConverter implements Converter<String, Binary> {

    
    private Logger log = LoggerFactory.getLogger( BinaryUUIDConverter.class );
    
    @Override
    public Binary convert(String source) {
    
        log.debug( "Trying to convert object"  + source );

            UUID uuid = UUID.fromString(source);
            return MongoIdConverter.convertUUIDtoBinary(uuid);

    }
    
    
}
