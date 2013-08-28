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
    private Timestamp creationDate;
    private String creator;
    private String welcomeMessage;
    private String farewellMessage;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TMaterial> materials;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TFarmLocation> keyPoints;
    private Timestamp lastRegeneration;

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

    public Timestamp getCreationDate() {

        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {

        this.creationDate = creationDate;
    }

    public String getCreator() {

        return creator;
    }

    public void setCreator(String creator) {

        this.creator = creator;
    }

    public String getWelcomeMessage() {

        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {

        this.welcomeMessage = welcomeMessage;
    }

    public String getFarewellMessage() {

        return farewellMessage;
    }

    public void setFarewellMessage(String farewellMessage) {

        this.farewellMessage = farewellMessage;
    }

    public Set<TMaterial> getMaterials() {

        return materials;
    }

    public void setMaterials(Set<TMaterial> materials) {

        this.materials = materials;
    }

    public Set<TFarmLocation> getKeyPoints() {

        return keyPoints;
    }

    public void setKeyPoints(Set<TFarmLocation> keyPoints) {

        this.keyPoints = keyPoints;
    }

    public Timestamp getLastRegeneration() {

        return lastRegeneration;
    }

    public void setLastRegeneration(Timestamp lastRegeneration) {

        this.lastRegeneration = lastRegeneration;
    }
}
