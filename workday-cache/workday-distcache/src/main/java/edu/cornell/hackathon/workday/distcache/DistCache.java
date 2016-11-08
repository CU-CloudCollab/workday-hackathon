package edu.cornell.hackathon.workday.distcache;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

import edu.cornell.hackathon.workday.jobandperson.model.ReportEntryType;

public class DistCache {

    private final HazelcastInstance hazelcast;

    public DistCache(final ClientConfig config) {
        hazelcast = HazelcastClient.newHazelcastClient(config);
    }

    public DistCache(final String hazelcastURL) {
        final ClientConfig config = new ClientConfig();
        final ClientNetworkConfig network = config.getNetworkConfig();
        network.addAddress(hazelcastURL);

        hazelcast = HazelcastClient.newHazelcastClient(config);
    }

    public DistCache() {
        hazelcast = Hazelcast.newHazelcastInstance();
    }

    public void store(final String setName, final ReportEntryType obj) {
        hazelcast.getMap(setName).put(obj.getNetid(), obj);
    }

    public void storeAll(final String setName, final Collection<Object> objs) {
        hazelcast.getMap(setName).put(UUID.randomUUID(), objs);
    }

    public void removeSingle(final String setName, final Object obj) {
        hazelcast.getSet(setName).remove(obj);
    }

    public void removeAll(final String setName, final Collection<Object> objs) {
        hazelcast.getSet(setName).removeAll(objs);
    }

    public void clearAll(final String setName) {
        hazelcast.getMap(setName).clear();
    }

    public Object find(final String serviceName, final MultivaluedMap<String, String> queryParams)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        final IMap<String, Object> map = hazelcast.getMap(serviceName);

        System.out.println(map.size());

        if (queryParams != null && !queryParams.isEmpty()) {
            final EntryObject e = new PredicateBuilder().getEntryObject();

            final List<PredicateBuilder> predicates = new ArrayList<>();

            Map<String, Object> multiLevelQueries = new HashMap<>();

            for (final Entry<String, List<String>> query : queryParams.entrySet()) {
                String[] queryParts = query.getKey().split("\\.");

                if (queryParts.length > 1) {
                    multiLevelQueries.put(query.getKey(), query.getValue().get(0));
                    continue;
                }

                final String key = query.getKey();

                if (query.getValue().size() == 1) {
                    predicates.add(e.get(key).equal(query.getValue().get(0)));
                } else if (query.getValue().size() > 1) {

                    final List<PredicateBuilder> tempPreds = new ArrayList<>();

                    for (final String value : query.getValue()) {
                        tempPreds.add(e.get(key).equal(value));
                    }

                    PredicateBuilder tempPred = null;

                    for (final PredicateBuilder temp : tempPreds) {
                        tempPred = tempPred != null ? tempPred.or(temp) : temp;
                    }

                }
            }

            PredicateBuilder finalPredicateBuilder = null;

            Collection<Object> values = null;
            
            if (!predicates.isEmpty()) {
                System.out.println("Executing predicate builder");
                for (final PredicateBuilder builder : predicates) {
                    finalPredicateBuilder = finalPredicateBuilder != null ? finalPredicateBuilder.and(builder)
                            : builder;
                }

                final Predicate queryPredicate = finalPredicateBuilder;

                values = map.values(queryPredicate);
            } else {
                values = map.values();
            }


            Collection<Object> finalValues = new ArrayList<>();

            if (!multiLevelQueries.isEmpty()) {
                for (Entry<String, Object> multiLevelQuery : multiLevelQueries.entrySet()) {
                    String[] queryParts = multiLevelQuery.getKey().split("\\.");
                    for (Object value : values) {
                        if (isEqual(value, queryParts, multiLevelQuery.getValue())) {
                            finalValues.add(value);
                        }
                    }
                }
            } else {
                finalValues = values;
            }

            return finalValues;

        } else {
            return map.get("foo");
        }

    }

    private boolean isEqual(Object object, String[] queryParts, Object equalValue) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if (queryParts.length > 0) {
            if (object instanceof Collection) {
                for (Object listObj : (Collection) object) {
                    if (isEqual(listObj, queryParts, equalValue)) {
                        return true;
                    }
                }
                return false;
            } else {
                String field = queryParts[0].substring(0, 1).toUpperCase() + queryParts[0].substring(1);
                Object comparisonObj = object.getClass().getMethod("get" + field, null).invoke(object, null);

                String[] subQueryParts = new String[queryParts.length - 1];
                for (int j = 1; j < queryParts.length; j++) {
                    subQueryParts[j - 1] = queryParts[j];
                }

                if (comparisonObj instanceof Collection) {
                    for (Object listObj : (Collection) comparisonObj) {
                        if (isEqual(listObj, subQueryParts, equalValue)) {
                            return true;
                        }
                    }
                    return false;
                }
                return isEqual(comparisonObj, subQueryParts, equalValue);
            }
        } else {
            return object.equals(equalValue);
        }
    }

}
