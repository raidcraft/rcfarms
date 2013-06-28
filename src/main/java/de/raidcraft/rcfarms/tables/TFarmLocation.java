package de.raidcraft.rcfarms.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_locations")
public class TFarmLocation {

    @Id
    private int id;
    @ManyToOne
    private int farmId;
    private String world;
    private int x;
    private int y;
    private int z;
}
