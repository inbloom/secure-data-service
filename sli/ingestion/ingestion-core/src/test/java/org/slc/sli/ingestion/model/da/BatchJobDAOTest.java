package org.slc.sli.ingestion.model.da;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for the BatchJobDAO interface
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class BatchJobDAOTest {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Test
    public void testTenantLock() {
        String tenantOne = "tenant1";
        String tenantTwo = "tenant2";

        batchJobDAO.releaseTenantLock(tenantOne);
        batchJobDAO.releaseTenantLock(tenantTwo);

        boolean locked = batchJobDAO.attemptTentantLockForJob(tenantOne, "job123");
        assertTrue(locked);

        boolean lockShouldFail = batchJobDAO.attemptTentantLockForJob(tenantOne, "job456");
        assertFalse(lockShouldFail);

        boolean lockTwo = batchJobDAO.attemptTentantLockForJob(tenantTwo, "job123");
        assertTrue(lockTwo);

        batchJobDAO.releaseTenantLock(tenantOne);

        boolean lockTwoShouldFail = batchJobDAO.attemptTentantLockForJob(tenantTwo, "job456");
        assertFalse(lockTwoShouldFail);

        boolean lockedAgain = batchJobDAO.attemptTentantLockForJob(tenantOne, "job456");
        assertTrue(lockedAgain);

        batchJobDAO.releaseTenantLock(tenantTwo);

        boolean lockedTwoAgain = batchJobDAO.attemptTentantLockForJob(tenantTwo, "job456");
        assertTrue(lockedTwoAgain);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadAttemptTenantLockForJob() {
        batchJobDAO.attemptTentantLockForJob(null, "job123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadReleaseTenantLock() {
        batchJobDAO.releaseTenantLock(null);
    }
}
