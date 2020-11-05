package io.jenkins.plugins.vault;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class RestClientBuilder {

    private ResteasyClient client;

    private ObjectMapper mapper;

    public RestClientBuilder() {
        Thread t = Thread.currentThread();
        ClassLoader orig = t.getContextClassLoader();
        t.setContextClassLoader(RestClientBuilder.class.getClassLoader());
        try {
            mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.setTimeZone(TimeZone.getDefault());

            ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
            builder.register(new JacksonJsonProvider(mapper));
            builder.register(JacksonJaxbJsonProvider.class);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(1, TimeUnit.MINUTES);
            client = builder.build();
        } finally {
            t.setContextClassLoader(orig);
        }
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ResteasyClient getClient() {
        return client;
    }

}