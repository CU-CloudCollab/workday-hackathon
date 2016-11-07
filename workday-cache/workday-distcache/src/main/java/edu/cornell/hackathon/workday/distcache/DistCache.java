package edu.cornell.hackathon.workday.distcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

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
		this(new ClientConfig());
	}

	public void store(final String setName, final Object obj) {
		hazelcast.getSet(setName).add(obj);
	}

	public void storeAll(final String setName, final Collection<Object> objs) {
		hazelcast.getSet(setName).addAll(objs);
	}

	public void removeSingle(final String setName, final Object obj) {
		hazelcast.getSet(setName).remove(obj);
	}

	public void removeAll(final String setName, final  Collection<Object> objs) {
		hazelcast.getSet(setName).removeAll(objs);
	}

	public void clearAll(final String setName) {
		hazelcast.getSet(setName).clear();
	}

	public Object find(final String serviceName, final MultivaluedMap<String, String> queryParams) {
		final IMap<String, Object> map = hazelcast.getMap(serviceName);

		final EntryObject e = new PredicateBuilder().getEntryObject();

		final List<PredicateBuilder> predicates = new ArrayList<>();

		for (final Entry<String, List<String>> query : queryParams.entrySet()) {

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

		for (final PredicateBuilder builder : predicates) {
			finalPredicateBuilder = finalPredicateBuilder != null ? finalPredicateBuilder.and(builder) : builder;
		}

		final Predicate queryPredicate = finalPredicateBuilder;

		return map.values(queryPredicate);

	}


}
