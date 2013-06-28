package de.raidcraft.rcfarms.tables;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_tenants")
public class TFarmTenant {

    @Id
    private int id;
    @ManyToOne
    private int farmId;
    private int upgradeId;
    private String owner;
    private Timestamp buyDate;

}
