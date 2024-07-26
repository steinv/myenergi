package com.stein.myenergi;

import com.stein.myenergi.transformers.HistoryModelMapper;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.DigestScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.modelmapper.config.Configuration.AccessLevel.PACKAGE_PRIVATE;

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
        // old hostUri before cloud migration uses the hubserial's last digit
        // final String hostUri = String.format("https://s%s.myenergi.net/", hubSerial.substring(hubSerial.length() - 1));
        final String hostUri = "https://s18.myenergi.net/";
        HttpHost host = new HttpHost(hostUri);
        CloseableHttpClient client =
                HttpClients.custom()
                        .setDefaultCredentialsProvider(provider())
                        .useSystemProperties()
                        .build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        return restTemplateBuilder
                .requestFactory(() -> {
                    requestFactory.setHttpContextFactory(((httpMethod, uri) -> {
                        AuthCache authCache = new BasicAuthCache();
                        DigestScheme digestAuth = new DigestScheme();
                        authCache.put(host, digestAuth);
                        BasicHttpContext localcontext = new BasicHttpContext();
                        localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
                        return localcontext;
                    }));
                    return requestFactory;
                })
                .rootUri(hostUri)
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofMinutes(3)) // looks like we get a 524 after 100 secs
                .build();
    }

    private CredentialsProvider provider() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(hubSerial, password.toCharArray());
        provider.setCredentials(new AuthScope(null, -1), credentials);
        return provider;
    }
}
