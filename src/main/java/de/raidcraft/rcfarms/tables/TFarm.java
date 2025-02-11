package de.raidcraft.rcfarms.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
@Entity
@Table(name = "rcfarms_farms")
public class TFarm {

    @Id
    private int id;
    private String name;
    private Timestamp creationDate;
    private UUID creatorId;
    private String creator;
    private String world;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TMaterial> materials;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "farm_id")
    private Set<TFarmLocation> keyPoints;
    private Timestamp lastRegeneration;
    private boolean allMaterials;
    // in seconds
    private long explicitRegenerationInterval;


    public void addMaterial(TMaterial tMaterial) {
        if (materials == null) {
            materials = new HashSet<>();
        }
        materials.add(tMaterial);
    }

    public TFarmLocation[] getKeyPointArray() {

        return keyPoints.toArray(new TFarmLocation[keyPoints.size()]);
    }

    public void addKeyPoint(TFarmLocation keyPoint) {

        if (keyPoints == null) {
            keyPoints = new HashSet<>();
        }
        keyPoints.add(keyPoint);
    }

    public void loadChildren() {

        materials = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TMaterial.class).where().eq("farm_id", id).findSet();
        keyPoints = RaidCraft.getDatabase(RCFarmsPlugin.class).find(TFarmLocation.class).where().eq("farm_id", id).findSet();
    }

    public World getBukkitWorld() {

        TFarmLocation[] keyPoints = getKeyPointArray();
        return Bukkit.getWorld(keyPoints[0].getWorld());
    }

    @Deprecated
    public String getCreator() {

        return creator;
    }

    @Deprecated
    public void setCreator(String creator) {

        this.creator = creator;
    }

    public String getWorld() {
        return world;
    }

    public Timestamp getCreationTime() {
        return creationDate;
    }

    public String getCreatorName() {
        return creator;
    }

    public String getWorldName() {
        return world;
    }

    public Timestamp getLastRegenerationTime() {
        return lastRegeneration;
    }

    public boolean isAllMaterialFarm() {
        return allMaterials;
    }

    public boolean isPlayerInside() {
        // TODO: finish it
        // TODO check take to much time
        //        TFarmLocation[] kp = getKeyPointArray();
        //
        //        for(Player player : Bukkit.getOnlinePlayers()) {
        //            Location loc = player.getLocation();
        //            if(loc.getBlockX() >= Math.min(kp[0].getX(), kp[1].getX()) && loc.getBlockX() <= Math.max(kp[0].getX(), kp[1].getX())
        //                    && loc.getBlockY() >= Math.min(kp[0].getY(), kp[1].getY()) && loc.getBlockY() <= Math.max(kp[0].getY(), kp[1].getY())
        //                    && loc.getBlockZ() >= Math.min(kp[0].getZ(), kp[1].getZ()) && loc.getBlockZ() <= Math.max(kp[0].getZ(), kp[1].getZ())) {
        //                return true;
        //            }
        //        }
        return false;
    }
}
