package edu.cornell.hackathon.workday.fetcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.cornell.hackathon.workday.distcache.DistCache;
import edu.cornell.hackathon.workday.fetcher.config.Config;
import edu.cornell.hackathon.workday.fetcher.service.WorkdayConsumerService;

public class App {

	public static void main(final String[] args) {

        Config config = new Config();
        config.setUsername(System.getenv("USERNAME"));
        config.setPassword(System.getenv("PASSWORD"));

        String servicesString = System.getenv("SERVICES");

        if (servicesString != null) {
            String[] servicesEntries = servicesString.split("\\|");
            Map<String, String> services = new HashMap<>();
            for (String entryStr : servicesEntries) {
                String[] entry = entryStr.split("::");
                services.put(entry[0], entry[1]);
            }

            config.setServiceMap(services);
        }

        //TODO read where hazelcast client config file is from config
        String hazelcastURL = System.getenv("HAZELCAST");
        final DistCache distCache = new DistCache(hazelcastURL);

        final WorkdayConsumerService consumerService = new WorkdayConsumerService(config, distCache);

        Integer refreshInterval = Integer.parseInt(System.getenv("INTERVAL"));

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                System.out.println("Starting fetch");
                System.out.println("Result of fetching: " + consumerService.fetchWorkdayData());
            }
        }, 0, refreshInterval, TimeUnit.SECONDS);
	}

}
