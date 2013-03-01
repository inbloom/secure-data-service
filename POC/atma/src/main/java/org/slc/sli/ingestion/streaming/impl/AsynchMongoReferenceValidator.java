package org.slc.sli.ingestion.streaming.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.streaming.ReferenceValidator;

/**
 * Validates references by asynchronously issuing mongo queries
 *
 * @author dkornishev
 *
 */
@Component
public class AsynchMongoReferenceValidator implements ReferenceValidator {

    @Resource(name = "mongoEntityRepository")
    private Repository<Entity> repo;

    private BlockingQueue<Pair<String, String>> queue = new LinkedBlockingQueue<Pair<String, String>>();

    private Thread t;

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (int i = 50; i > 0; i--) {
                            Pair<String, String> ref = queue.take();
                            if (!repo.exists(getCollectionName(ref.getLeft()), ref.getRight())) {
                                queue.offer(ref, 1, TimeUnit.MILLISECONDS);
                            }
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // NOPMD OK to print stack in POC code
                    }
                }
            }

            /**
             * Figures out which collection to query FIXME needs to do actual figuring out
             *
             * @param elementName
             * @return
             */
            private String getCollectionName(String elementName) {
                return elementName;
            }

        };

        t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void addForValidation(String elementName, String value) {
        try {
            queue.offer(Pair.of(elementName, value), 1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(); // NOPMD OK to print stack in POC code
        }
    }

    @Override
    public Set<Pair<String, String>> getRemainingReferences() {
        return new HashSet<Pair<String, String>>(this.queue);
    }

}
