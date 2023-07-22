package com.pipelinepowertool.api.calculator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static com.pipelinepowertool.api.utils.Constants.DEFAULT_CARBON_INTENSITY;

@ApplicationScoped
public class CarbonIntensityService {

    @ConfigProperty(name = "carbon-api.baseUri")
    String baseUri;

    @ConfigProperty(name = "carbon-api.token")
    String token;

    private final ObjectMapper objectMapper;

    public CarbonIntensityService() {
        objectMapper = new ObjectMapper();
    }

    public float getCarbonIntensity(String countryCode) {
        if (countryCode == null) {
            return DEFAULT_CARBON_INTENSITY;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + "latest?countryCode=" + countryCode))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .header("auth-token", token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
            Co2SignalResponse co2SignalResponse = objectMapper.readValue(response.body(), Co2SignalResponse.class);
            return co2SignalResponse.carbonIntensityGr;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return DEFAULT_CARBON_INTENSITY;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Co2SignalResponse {

        private static final String CARBON_INTENSITY_KEY = "carbonIntensity";

        Float carbonIntensityGr;

        @JsonProperty("data")
        private void setCarbonIntensityGr(Map<String, String> data) {
            carbonIntensityGr = Float.valueOf(data.get(CARBON_INTENSITY_KEY));
        }

    }
}
