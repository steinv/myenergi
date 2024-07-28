package com.stein.myenergi;

import com.stein.myenergi.transformers.HistoryModelMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
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
                // final String hostUri = String.format("https://s%s.myenergi.net/",
                // hubSerial.substring(hubSerial.length() - 1));
                final String hostUri = "https://s18.myenergi.net/";
                HttpHost host = new HttpHost(hostUri);
                CloseableHttpClient client = HttpClientBuilder
                                .create()
                                .setDefaultCredentialsProvider(provider())
                                .useSystemProperties()
                                .build();

                final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                                client);
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
                CredentialsProvider provider = new BasicCredentialsProvider();
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(hubSerial, password);
                provider.setCredentials(AuthScope.ANY, credentials);
                return provider;
        }

        /**
         * uncomment below when upgrading
         * spring boot 3.3.x + apache 5 + java 17
         */
        // @Bean
        // public RestTemplate myEnergiRestTemplate(RestTemplateBuilder
        // restTemplateBuilder) {
        // // old hostUri before cloud migration uses the hubserial's last digit
        // // final String hostUri = String.format("https://s%s.myenergi.net/",
        // // hubSerial.substring(hubSerial.length() - 1));
        // return restTemplateBuilder
        // .requestFactory(this::httpRequestFactory)
        // .rootUri("https://s18.myenergi.net/")
        // .build();
        // }

        // private HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        // // Connect timeout
        // ConnectionConfig connectionConfig = ConnectionConfig.custom()
        // .setConnectTimeout(Timeout.ofSeconds(30))
        // .build();

        // // Socket timeout
        // SocketConfig socketConfig = SocketConfig.custom()
        // .setSoTimeout(Timeout.ofSeconds(30))
        // .build();

        // // Connection request timeout
        // RequestConfig requestConfig = RequestConfig.custom()
        // .setConnectionRequestTimeout(Timeout.ofMinutes(3))
        // .build();

        // PoolingHttpClientConnectionManager connectionManager = new
        // PoolingHttpClientConnectionManager();
        // connectionManager.setDefaultSocketConfig(socketConfig);
        // connectionManager.setDefaultConnectionConfig(connectionConfig);

        // CloseableHttpClient httpClient = HttpClientBuilder.create()
        // .setConnectionManager(connectionManager)
        // .setDefaultRequestConfig(requestConfig)
        // .setDefaultCredentialsProvider(provider())
        // .useSystemProperties()
        // .build();

        // return new HttpComponentsClientHttpRequestFactory(httpClient);
        // }

        // private CredentialsProvider provider() {
        // BasicCredentialsProvider provider = new BasicCredentialsProvider();
        // UsernamePasswordCredentials credentials = new
        // UsernamePasswordCredentials(hubSerial, password.toCharArray());
        // provider.setCredentials(new AuthScope(null, -1), credentials);
        // return provider;
        // }
}
