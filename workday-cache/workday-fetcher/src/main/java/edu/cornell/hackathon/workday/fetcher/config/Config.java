package edu.cornell.hackathon.workday.fetcher.config;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

    private String username;
    private String password;
    private String hazelcastConfig;
    private Map<String, String> serviceMap;
}
