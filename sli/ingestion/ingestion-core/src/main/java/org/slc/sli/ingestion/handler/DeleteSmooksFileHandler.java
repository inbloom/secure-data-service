/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.milyn.Smooks;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * @author unavani
 *
 */
@Component
public class DeleteSmooksFileHandler extends SmooksFileHandler {

    private static final String TENANT_ID = "tenantId";
    private static final String EXTERNAL_ID = "externalId";
    private DeleteSmooksFileObserverable observerable = new DeleteSmooksFileObserverable();

    @Override
    protected Smooks initializeSmooks(IngestionFileEntry ingestionFileEntry,
            ErrorReport errorReport) throws IOException, SAXException {
        return sliSmooksFactory.createInstance(ingestionFileEntry, errorReport, true);
    }

    @Override
    protected void postSmookFileHandling(IngestionFileEntry ingestionFileEntry,
            FileProcessStatus fileProcessStatus, SmooksEdFiVisitor visitAfter) {

        List<NeutralRecordEntity> neutralRecordEntities = new ArrayList<NeutralRecordEntity>();
        for (NeutralRecord neutralRecord : visitAfter.getNeutralRecords()) {
            NeutralRecordEntity recordEntity = new NeutralRecordEntity(neutralRecord);
            recordEntity.setMetaDataField(TENANT_ID, TenantContext.getTenantId());
            recordEntity.setMetaDataField(EXTERNAL_ID, neutralRecord.getLocalId());
            neutralRecordEntities.add(recordEntity);
        }

        updateObservers(neutralRecordEntities);
    }

    public void addObserver(Observer o)
    {
        observerable.addObserver(o);
    }

    private void updateObservers(List<NeutralRecordEntity> neutralRecords) {
        observerable.updateObservers(neutralRecords);
    }

    public class DeleteSmooksFileObserverable extends Observable {

        List<Observer> observerList = new ArrayList<Observer>();

        public void addObserver(Observer o)
        {
            if(!observerList.contains(o))
                super.addObserver(o);
        }

        public void updateObservers(List<NeutralRecordEntity> neutralRecords) {
          setChanged();
          notifyObservers(neutralRecords);
        }
    }
}
