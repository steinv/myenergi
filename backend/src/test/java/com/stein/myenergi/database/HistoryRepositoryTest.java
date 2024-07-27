package com.stein.myenergi.database;

import com.stein.myenergi.database.entities.HistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;

@SpringBootTest
@ActiveProfiles("test")
class HistoryRepositoryTest {
    @Autowired
    private HistoryRepository repo;

    private Calendar calendar;
    private static final String DUMMY_SERIAL = "12345678";
    private static final HistoryEntity HISTORY_ENTITY = new HistoryEntity();

    @BeforeEach
    public void setUp() {
        calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.SEPTEMBER, 23);

        HISTORY_ENTITY.setDate(calendar.getTime().getTime());
        HISTORY_ENTITY.setSerial(DUMMY_SERIAL);
        HISTORY_ENTITY.setCharged(1000);
        HISTORY_ENTITY.setImported(6000);
        HISTORY_ENTITY.setExported(3000);
    }

    @Test
    public void test_insertHistoryEntity_shouldAddEntityToDb() {
        repo.save(HISTORY_ENTITY);
    }
}
