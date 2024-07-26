package com.stein.myenergi.transformers;

import com.stein.myenergi.api.dto.HistoryDay;
import com.stein.myenergi.database.entities.HistoryEntity;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Arrays;

/**
 * Converts an array of HistoryDay[] received from an API call to a HistoryEntity for the database
 * The database entity contains the aggregated result of the day + the original api result.
 */
public class HistoryModelMapper implements Converter<HistoryDay[], HistoryEntity> {

    @Override
    public HistoryEntity convert(MappingContext<HistoryDay[], HistoryEntity> mappingContext) {
        HistoryDay[] source = mappingContext.getSource();
        HistoryEntity destination = new HistoryEntity();

        // temp array to aggregate sum results
        int[] temp = new int[]{0, 0 ,0, 0}; // index based, 0 = generated, 1 = exported, 2 = imported , 3 = charged
        Arrays.stream(source).forEach(r -> {
                    temp[0] += r.getGeneratedJoules();
                    temp[1] += r.getExportedJoules();
                    temp[2] += r.getImportedJoules();
                    temp[3] += r.getPhase1JoulesForCharging() + r.getPhase2JoulesForCharging() + r.getPhase3JoulesForCharging();
        });
        destination.setGenerated(temp[0]);
        destination.setExported(temp[1]);
        destination.setImported(temp[2]);
        destination.setCharged(temp[3]);

        // consumed = generated + imported - exported
        destination.setConsumed(temp[0] + temp[2] - temp[1]);
        // keep the original data too in the entity, not just the aggregation
        destination.setHistory(source);

        return destination;
    }
}
