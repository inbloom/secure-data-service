package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
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
    private Client getClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name", true).build();
        return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        SearchResponse response = Converter.getQuery(getClient(), neutralQuery).execute().actionGet();
        return Converter.toEntityCol(response.hits());
    }

    /**
     * Converter SLI to/from ES
     * @author agrebneva
     *
     */
    private static class Converter {
        static Collection<Entity> toEntityCol(SearchHits hits) {
            Collection<Entity> col = new ArrayList<Entity>();
            for (SearchHit hit : hits) {
                col.add(new SearchHitEntity(hit));
            }
            return col;
        }

        static SearchRequestBuilder getQuery(Client client, NeutralQuery query) {

            SearchRequestBuilder srb = client.prepareSearch(TenantContext.getTenantId().toLowerCase()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            String queryString = null;
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            BoolFilterBuilder bfb = FilterBuilders.boolFilter(), inFilter;

            for (NeutralCriteria s : query.getCriteria()) {
                if ("query".equals(s.getKey())) {
                  queryString = ((String) s.getValue()).trim();
                  queryString = "+" + StringUtils.join(queryString.split(" "), "* +") + "*";
                  bqb.must(new QueryStringQueryBuilder(queryString.toLowerCase()));
                } else {
                    bfb.must(FilterBuilders.termsFilter(s.getKey(), getTermTokens(s.getValue())));
                }
            }
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
