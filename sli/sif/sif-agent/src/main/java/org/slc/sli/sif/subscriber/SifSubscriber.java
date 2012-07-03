package org.slc.sli.sif.subscriber;

import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.Subscriber;
import openadk.library.Zone;

public class SifSubscriber implements Subscriber {

    @Override
    public void onEvent(Event event, Zone zone, MessageInfo info) throws ADKException {
        System.out.println("SifSubscriber.onEvent");
        System.out.println(info);
    }

}
