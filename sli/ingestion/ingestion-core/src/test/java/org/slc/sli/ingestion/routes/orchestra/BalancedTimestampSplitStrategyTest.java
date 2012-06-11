package org.slc.sli.ingestion.routes.orchestra;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordAccess;

/**
 * Tests for TimestampSplitStrategy
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class BalancedTimestampSplitStrategyTest {

    private static String jobId = "it doesn't matter what your job id is!";

    @Autowired
    private BalancedTimestampSplitStrategy balancedTimestampSplitStrategy;

    @Test
    public void testOneChunk() {
        String collectionName = "student";
        IngestionStagedEntity stagedEntity = new IngestionStagedEntity(collectionName, EdfiEntity.STUDENT);

        balancedTimestampSplitStrategy.setSplitChunkSize(50);
        List<WorkNote> workNotes = balancedTimestampSplitStrategy.splitForEntity(stagedEntity, jobId);

        assertEquals(1, workNotes.size());
    }

    @Test
    public void testTwoChunks() {
        String collectionName = "student";
        IngestionStagedEntity stagedEntity = new IngestionStagedEntity(collectionName, EdfiEntity.STUDENT);

        balancedTimestampSplitStrategy.setSplitChunkSize(25);
        List<WorkNote> workNotes = balancedTimestampSplitStrategy.splitForEntity(stagedEntity, jobId);

        assertEquals(2, workNotes.size());
    }

    @Test
    public void testManyChunks() {
        String collectionName = "student";
        IngestionStagedEntity stagedEntity = new IngestionStagedEntity(collectionName, EdfiEntity.STUDENT);

        balancedTimestampSplitStrategy.setSplitChunkSize(5);
        List<WorkNote> workNotes = balancedTimestampSplitStrategy.splitForEntity(stagedEntity, jobId);

        assertEquals(10, workNotes.size());
    }

    @SuppressWarnings("unused")
    @Component
    private static class DummyNeutralRecordAccess implements NeutralRecordAccess {

        private static long[] testRepoTimes = { 0, 1, 1, 2, 2, 2, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 8,
                11, 11, 11, 11, 11, 11, 12, 16, 17, 17, 17, 50, 51, 52, 57, 72, 77, 88, 88, 89, 91, 99 };

        @Override
        public long collectionCountForJob(String collectionNameAsStaged, String jobId) {
            return testRepoTimes.length;
        }

        @Override
        public long countCreationTimeWithinRange(String collectionName, long min, long max, String jobId) {
            long count = 0;
            for (long repoTime : testRepoTimes) {
                if (repoTime >= min && repoTime < max) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public long getMaxCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
            return testRepoTimes[testRepoTimes.length - 1] + 1;
        }

        @Override
        public long getMinCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
            return testRepoTimes[0];
        }

    }

}
