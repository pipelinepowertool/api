package com.pipelinepowertool.api.integration;

import com.pipelinepowertool.FakeDataGenerator;
import com.pipelinepowertool.common.core.database.EnergyReadingRecord;
import com.pipelinepowertool.common.core.database.elasticsearch.ElasticSearchService;
import com.pipelinepowertool.api.utils.DataConstants;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.apache.http.HttpHost;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pipelinepowertool.api.integration.ElasticSearchTestResource.container;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTestResource(ElasticSearchTestResource.class)
public class CucumberIntegrationTest extends CucumberQuarkusTest {

    public static void main(String[] args) {
        runMain(CucumberIntegrationTest.class, args);
    }

    public static ElasticSearchService elasticSearchService;

    @BeforeAll
    public static void setup() throws ExecutionException, InterruptedException, IOException {
        System.out.println("ElasticSearch started");


        HttpHost host = new HttpHost(container.getHost(), container.getFirstMappedPort(), "http");

        System.out.println("Adding test data");
        List<EnergyReadingRecord> testRecords = FakeDataGenerator.createTestRecords(
                DataConstants.AMOUNT_OF_PIPELINES,
                DataConstants.AMOUNT_OF_BRANCHES,
                DataConstants.AMOUNT_OF_BUILDS);
        var partitionedEnergyReadingRecords = partitionEnergyReadingRecords(testRecords);
        elasticSearchService = new ElasticSearchService(host);

        for (List<EnergyReadingRecord> energyReadingRecords : partitionedEnergyReadingRecords) {
            elasticSearchService.send(energyReadingRecords).get();
        }

        System.out.println("Added test data");
    }

    private static Collection<List<EnergyReadingRecord>> partitionEnergyReadingRecords(List<EnergyReadingRecord> energyReadingRecords) {
        int chunkSize = 8000;
        AtomicInteger counter = new AtomicInteger();
        return energyReadingRecords.stream().collect(Collectors.groupingBy(i -> counter.getAndIncrement() / chunkSize)).values();
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("closing ElasticSearch connection");
    }

    @Test
    void testSuite() {
        File bddResourcesDirectory = new File("src/test/resources/bdd");
        assertTrue(bddResourcesDirectory.exists());
    }
}
