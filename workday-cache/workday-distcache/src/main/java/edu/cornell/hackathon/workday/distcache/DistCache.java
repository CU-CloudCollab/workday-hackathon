package edu.cornell.hackathon.workday.distcache;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

public class DistCache {

	private final HazelcastInstance hazelcast;

	public DistCache(final ClientConfig config) {
		hazelcast = HazelcastClient.newHazelcastClient(config);
	}

	public DistCache() {
		this(new ClientConfig());
	}

}
