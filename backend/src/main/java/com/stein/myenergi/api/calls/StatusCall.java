package com.stein.myenergi.api.calls;

import com.stein.myenergi.api.dto.StatusCallInput;
import com.stein.myenergi.api.dto.StatusCallOutput;

public class StatusCall extends AbstractMyEnergiCall<StatusCallInput, StatusCallOutput> {

    public static final String COMMAND = "/cgi-jstatus-%s";

    private static StatusCall INSTANCE;

    public static StatusCall getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatusCall();
        }

        return INSTANCE;
    }

    private StatusCall() {
        super(StatusCallOutput.class);
    }

    @Override
    String getCommand(StatusCallInput input) {
        String cmd = String.format(COMMAND, input.getDeviceType().toString());

        if (null != input.getSerial() && !input.getSerial().isBlank()) {
            return cmd + input.getSerial();
        }

        return cmd;
    }
}
