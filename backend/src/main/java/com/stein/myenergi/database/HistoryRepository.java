package com.stein.myenergi.database;

import com.google.firebase.database.FirebaseDatabase;
import com.stein.myenergi.database.entities.HistoryEntity;
import org.springframework.stereotype.Service;

@Service
public class HistoryRepository {

    public void save(HistoryEntity historyEntity) {
        FirebaseDatabase.getInstance()
                .getReference(String.format("/history/%s/", historyEntity.getSerial()))
                .push()
                .setValueAsync(historyEntity);
    }
}
