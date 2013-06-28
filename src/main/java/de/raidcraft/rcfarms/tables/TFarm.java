package de.raidcraft.rcfarms.tables;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_farms")
public class TFarm {

    @Id
    private int id;
    private String name;
    private String material;
    private Timestamp lastRegeneration;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TFarmLocation> keyPoints;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TFarmTenant> tenants;

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

    public String getMaterial() {

        return material;
    }

    public void setMaterial(String material) {

        this.material = material;
    }

    public Timestamp getLastRegeneration() {

        return lastRegeneration;
    }

    public void setLastRegeneration(Timestamp lastRegeneration) {

        this.lastRegeneration = lastRegeneration;
    }

    public Set<TFarmLocation> getKeyPoints() {

        return keyPoints;
    }

    public void setKeyPoints(Set<TFarmLocation> keyPoints) {

        this.keyPoints = keyPoints;
    }

    public Set<TFarmTenant> getTenants() {

        return tenants;
    }

    public void setTenants(Set<TFarmTenant> tenants) {

        this.tenants = tenants;
    }
}
