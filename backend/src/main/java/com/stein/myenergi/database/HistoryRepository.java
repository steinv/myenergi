package com.stein.myenergi.database;

import com.google.firebase.database.FirebaseDatabase;
import com.stein.myenergi.database.entities.HistoryEntity;

import java.util.Date;

import static com.stein.myenergi.functions.PersistZappiData.DATE_FORMAT;

public class HistoryRepository {

    private static HistoryRepository INSTANCE;

    public static HistoryRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HistoryRepository();
        }

        return INSTANCE;
    }
    
    public void save(HistoryEntity historyEntity) {
        String yyyyMMdd = DATE_FORMAT.format(new Date(historyEntity.getDate()));
        FirebaseDatabase.getInstance()
                .getReference(String.format("/history/%s/%s", historyEntity.getSerial(), yyyyMMdd))
                .setValueAsync(historyEntity);
    }
}
