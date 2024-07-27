package com.stein.myenergi;

import com.stein.myenergi.api.dto.HistoryDay;
import com.stein.myenergi.api.dto.StatusCallOutput;
import com.stein.myenergi.api.dto.Zappi;
import com.stein.myenergi.database.HistoryRepository;
import com.stein.myenergi.database.entities.HistoryEntity;
import com.stein.myenergi.service.MyEnergiApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ScheduledTasksTest {

    @Autowired
    private ScheduledTasks scheduledTasks;

    @MockBean
    HistoryRepository historyRepository;

    @MockBean
    MyEnergiApiService apiService;

    private static final Zappi DUMMY_ZAPPI = new Zappi();

    @BeforeEach
    void setUp() {
        DUMMY_ZAPPI.setSerialNumber("dummy serialnr");
    }

    @Test
    public void test_persistHistoricZappiData_shouldAddAggregatedDataInDb() {
        when(apiService.getZappiStatus(null)).thenReturn(
                new StatusCallOutput(new Zappi[]{DUMMY_ZAPPI})
        );

        HistoryDay historyDayPart1 = new HistoryDay();
        historyDayPart1.setGeneratedJoules(5000);
        historyDayPart1.setExportedJoules(0);
        historyDayPart1.setImportedJoules(5000);
        historyDayPart1.setPhase1JoulesForCharging(10000);

        HistoryDay historyDayPart2 = new HistoryDay();
        historyDayPart2.setGeneratedJoules(4000);
        historyDayPart2.setExportedJoules(4000);
        when(apiService.getZappiHistory(eq(DUMMY_ZAPPI.getSerialNumber()), any())).thenReturn(
                new HistoryDay[]{historyDayPart1, historyDayPart2}
        );

        HistoryEntity expectedResult = new HistoryEntity();
        expectedResult.setExported(4000);
        expectedResult.setGenerated(9000);
        expectedResult.setCharged(10000);
        expectedResult.setImported(5000);
        expectedResult.setConsumed(9000 + 5000 - 4000);

        this.scheduledTasks.persistHistoricZappiData();

        verify(historyRepository, times(1)).save(argThat(r -> {
                    assertThat(r.getSerial()).isEqualTo(DUMMY_ZAPPI.getSerialNumber());
                    assertThat(r.getExported()).isEqualTo(4000);
                    assertThat(r.getCharged()).isEqualTo(10000);
                    assertThat(r.getImported()).isEqualTo(5000);
                    assertThat(r.getConsumed()).isEqualTo(9000 + 5000 - 4000);
                    return true;
                })
        );
    }
}