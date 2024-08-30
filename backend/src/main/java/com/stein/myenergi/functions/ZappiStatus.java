package com.stein.myenergi.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.firebase.FirebaseApp;
import com.stein.myenergi.MyEnergiConfiguration;
import com.stein.myenergi.api.dto.Zappi;
import com.stein.myenergi.service.MyEnergiApiService;

import java.io.IOException;

public class ZappiStatus implements HttpFunction {

    static {
        FirebaseApp.initializeApp();
    }

    private final MyEnergiApiService apiService = MyEnergiApiService.getInstance();
    private final ObjectMapper objectMapper = MyEnergiConfiguration.getObjectMapper();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String serial = request.getFirstQueryParameter("serial").orElse(null);
        Zappi[] zappi = this.apiService.getZappiStatus(serial).getZappi();

        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(objectMapper.writeValueAsString(zappi));
    }
}