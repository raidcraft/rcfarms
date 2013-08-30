package de.raidcraft.rcfarms;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TFarmLocation;
import de.raidcraft.rcfarms.tables.TMaterial;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class FarmBuilder {

    private String name;
    private Timestamp creationDate;
    private String creator;
    private String welcomeMessage = "";
    private String farewellMessage = "";
    private Set<Material> materials = new HashSet<>();
    private Location minimumPoint;
    private Location maximumPoint;

    public void createFarm() throws RaidCraftException {

        RCFarmsPlugin plugin = RaidCraft.getComponent(RCFarmsPlugin.class);

        // check name
        if(RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarm.class).where().eq("name", name).findUnique() != null) {
            throw new RaidCraftException("Es gibt bereits eine Farm mit diesem Namen!");
        }

        // check area
        if(minimumPoint == null || maximumPoint == null) {
            throw new RaidCraftException("Eckpunkte ungültig!");
        }

        // check material
        if(materials.isEmpty()) {
            throw new RaidCraftException("Keine Materialien zugewiesen!");
        }

        // save farm
        TFarm tFarm = new TFarm();
        tFarm.setName(name);
        tFarm.setCreationDate(new Timestamp(System.currentTimeMillis()));
        tFarm.setLastRegeneration(new Timestamp(System.currentTimeMillis()));
        tFarm.setCreator(creator);
        tFarm.setWelcomeMessage(welcomeMessage);
        tFarm.setFarewellMessage(farewellMessage);
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarm);

        // save locations
        TFarmLocation tFarmLocationMinimum = new TFarmLocation(minimumPoint, tFarm);
        tFarm.addKeyPoint(tFarmLocationMinimum); // required to work with existing tFarm!!!
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarmLocationMinimum);
        TFarmLocation tFarmLocationMaximum = new TFarmLocation(maximumPoint, tFarm);
        tFarm.addKeyPoint(tFarmLocationMaximum); // required to work with existing tFarm!!!
        RaidCraft.getDatabase(RCFarmsPlugin.class).save(tFarmLocationMaximum);

        // save materials
        for(Material material : materials) {
            TMaterial tMaterial = new TMaterial(material.name(), tFarm);
            RaidCraft.getDatabase(RCFarmsPlugin.class).save(tMaterial);
        }

        RaidCraft.LOGGER.info("D0");
        // create schematic
        plugin.getSchematicManager().createSchematic(tFarm, 0);
        RaidCraft.LOGGER.info("D1");
        // generate region
        plugin.getFarmManager().generateRegions(minimumPoint.getWorld());
        RaidCraft.LOGGER.info("D2");
        // create dynmap marker
        plugin.getDynmapManager().addFarmMarker(tFarm);
    }

    public FarmBuilder setName(String name) {

        this.name = name;
        return this;
    }

    public FarmBuilder setCreator(String creator) {

        this.creator = creator;
        return this;
    }

    public FarmBuilder setWelcomeMessage(String welcomeMessage) {

        this.welcomeMessage = welcomeMessage;
        return this;
    }

    public FarmBuilder setFarewellMessage(String farewellMessage) {

        this.farewellMessage = farewellMessage;
        return this;
    }

    public FarmBuilder addMaterial(Material material) {

        this.materials.add(material);
        return this;
    }

    public FarmBuilder addMaterials(Collection<Material> materials) {

        this.materials.addAll(materials);
        return this;
    }

    public FarmBuilder setMinimumPoint(Location minimumPoint) {

        this.minimumPoint = minimumPoint;
        return this;
    }

    public FarmBuilder setMaximumPoint(Location maximumPoint) {

        this.maximumPoint = maximumPoint;
        return this;
    }
}
