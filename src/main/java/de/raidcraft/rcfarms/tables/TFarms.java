package de.raidcraft.rcfarms.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_farms")
public class TFarms {

    @Id
    private int id;
    private int upgradeId;
    private String owner;
    private Timestamp buyDate;
}
