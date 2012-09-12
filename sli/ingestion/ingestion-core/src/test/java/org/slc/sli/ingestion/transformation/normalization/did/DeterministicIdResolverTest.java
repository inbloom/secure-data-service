package org.slc.sli.ingestion.transformation.normalization.did;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

public class DeterministicIdResolverTest {
    @InjectMocks
    @Autowired
    DeterministicIdResolver didResolver;

    @Mock
    private UUIDGeneratorStrategy didGenerator;

    @Mock
    private DidEntityConfigFactory didEntityConfigs;

    @Mock
    private DidRefConfigFactory didRefConfigs;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
