package com.stein.myenergi.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.stein.myenergi.api.dto.Zappi;
import com.stein.myenergi.service.MyEnergiApiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ZappiStatus implements HttpFunction {

    private final MyEnergiApiService apiService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ZappiStatus(MyEnergiApiService apiService, ObjectMapper objectMapper) {
        this.apiService = apiService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String serial = request.getFirstQueryParameter("serial").orElse(null);
        Zappi[] zappi = this.apiService.getZappiStatus(serial).getZappi();

        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(objectMapper.writeValueAsString(zappi));
    }
}