package com.pipelinepowertool.api.configuration;

import com.pipelinepowertool.common.core.database.elasticsearch.ElasticSearchService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.apache.http.HttpHost;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class ElasticSearchServiceConfiguration {

    private static final Logger LOG = Logger.getLogger(ElasticSearchServiceConfiguration.class);

    @ConfigProperty(name = "api.elasticsearch.username")
    String username;
    @ConfigProperty(name = "api.elasticsearch.password")
    String password;
    @ConfigProperty(name = "api.elasticsearch.host")
    String host;
    @ConfigProperty(name = "api.elasticsearch.port")
    int port;
    @ConfigProperty(name = "api.elasticsearch.httpScheme")
    String httpScheme;
    @ConfigProperty(name = "api.elasticsearch.certificate")
    String certificate;


    @Produces
    @ApplicationScoped
    public ElasticSearchService elasticSearchService() {
        return new ElasticSearchService(username, password, getCertificate(certificate), new HttpHost(host, port, httpScheme));
    }

    private static byte[] getCertificate(String certificate) {
        try (InputStream resource = ElasticSearchServiceConfiguration.class.getClassLoader().getResourceAsStream(certificate)) {
            if (resource == null) {
                LOG.error("Cannot find certificate");
                throw new RuntimeException();
            }
            return resource.readAllBytes();
        } catch (IOException e) {
            LOG.error("Cannot read certificate");
            throw new RuntimeException(e);
        }
    }
}
