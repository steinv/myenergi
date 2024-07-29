package com.stein.myenergi.service;

import java.util.Date;

import com.stein.myenergi.DeviceType;
import com.stein.myenergi.api.calls.DayCall;
import com.stein.myenergi.api.calls.StatusCall;
import com.stein.myenergi.api.dto.DayCallInput;
import com.stein.myenergi.api.dto.HistoryDay;
import com.stein.myenergi.api.dto.StatusCallInput;
import com.stein.myenergi.api.dto.StatusCallOutput;

public class MyEnergiApiService {

    private static MyEnergiApiService INSTANCE;

    public static MyEnergiApiService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyEnergiApiService();
        }

        return INSTANCE;
    }

    private final DayCall dayCall;
    private final StatusCall statusCall;

    private MyEnergiApiService() {
        this.dayCall = DayCall.getInstance();
        this.statusCall = StatusCall.getInstance();
    }

    public StatusCallOutput getZappiStatus(String serial) {
        return statusCall.fire(new StatusCallInput(DeviceType.ZAPPI, serial));
    }

    public HistoryDay[] getZappiHistory(String serial, Date date) {
        return dayCall.fire(new DayCallInput(DeviceType.ZAPPI, serial, date)).getHistoryDay();
    }
}
