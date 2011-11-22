package org.slc.sli.client;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.client.config.ClientJacksonContextResolver;
import org.slc.sli.client.entitywrapper.StudentWrapper;
import org.slc.sli.domain.Student;

/**
 * RESTful Client for consuming Jersey Web App
 * 
 * @author Dong Liu <dliu@wgen.net>
 * 
 */

@Component
public class StudentClient {
    
    private Logger log = LoggerFactory.getLogger(StudentClient.class);
    
    private static final String RESOURCE_URL = "http://ec2-50-19-203-5.compute-1.amazonaws.com:8080/api/rest/students";
    // private static final String RESOURCE_URL = "http://localhost:8080/api/rest/students";
    private static final String USERNAME = "jimi";
    private static final String PASSWORD = "jimi";
    
    public static final String JSON_FORMAT = "application/json";
    public static final String XML_FORMAT = "application/xml";
    
    private String format = JSON_FORMAT;
    
    private DefaultClientConfig cc;
    private HTTPBasicAuthFilter filter;
    private Client client;
    
    @Autowired
    private ContextResolver<ObjectMapper> contextResolver;
    
    public StudentClient() {
        cc = new DefaultClientConfig();
        filter = new HTTPBasicAuthFilter(USERNAME, PASSWORD);
        cc.getClasses().add(ClientJacksonContextResolver.class);
        client = Client.create(cc);
        client.addFilter(filter);
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setContextResolver(ContextResolver<ObjectMapper> resolver) {
        this.contextResolver = resolver;
    }
    
    /**
     * return all students as List
     * 
     * @return collection of student as list
     * @throws Exception
     */
    public List<Student> getAll() throws Exception {
        String s = "";
        log.info("start process get request");
        log.info("get all request format is:" + format);
        WebResource resource = client.resource(RESOURCE_URL);
        Builder builder = resource.accept(format);
        s = builder.get(String.class);
        
        if (format.contains("json")) {
            return jsonParser(s);
        } else if (format.contains("xml")) {
            return xmlParser(s);
        } else {
            return null;
        }
    }
    
    /**
     * retrieve student object from Web Resource by student id
     * 
     * @param studentId
     * @return
     */
    public Student getStudent(int studentId) {
        WebResource resource = client.resource(RESOURCE_URL + "/" + studentId);
        Builder builder = resource.type(format).accept(format);
        return builder.get(Student.class);
    }
    
    public void updateStudent(Student student) {
        WebResource resource = client.resource(RESOURCE_URL + "/" + student.getStudentId());
        Builder builder = resource.type(format);
        builder.put(student);
    }
    
    /**
     * 
     * @param student
     *            new student entity that will be added
     * @throws Exception
     */
    
    public void addNewStudent(Student student) throws Exception {
        WebResource resource = client.resource(RESOURCE_URL);
        Builder builder = resource.type(format).accept(format);
        builder.post(student);
    }
    
    /**
     * delete the student by passing studentId
     * 
     * @param id
     *            studentId
     * @throws Exception
     */
    
    public void deleteStudent(int id) throws Exception {
        client.resource(RESOURCE_URL + "/" + id).delete();
    }
    
    /**
     * 
     * @param s
     *            input string in xml format that need to be parsed to collection of student object
     * @return collection of student as list
     * @throws Exception
     */
    
    public List<Student> xmlParser(String s) throws Exception {
        JAXBContext context = JAXBContext.newInstance(StudentWrapper.class);
        Unmarshaller u = context.createUnmarshaller();
        log.info("process xml get request");
        List<Student> list = ((StudentWrapper) u.unmarshal(new StreamSource(new StringReader(s)))).getStudents();
        log.info("finish process xml get request");
        return list;
    }
    
    /**
     * 
     * @param s
     *            input string in json format that need to be parsed to collection of student object
     * @return collection of student as list
     * @throws Exception
     */
    public List<Student> jsonParser(String s) throws Exception {
        ObjectMapper mapper = contextResolver.getContext(ObjectMapper.class);
        return Arrays.asList(mapper.readValue(s, Student[].class));
    }
}
