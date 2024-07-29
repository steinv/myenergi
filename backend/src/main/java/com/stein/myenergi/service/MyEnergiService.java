package com.stein.myenergi.service;

import java.util.Date;

import org.modelmapper.ModelMapper;

import com.stein.myenergi.MyEnergiConfiguration;
import com.stein.myenergi.api.dto.HistoryDay;
import com.stein.myenergi.database.HistoryRepository;
import com.stein.myenergi.database.entities.HistoryEntity;

public class MyEnergiService {

    private final ModelMapper modelMapper;
    private final MyEnergiApiService apiService;
    private final HistoryRepository historyRepository;

    private static MyEnergiService INSTANCE;

    public static MyEnergiService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyEnergiService();
        }

        return INSTANCE;
    }
    
    private MyEnergiService() {
        this.modelMapper = MyEnergiConfiguration.getModelMapper();
        this.apiService = MyEnergiApiService.getInstance();
        this.historyRepository = HistoryRepository.getInstance();
    }

    /**
     * Persist Historical data for a zappi serial on a certain point in time;
     * 
     * @param zappiSerial
     * @param date
     */
    public void persistZappiData(String zappiSerial, Date date) {
        HistoryDay[] zappiHistory = this.apiService.getZappiHistory(zappiSerial, date);
        HistoryEntity entity = modelMapper.map(zappiHistory, HistoryEntity.class);
        entity.setDate(date.getTime());
        entity.setSerial(zappiSerial);
        this.historyRepository.save(entity);
    }
}
