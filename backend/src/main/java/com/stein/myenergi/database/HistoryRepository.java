package com.stein.myenergi.database;

import com.google.firebase.database.FirebaseDatabase;
import com.stein.myenergi.database.entities.HistoryEntity;

public class HistoryRepository {

    private static HistoryRepository INSTANCE;

    public static HistoryRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HistoryRepository();
        }

        return INSTANCE;
    }
    
    public void save(HistoryEntity historyEntity) {
        FirebaseDatabase.getInstance()
                .getReference(String.format("/history/%s/", historyEntity.getSerial()))
                .push()
                .setValueAsync(historyEntity);
    }
}
