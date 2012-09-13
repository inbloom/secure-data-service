package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 *
 * @author agrebneva
 *
 */
public class ESRepository extends SimpleEntityRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleEntityRepository.class);

    private String esHost = "localhost";
    private int esPort = 9200;
    private int esTransportPort = 9300;

    private Client getClient() {
        // TODO: can't use transport client anymore
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name", true).build();
        return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(esHost, esTransportPort));
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {

        // send an elasticsearch REST query
        String query = Converter.getQuery(getClient(), neutralQuery).toString();
        HttpEntity<String> response = sendRESTQuery(query);

        // convert the response to search hits
        return Converter.toEntityCol(response);
    }

    /**
     * Send REST query to elasticsearch server
     * @param query
     * @return
     */
    private HttpEntity<String> sendRESTQuery(String query) {

        RestTemplate templateIn = new RestTemplate();
        String url = "http://" + esHost + ":" + esPort + "/" + TenantContext.getTenantId().toLowerCase() + "/_search";
        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(query, headers);
        HttpEntity<String> response = null;
        try {
            response = templateIn.exchange(url, method, entity, String.class);
        } catch(Exception e) {
            System.out.println("ERROR");
        }
        return response;
    }

    /**
     * Converter SLI to/from ES
     * @author agrebneva
     *
     */
    private static class Converter {

        /**
         * Converts elasticsearch search hits to collection of entities
         * @param hits
         * @return
         */
        static Collection<Entity> toEntityCol(SearchHits hits) {
            Collection<Entity> col = new ArrayList<Entity>();
            for (SearchHit hit : hits) {
                col.add(new SearchHitEntity(hit));
            }
            return col;
        }

        /**
         * Converts elasticsearch http response to collection of entities
         * @param response
         * @return
         */
        static Collection<Entity> toEntityCol(HttpEntity<String> response) {

            ObjectMapper mapper = new ObjectMapper();
            Collection<SearchHitEntity> hits = new ArrayList<SearchHitEntity>();
            JsonNode node = null;
            try {
                node = mapper.readTree(response.getBody());
                JsonNode hitsNode = node.get("hits").get("hits");

                for(int i=0; i<hitsNode.size(); i++) {
                    JsonNode hitNode = hitsNode.get(i);
                    String id = hitNode.get("_id").getTextValue();
                    String type = hitNode.get("_type").getTextValue();
                    JsonNode bodyNode = hitNode.get("fields").get("body");
                    Map<String, Object> body = mapper.readValue(bodyNode, new TypeReference<Map<String, Object>>() {});
                    JsonNode metaDataNode = hitNode.get("fields").get("metaData");
                    Map<String, Object> metaData = mapper.readValue(metaDataNode, new TypeReference<Map<String, Object>>() {});
                    SearchHitEntity hit = new SearchHitEntity(id, type, body, metaData);
                    hits.add(hit);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Collection<Entity> ret = new ArrayList<Entity>(hits);
            return ret;
        }

        /**
         * Build elasticsearch query
         * @param client
         * @param query
         * @return
         */
        static SearchRequestBuilder getQuery(Client client, NeutralQuery query) {

            SearchRequestBuilder srb = client.prepareSearch(TenantContext.getTenantId().toLowerCase()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            String queryString = null;
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            BoolFilterBuilder bfb = FilterBuilders.boolFilter(), inFilter;

            // set query criteria
            for (NeutralCriteria s : query.getCriteria()) {
                if ("query".equals(s.getKey())) {
                  queryString = ((String) s.getValue()).trim();
                  queryString = "+" + StringUtils.join(queryString.split(" "), "* +") + "*";
                  bqb.must(new QueryStringQueryBuilder(queryString.toLowerCase()));
                } else {
                    bfb.must(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                }
            }
            
            // set context metadata filter
            if (!query.getOrQueries().isEmpty()) {
                for (NeutralQuery q : query.getOrQueries()) {
                    if (q.getCriteria().size() == 1) {
                        NeutralCriteria s = q.getCriteria().get(0);
                        bfb.should(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                    } else if (q.getCriteria().size() > 1) {
                        inFilter = FilterBuilders.boolFilter();
                        for (NeutralCriteria s : q.getCriteria()) {
                            inFilter.must(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                        }
                        bfb.should(inFilter);
                    }
                }
            }
            srb.setQuery(bqb).setFilter(bfb).addFields("id", "body", "metaData").setFrom(query.getOffset()).setSize(query.getLimit());
            LOG.info(srb.toString());
            return srb;
        }

        @SuppressWarnings("unchecked")
        private static String[] getTermTokens(Object value) {
            return (value instanceof List) ? ((List<String>) value).toArray(new String[0]) : ((String) value).split(",");
        }
    }


    /**
     * Simple adapter for SearchHits to Entity
     * @author agrebneva
     *
     */
    private static final class SearchHitEntity implements Entity {
        private Map<String, Object> body;
        private Map<String, Object> metaData;
        private String type;
        private String id;

        @SuppressWarnings("unchecked")
        SearchHitEntity(SearchHit hit) {
            this.body = new HashMap<String, Object>((Map<String, Object>) hit.getFields().get("body").getValue());
            body.put("type", hit.getType());
            this.type = hit.getType();
            this.id = hit.getId();
            this.metaData = hit.getFields().get("metaData").getValue();
        }

        SearchHitEntity(String id, String type, Map<String, Object> body, Map<String, Object> metaData) {
            this.id = id;
            this.type = type;
            this.body = body;
            this.body.put("type", type);
            this.metaData = metaData;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String getEntityId() {
            return this.id;
        }

        @Override
        public Map<String, Object> getBody() {
            return this.body;
        }

        @Override
        public Map<String, Object> getMetaData() {
            return metaData;
        }

        @Override
        public CalculatedData<Map<String, Integer>> getAggregates() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public CalculatedData<String> getCalculatedValues() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
