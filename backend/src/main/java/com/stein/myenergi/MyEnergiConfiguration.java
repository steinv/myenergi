package com.stein.myenergi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.stein.myenergi.transformers.HistoryModelMapper;
import org.apache.hc.client5.http.auth.AuthScope;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.modelmapper.config.Configuration.AccessLevel.PACKAGE_PRIVATE;

public class MyEnergiConfiguration {

    private static final String hubSerial = System.getenv("MYENERGI_HUB_SERIAL");
    private static final String password = System.getenv("MYENERGI_PASSWORD");
    private static final String database = System.getenv("FIREBASE_DATABASE");
    private static final String firebaseAdminSdk = System.getenv("FIREBASE_ADMIN_SDK");

    // old url before cloud migration uses the hubserial's last digit
    // final String myenergiUrl = String.format("https://s%s.myenergi.net", hubSerial.substring(hubSerial.length() - 1));
    public static final String myenergiUrl = "https://s18.myenergi.net";

    private static ModelMapper modelMapper;
    private static ObjectMapper objectMapper;

    static {
        try (InputStream serviceAccount = MyEnergiConfiguration.class.getResourceAsStream(firebaseAdminSdk)) {
            FirebaseApp.initializeApp(FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(Objects.requireNonNull(serviceAccount)))
                    .setDatabaseUrl(database)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModelMapper getModelMapper() {
        if (modelMapper != null) {
            return modelMapper;
        } else {
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration()
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(PACKAGE_PRIVATE);
            mapper.addConverter(new HistoryModelMapper());
            MyEnergiConfiguration.modelMapper = mapper;
            return modelMapper;
        }
    }

    public static ObjectMapper getObjectMapper() {
        if (MyEnergiConfiguration.objectMapper != null) {
            return MyEnergiConfiguration.objectMapper;
        } else {
            MyEnergiConfiguration.objectMapper = new ObjectMapper();
            return MyEnergiConfiguration.objectMapper;
        }
    }

    public static CloseableHttpClient getHttpClient() {
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

        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(hubSerial,
                password.toCharArray());
        provider.setCredentials(new AuthScope(null, -1), credentials);

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(provider)
                .useSystemProperties()
                .build();
    }
}
