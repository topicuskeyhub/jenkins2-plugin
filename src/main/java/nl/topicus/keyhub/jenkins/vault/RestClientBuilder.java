/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.topicus.keyhub.jenkins.vault;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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