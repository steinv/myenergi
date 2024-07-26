package com.stein.myenergi.service;

import com.stein.myenergi.api.dto.HistoryDay;
import com.stein.myenergi.database.HistoryRepository;
import com.stein.myenergi.database.entities.HistoryEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Service
public class MyEnergiService {

    private final ModelMapper modelMapper;
    private final MyEnergiApiService apiService;
    private final HistoryRepository historyRepository;

    public MyEnergiService(ModelMapper modelMapper, MyEnergiApiService apiService, HistoryRepository historyRepository) {
        this.modelMapper = modelMapper;
        this.apiService = apiService;
        this.historyRepository = historyRepository;
    }

    /**
     * Persist Historical data for a zappi serial on a certain point in time;
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
