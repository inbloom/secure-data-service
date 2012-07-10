package org.slc.sli.sif.publisher;

import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.MessageInfo;
import openadk.library.Publisher;
import openadk.library.Query;
import openadk.library.Zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SifPublisher implements Publisher {

    private static final Logger LOG = LoggerFactory.getLogger(SifPublisher.class);

    @Override
    public void onRequest(DataObjectOutputStream out, Query query, Zone zone, MessageInfo info) throws ADKException {

        LOG.info("Received query:\n" + "\tQuery: " + query + "\n" + "\tZone: " + zone.getZoneId()
                + "\n" + "\tInfo: " + info.getMessage());

    }




}
