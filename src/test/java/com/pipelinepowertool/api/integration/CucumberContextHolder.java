package com.pipelinepowertool.api.integration;

import io.restassured.response.Response;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CucumberContextHolder {

    Response response;

    String request;


}
