package edu.cornell.hackathon.workday.distcache;

import java.util.Collection;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;

public class DistCache {

	private final HazelcastInstance hazelcast;

	public DistCache(final ClientConfig config) {
		hazelcast = HazelcastClient.newHazelcastClient(config);
	}

    public DistCache(final String hazelcastURL) {
        ClientConfig config = new ClientConfig();
        ClientNetworkConfig network = config.getNetworkConfig();
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

}
