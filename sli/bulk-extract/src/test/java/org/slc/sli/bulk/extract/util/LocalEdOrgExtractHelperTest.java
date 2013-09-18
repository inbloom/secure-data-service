package org.slc.sli.bulk.extract.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LocalEdOrgExtractHelperTest {

    private static final String STATE_EDUCATION_AGENCY = "State Education Agency";

    @InjectMocks
    EdOrgExtractHelper helper = new EdOrgExtractHelper();

    @Mock
    Repository<Entity> repository;

    @Before
    public void setup() throws Exception {
        repository = Mockito.mock(MongoEntityRepository.class);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTopLevelLEAsTest() {
        String seaId = "1234";

        when(repository.findAllIds(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ORGANIZATION_CATEGORIES,
                        NeutralCriteria.CRITERIA_IN, Arrays.asList(STATE_EDUCATION_AGENCY)))))
                .thenReturn(Arrays.asList(seaId));

        final List<String> edOrgIds = Arrays.asList("1", "2", "3", "4");
        when(repository.findAllIds(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(new NeutralCriteria(
                ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, NeutralCriteria.CRITERIA_IN,
                Arrays.asList(seaId))))).thenReturn(edOrgIds);


        final Set<String> topLevelLEAs = helper.getTopLevelLEAs();
        topLevelLEAs.removeAll(edOrgIds);
        assertTrue(topLevelLEAs.isEmpty());
    }

    @Test
    public void getBulkExtractAppsTest() {

        when(repository.findAll("application", new NeutralQuery())).thenReturn(
                Arrays.asList(
                        buildAppEntity("1", true, true),
                        buildAppEntity("2", false, false),
                        buildAppEntity("3", true, false),
                        buildAppEntity("4", false, true),
                        buildAppEntity("5", true, true)
                )
        );

        Set<String> bulkExtractApps = helper.getBulkExtractApps();
        bulkExtractApps.removeAll(Arrays.asList("1","5"));
        assertTrue(bulkExtractApps.isEmpty());

    }

    @Test
    public void getBulkExtractEdOrgsPerAppTest() {

        getBulkExtractAppsTest(); // mock applications

        Entity authOne = buildAuthEntity("1", Arrays.asList("edOrg1", "edOrg2"));
        Entity authTwo = buildAuthEntity("5", new ArrayList<String>());

        List<Entity> auths = Arrays.asList(authOne, authTwo);
        when(repository.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(auths);

        Map<String, Set<String>> bulkExtractEdOrgsPerApp = helper.getBulkExtractEdOrgsPerApp();

        assertTrue(bulkExtractEdOrgsPerApp.size() == 2);
        assertTrue(bulkExtractEdOrgsPerApp.get(authOne.getBody().get("applicationId")).containsAll(
                (Collection<?>) authOne.getBody().get("edorgs")));
        assertTrue(bulkExtractEdOrgsPerApp.get(authTwo.getBody().get("applicationId")).isEmpty());

    }

    private Entity buildAuthEntity(String applicationId, List<String> edOrgs) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("applicationId", applicationId);
        body.put("edorgs", edOrgs);
        return new MongoEntity("applicationAuthorization", body);
    }


    private Entity buildAppEntity(String id, boolean isBulkExtract, boolean isApproved) {
        Map<String, Object> body = new HashMap<String, Object>();

        if (isBulkExtract) {
            body.put("isBulkExtract", isBulkExtract);
        }

        if (isApproved) {
            Map<String, Object> registration = new HashMap<String, Object>();
            registration.put("status", "APPROVED");
            body.put("registration", registration);
        }

        return new MongoEntity(EntityNames.APPLICATION, id, body, new HashMap<String, Object>());
    }

}
