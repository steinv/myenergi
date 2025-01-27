package com.stein.myenergi.functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.stein.myenergi.service.MyEnergiService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.hc.core5.http.HttpStatus;

// TODO https://www.baeldung.com/spring-cloud-function
public class PersistZappiData implements HttpFunction {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final MyEnergiService service = MyEnergiService.getInstance();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws ParseException {
        String serial = request.getFirstQueryParameter("serial").orElseThrow();
        Optional<String> dateString = request.getFirstQueryParameter("date");
        if(dateString.isPresent()) {
            Date givenDate = DATE_FORMAT.parse(dateString.get());
            this.service.persistZappiData(serial, givenDate);
        } else {
            // persist yesterdays data when no date(s) are specified
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            this.service.persistZappiData(serial, yesterday.getTime());
        }

        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.setStatusCode(HttpStatus.SC_NO_CONTENT);
    }
}
