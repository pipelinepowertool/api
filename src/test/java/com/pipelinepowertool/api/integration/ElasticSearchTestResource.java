package com.pipelinepowertool.api.integration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.Map;

public class ElasticSearchTestResource implements QuarkusTestResourceLifecycleManager {

    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.8.2")
            .withExposedPorts(9200)
            .withEnv("xpack.security.enabled", "false");

    @Override
    public Map<String, String> start() {

        container.start();
        return Map.of(
                "api.elasticsearch.host", container.getHost(),
                "api.elasticsearch.port", container.getFirstMappedPort().toString(),
                "api.elasticsearch.httpScheme", "http");

    }

    @Override
    public void stop() {
        container.stop();
    }
}
