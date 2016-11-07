package edu.cornell.hackathon.workday.fetcher.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.cornell.hackathon.workday.fetcher.config.Config;

public class WorkdayConsumerServiceTest {
    private WorkdayConsumerService workdayConsumerService;

    @Test
    public void testFetch() {
        Config config = new Config();

        Map<String, String> serviceMap = new HashMap<>();
        serviceMap.put("231", "http://mockbin.org/bin/33e47c84-fa8f-4ca2-ad93-3deb4be9e841");

        config.setServiceMap(serviceMap);

        workdayConsumerService = new WorkdayConsumerService(config);

        Assert.assertTrue(workdayConsumerService.fetchWorkdayData());
    }
}
