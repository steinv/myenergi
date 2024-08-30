package com.stein.myenergi.api.calls;

import com.stein.myenergi.MyEnergiConfiguration;
import com.stein.myenergi.api.dto.MyenergiCallInput;
import com.stein.myenergi.api.dto.MyenergiCallOutput;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

public abstract class AbstractMyEnergiCall<I extends MyenergiCallInput, O extends MyenergiCallOutput> {

    private final Class<O> outputClass;

    public AbstractMyEnergiCall(Class<O> outputClass) {
        this.outputClass = outputClass;
    }

    // @Retryable(maxAttempts = 5, value = RestClientException.class)
    public O fire(I input) {
        try (CloseableHttpClient httpclient = MyEnergiConfiguration.getHttpClient()) {
            String url = MyEnergiConfiguration.myenergiUrl + getCommand(input);
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
            return httpclient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final HttpEntity entity = response.getEntity();
                final String responseString = EntityUtils.toString(entity);
                return MyEnergiConfiguration.getObjectMapper().readValue(responseString, outputClass);
            });
        } catch (IOException e) {
            e.printStackTrace(); // TODO retry?
            throw new RuntimeException("Failed to call myEnergi service", e);
        }
    }

    abstract String getCommand(I input);
}
