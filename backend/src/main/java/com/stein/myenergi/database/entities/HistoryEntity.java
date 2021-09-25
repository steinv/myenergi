package com.stein.myenergi.database.entities;

import com.stein.myenergi.api.dto.HistoryDay;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "history")
@Data
public class HistoryEntity implements Serializable {

    @EmbeddedId
    private HistoryId id;

    // electricity in joules imported from the net
    private int imported;
    // electricity in joules exported to the net
    private int exported;

    // electricity in joules generated by solar panels
    private int generated;

    // electricity in joules charged into electrical vehicle
    private int charged;

    // electricity in joules consumed
    private int consumed;

    // original data from api
    private HistoryDay[] history;
}
