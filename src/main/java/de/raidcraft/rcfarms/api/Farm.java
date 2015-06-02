package de.raidcraft.rcfarms.api;


import de.raidcraft.rcfarms.api.location.FarmLocation;
import de.raidcraft.rcfarms.api.material.FarmMaterial;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Philip on 31.05.2015.
 */
public interface Farm {

    public String getName();

    public Timestamp getCreationTime();

    public UUID getCreatorId();

    public String getCreatorName();

    public String getWorldName();

    public Set<FarmMaterial> getMaterials();

    public Set<FarmLocation> getKeyPoints();

    public Timestamp getLastRegenerationTime();

    public boolean isAllMaterialFarm();
}
