package com.stein.myenergi;

import static org.modelmapper.config.Configuration.AccessLevel.PACKAGE_PRIVATE;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.stein.myenergi.transformers.HistoryModelMapper;

@Configuration
public class MyEnergiConfiguration {

    @Value("${myenergi.hub.serial}")
    private String hubSerial;

    @Value("${myenergi.password}")
    private String password;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PACKAGE_PRIVATE);
        mapper.addConverter(new HistoryModelMapper());
        return mapper;
    }

    @Bean
    public RestTemplate myEnergiRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        // Connect timeout
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))
                .build();

        // Socket timeout
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(30))
                .build();

        // Connection request timeout
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMinutes(3))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(provider())
                .useSystemProperties()
                .build();

        // old hostUri before cloud migration uses the hubserial's last digit
        // final String hostUri = String.format("https://s%s.myenergi.net/",
        // hubSerial.substring(hubSerial.length() - 1));
        return restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
                .rootUri("https://s18.myenergi.net/")
                .build();
    }

    private CredentialsProvider provider() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(hubSerial, password.toCharArray());
        provider.setCredentials(new AuthScope(null, -1), credentials);
        return provider;
    }
}
