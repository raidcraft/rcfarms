package de.raidcraft.rcfarms.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_materials")
public class TMaterial {

    @Id
    private int id;
    private String name;
    @ManyToOne
    private int farmId;

    public TMaterial(String name, int farmId) {

        this.name = name;
        this.farmId = farmId;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getFarmId() {

        return farmId;
    }

    public void setFarmId(int farmId) {

        this.farmId = farmId;
    }
}
