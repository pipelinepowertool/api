package com.pipelinepowertool.api.calculator;

import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadataBuilder;
import com.pipelinepowertool.api.model.PowerUsageResponse;
import com.pipelinepowertool.common.core.pipeline.jenkins.JenkinsMetadata;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/calculator/jenkins")
public class CalculatorResource {

    @Inject
    CalculatorService calculatorService;

    @GET
    @Produces({"application/json"})
    public Uni<PowerUsageResponse> getUsage(
            @RestQuery("country") String country,
            @RestQuery("job") String job,
            @RestQuery("branch") String branch,
            @RestQuery("build") Long build) {
        JenkinsMetadata jenkinsMetadata = new JenkinsMetadataBuilder()
                .withJob(job)
                .withBranch(branch)
                .withBuildNumber(build).build();
        return calculatorService.getPowerUsage(jenkinsMetadata, country);
    }

}
