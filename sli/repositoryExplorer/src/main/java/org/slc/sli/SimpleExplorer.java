package org.slc.sli;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.slc.sli.validation.schema.SchemaReferencePath;
import org.slc.sli.validation.schema.SchemaReferencesMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;


//--vm options
//        -Dsli.conf=../config/properties/sli.properties
//        -Dsli.security.truststore.path=../data-access/dal/keyStore/trustey.jks
@Component
public class SimpleExplorer {
    
   private final String SEP = "    ";
    
   private ArrayListMultimap<Pair, Entity> children = ArrayListMultimap.create();

   private Ordering<Entity> comparator= Ordering.from(new Comparator<Entity>() {
       @Override
       public int compare(Entity entity1, Entity entity2) {
           return entity1.getType().compareTo(entity2.getType());
       };
   });

   @Autowired
   private MongoEntityRepository mongo;

   @Autowired
   private SchemaReferencesMetaData metaData;

   @Autowired
   private NaturalKeyExtractor keyExtractor;

   @PostConstruct
   public void init()
   {
       TenantContext.setTenantId("Midgar");
       recurse      ("educationOrganization", "bd086bae-ee82-4cf2-baf9-221a9407ea07");
       StringWriter writer = new StringWriter();
       String out = print(Pair.of("educationOrganization", "bd086bae-ee82-4cf2-baf9-221a9407ea07"), null, writer, 0);
       String print = "[" + out + "]";
   }

   private void recurse(String type, String id) {
       List<SchemaReferencePath> referencesTo = metaData.getReferencesTo(type);
       for(SchemaReferencePath reference:referencesTo) {
           NeutralQuery q = new NeutralQuery(new NeutralCriteria(reference.getFieldPath(), NeutralCriteria.OPERATOR_EQUAL, id));
           Iterable<Entity> referring = mongo.findAll(reference.getEntityName(), q);
           children.putAll(Pair.of(type, id), referring);
           for(Entity refereringEntity:referring) {
               if(!children.containsKey(Pair.of(refereringEntity.getType(), refereringEntity.getEntityId()))) {
                   recurse(refereringEntity.getType(), refereringEntity.getEntityId());
               }
           }
       }
    }

    String print(Pair p, Entity e, StringWriter writer, int depth) {
        try {
            String key = keyExtractor.getNaturalKeys(e).toString();
        } catch(Exception ex) {/*Cannot find key!*/}
        writer.append(StringUtils.repeat(SEP, depth) + p.toString() + "\n");
        List<Entity> c = comparator.sortedCopy(children.get(p));
        ++depth;
        for(Entity child:c) {
            print(Pair.of(child.getType(), child.getEntityId()), child, writer, depth);
        }
        return writer.toString();
    }
}
