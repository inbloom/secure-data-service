package org.slc.sli.api.resources.v1.view.impl;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;

/**
 * Provides data about students and attendance to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentAttendanceOptionalFieldAppender implements OptionalFieldAppender {

    private final Logger logger = LoggerFactory.getLogger(StudentAttendanceOptionalFieldAppender.class);

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @Autowired
    private MongoTemplate template;

    public StudentAttendanceOptionalFieldAppender() {
    }

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {
        String collectionName = "attagg" + System.currentTimeMillis();
        Map<String, EntityBody> studentAttendances = new HashMap<String, EntityBody>();

        //get the section Ids
        Set<String> sectionIds = optionalFieldAppenderHelper.getSectionIds(entities);

        //execute the map/reduce
        if (!executeMapReduce(collectionName, sectionIds)) return entities;

        //get the attendance aggregate objects
        List<Object> attendanceEntities = template.findAll(Object.class, collectionName);
        //drop the temp collection
        template.dropCollection(collectionName);

        for (Object entity : attendanceEntities) {
            Map<String, Object> map = (Map<String, Object>) entity;

            String id = (String) map.get("_id");
            Map<String, Object> value = (Map<String, Object>) map.get("value");
            studentAttendances.put(id, new EntityBody((Map<String, Object>) value.get("attendance")));
        }

        //add attendances to student's entityBody
        for (EntityBody entity : entities) {
            String id = (String) entity.get("id");
            EntityBody attendancesBody = new EntityBody();

            attendancesBody.put(ResourceNames.ATTENDANCES, studentAttendances.get(id));
            entity.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
        }

        return entities;
    }

    protected boolean executeMapReduce(String collectionName, Set<String> sectionIds) {
        StringBuffer buffer = new StringBuffer();
        String separator = "";

        //construct the map/reduce command
        //need to extract the constants out of this
        buffer.append("db.runCommand( {");
        buffer.append("mapreduce:\"studentSectionAssociation\",");
        buffer.append("map:mapSectionAttendance,");
        buffer.append("reduce:reduceSectionAttendance,");
        buffer.append("query: { \"body.sectionId\":{\"$in\": [");

        for (String sectionId : sectionIds) {
            buffer.append(separator);
            buffer.append("\"");
            buffer.append(sectionId);
            buffer.append("\"");
            separator = ",";
        }

        buffer.append("]}},");

        buffer.append("out: { reduce: \"");
        buffer.append(collectionName);
        buffer.append("\" }");
        buffer.append("});");

        return template.executeCommand("{\"$eval\":\"" + StringEscapeUtils.escapeJava(buffer.toString()) + "\"}").ok();
    }

}
