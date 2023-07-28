package com.pipelinepowertool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pipelinepowertool.common.core.database.EnergyReading;
import com.pipelinepowertool.common.core.database.EnergyReadingRecord;
import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadata;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.pipelinepowertool.api.utils.DataConstants.*;

public class FakeDataGenerator {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        List<EnergyReadingRecord> testRecords = createTestRecordsRandomized(70);

        StringBuilder stringBuilder = new StringBuilder();
        for (EnergyReadingRecord testRecord : testRecords) {
            stringBuilder.append("{ \"index\" : { \"_index\" : \"readings\"} }").append("\n");
            stringBuilder.append(mapper.writeValueAsString(testRecord)).append("\n");
        }
        try (FileWriter fileWriter = new FileWriter("/home/sdenboer/code/scriptie/docker/elastic/fakedata.json")) {
            fileWriter.write(stringBuilder.toString());
        }

    }

    public static List<EnergyReadingRecord> createTestRecords(int amountPipelines, Integer amountBranches, int amountBuilds) {
        List<EnergyReadingRecord> readingRecords = new ArrayList<>();
        OffsetDateTime pipelineEnd = OffsetDateTime.now();
        OffsetDateTime pipelineStart = pipelineEnd.minusMinutes(PIPELINE_DURATION_MINUTES);
        boolean skipBranch = (amountBranches == null);
        for (int i = 0; i < amountPipelines; ++i) {
            String job = PIPELINE_NAME_PREFIX + i;
            int loopOverBranch = skipBranch ? 1 : amountBranches;
            for (int j = 0; j < loopOverBranch; j++) {
                String branchName = skipBranch ? null : BRANCH_NAME_PREFIX + j;
                for (int k = 0; k < amountBuilds; k++) {
                    JenkinsMetadata jenkinsMetadata = new JenkinsMetadata(job, branchName, (long) k,
                            pipelineStart, pipelineEnd);
                    EnergyReading energyReading = new EnergyReading(BigDecimal.valueOf(JOULES * (PIPELINE_DURATION_MINUTES * 60)), BigDecimal.valueOf(UTILIZATION));
                    readingRecords.add(new EnergyReadingRecord(energyReading, jenkinsMetadata));
                }
            }
        }
        return readingRecords;
    }
    public static List<EnergyReadingRecord> createTestRecordsRandomized(int amountPipelines) {
        List<EnergyReadingRecord> readingRecords = new ArrayList<>();
        for (int i = 0; i < amountPipelines; ++i) {
            String job = "pipeline" + i;
            int randomNumber = (int) getRandomNumber(0, 4);
            List<String> branches = switch (randomNumber) {
                case 0 -> List.of("master", "dev");
                case 1 -> List.of("master");
                default -> {
                    ArrayList<String> objects = new ArrayList<>();
                    objects.add(null);
                    yield objects;
                }
            };
            OffsetDateTime pipelineEnd = getRandomDateTime();
            int randomDuration = (int) getRandomNumber(1, 50);
            OffsetDateTime pipelineStart = pipelineEnd.minusMinutes(randomDuration);
            double joules = getRandomNumber(5, 10);

            for (String branch : branches) {
                int amountBuilds = (int) getRandomNumber(10, 1200);
                for (int k = 0; k < amountBuilds; k++) {
                    OffsetDateTime actualPipelineEnd = pipelineEnd.plusSeconds((int) getRandomNumber(3, 6));
                    long actualRuntime = ChronoUnit.SECONDS.between(pipelineStart, actualPipelineEnd);
                    double wattsUsedInPipeline = joules * actualRuntime;
                    JenkinsMetadata jenkinsMetadata = new JenkinsMetadata(job, branch, (long) k,
                            pipelineStart, actualPipelineEnd);
                    EnergyReading energyReading = new EnergyReading(BigDecimal.valueOf(wattsUsedInPipeline), BigDecimal.valueOf(getRandomNumber(0, 20)));
                    readingRecords.add(new EnergyReadingRecord(energyReading, jenkinsMetadata));
                }
            }
        }
        return readingRecords;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }



    private static double getRandomNumber(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    private static OffsetDateTime getRandomDateTime() {
        Instant startInclusive = ZonedDateTime.now().minus(3, ChronoUnit.MONTHS).toInstant();
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = Instant.now().getEpochSecond();
        long random = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);

        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(random), ZoneId.systemDefault());
    }
}
