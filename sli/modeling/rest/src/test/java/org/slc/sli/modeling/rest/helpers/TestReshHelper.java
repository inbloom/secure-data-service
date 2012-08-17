package org.slc.sli.modeling.rest.helpers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Link;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Option;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Resource;

/**
 * JUnit test for RestHelper class.
 *
 * @author wscott
 *
 */
public class TestReshHelper {

    private static final ArrayList<String> TYPE = new ArrayList<String>(0);
    private static final String QUERY_TYPE = "queryType";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Method> METHODS = new ArrayList<Method>(0);
    private static final ArrayList<Resource> RESOURCES = new ArrayList<Resource>(0);
    private static final String NAME = "name";
    private static final QName TYPE_QNAME = new QName("qname");
    private static final String PATH = "path";
    private static final String FIXED = "fixed";
    private static final boolean REPEATING = true;
    private static final boolean REQUIRED = true;
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String RESOURCE_TYPE = "resourceType";
    private static final String REL = "rel";
    private static final String REV = "rev";
    private static final ArrayList<Option> OPTIONS = new ArrayList<Option>(0);
    private static final Link LINK = new Link(RESOURCE_TYPE, REL, REV, DOC);
    private static final String R1P1_ID = "r1p1";
    private static final String R2P1_ID = "r2p1";
    private static final String R3P1_ID = "r3p1";
    private static final String R3P2_ID = "r3p2";
    private static final String R1_ID = "r1";
    private static final String R2_ID = "r2";
    private static final String R3_ID = "r3";

    @Test
    public void testConstructor() {
        new RestHelper();
    }

    @Test
    public void testComputeRequestTemplateParamsEmptyAncestors() {
        Stack<Resource> resources = new Stack<Resource>();

        Param r1p1 = new Param(NAME, ParamStyle.TEMPLATE, R1P1_ID, TYPE_QNAME, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
        List<Param> r1Params = new ArrayList<Param>(1);
        r1Params.add(r1p1);
        Resource r1 = new Resource(R1_ID, TYPE, QUERY_TYPE, PATH, DOC, r1Params, METHODS, RESOURCES);

        List<Param> templateParams = RestHelper.computeRequestTemplateParams(r1, resources);
        assertEquals(1, templateParams.size());
        assertEquals(R1P1_ID, templateParams.get(0).getId());
    }

    @Test
    public void testComputeRequestTemplateParams() {

        Stack<Resource> resources = new Stack<Resource>();

        Param r1p1 = new Param(NAME, ParamStyle.TEMPLATE, R1P1_ID, TYPE_QNAME, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
        List<Param> r1Params = new ArrayList<Param>(1);
        r1Params.add(r1p1);
        Resource r1 = new Resource(R1_ID, TYPE, QUERY_TYPE, PATH, DOC, r1Params, METHODS, RESOURCES);

        Param r2p1 = new Param(NAME, ParamStyle.TEMPLATE, R2P1_ID, TYPE_QNAME, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
        Param r2pNonTemplate = new Param(NAME, ParamStyle.QUERY, "nontemplate", TYPE_QNAME, DEFAULT_VALUE, REQUIRED,
                REPEATING, FIXED, PATH, DOC, OPTIONS, LINK);
        List<Param> r2Params = new ArrayList<Param>(2);
        r2Params.add(r2p1);
        r2Params.add(r2pNonTemplate);
        Resource r2 = new Resource(R2_ID, TYPE, QUERY_TYPE, PATH, DOC, r2Params, METHODS, RESOURCES);

        Param r3p1 = new Param(NAME, ParamStyle.TEMPLATE, R3P1_ID, TYPE_QNAME, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
        Param r3p2 = new Param(NAME, ParamStyle.TEMPLATE, R3P2_ID, TYPE_QNAME, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
        List<Param> r3Params = new ArrayList<Param>(2);
        r3Params.add(r3p1);
        r3Params.add(r3p2);
        Resource r3 = new Resource(R3_ID, TYPE, QUERY_TYPE, PATH, DOC, r3Params, METHODS, RESOURCES);

        resources.push(r2);
        resources.push(r3);

        List<Param> templateParams = RestHelper.computeRequestTemplateParams(r1, resources);
        assertEquals(4, templateParams.size());
        assertEquals(R3P1_ID, templateParams.get(0).getId());
        assertEquals(R3P2_ID, templateParams.get(1).getId());
        assertEquals(R2P1_ID, templateParams.get(2).getId());
        assertEquals(R1P1_ID, templateParams.get(3).getId());
    }

    @Test
    public void testReverse() {
        List<Integer> numbers = new ArrayList<Integer>(3);
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        Integer crntNumber = 3;
        assertEquals(3, reversedNumbers.size());
        for (Integer i : reversedNumbers) {
            assertEquals(crntNumber--, i);
        }
    }

    @Test
    public void testReverseEmpty() {
        List<Integer> numbers = new ArrayList<Integer>(0);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        assertEquals(0, reversedNumbers.size());
    }

    @Test
    public void testReverseOne() {
        List<Integer> numbers = new ArrayList<Integer>(1);
        numbers.add(9);

        List<Integer> reversedNumbers = RestHelper.reverse(numbers);
        assertEquals(1, reversedNumbers.size());
        assertEquals((Integer) 9, (Integer) reversedNumbers.get(0));
    }

}
