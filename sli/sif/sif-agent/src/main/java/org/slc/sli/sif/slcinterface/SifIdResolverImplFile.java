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


package org.slc.sli.sif.slcinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.net.URISyntaxException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ws.rs.MessageProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.util.Query;

/**
 * Mapping between SIF id and SLI ids. File implementation
 */
@Component
public class SifIdResolverImplFile implements SifIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SifIdResolverImplFile.class);

    @Value("${sli.sif-agent.idmap}")
    private String idmap; // TODO: This is temporary; when we have a clear and approved sif id resolution strategy this can get swapped out

    @Autowired
    private SlcInterface slcInterface;

    class SliId { 
        String type, id, field;
        SliId(String t, String i, String f) { type = t; id = i; field = f; }
    }
    
    private Map<String, SliId> sifToSliIdMap;
    
    @PostConstruct
    public void init() {
        sifToSliIdMap = new HashMap<String, SliId> ();
        try {
            BufferedReader mapFileReader = new BufferedReader(new FileReader(new File(idmap)));
            while (true) {
                String line = mapFileReader.readLine();
                if (line == null) { break; }
                if (line.startsWith("#")) { continue; }
                String [] entry = line.split(",");
                if (entry.length != 4) { continue; }
                String sifId = entry[0];
                String sliValue = entry[1];
                String sliType = entry[2];
                String sliField = entry[3];
                sifToSliIdMap.put(sifId, new SliId(sliType, sliValue, sliField));
            }
            mapFileReader.close();
        } catch (IOException e) {
            LOG.error("Error reading sif id resolver idMap file");
        }
    }
    
    @Override
    public String getSLIGuid(String sifId) {
        // check if it is in the map
        if (!sifToSliIdMap.containsKey(sifId)) { return null; }
        // dig up the SLI Id from the SLI database
        SliId sliId = sifToSliIdMap.get(sifId);
        String id = sliId.id;
        String type = sliId.type;
        String queryField = sliId.field;
        List<Entity> retVal = new ArrayList<Entity>();
        try {
            Query query = BasicQuery.Builder.create().filterEqual(queryField, id).maxResults(1).build();
            slcInterface.read(retVal, type, query);
        } catch (URISyntaxException e) {
            throw new RuntimeException (e);
        } catch (MessageProcessingException e) {
            throw new RuntimeException (e);
        } catch (IOException e) {
            throw new RuntimeException (e);
        } catch (SLIClientException e) {
            throw new RuntimeException (e);
        }
        if (retVal.size() == 0) { return null; };
        if (retVal.size() > 1) {
            throw new RuntimeException ("  SIF Ref ID Resolution error: resolves to more than one entity: " + sifId);
        }
        return retVal.get(0).getId();
    }
}
