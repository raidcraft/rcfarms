package de.raidcraft.rcfarms.tables;

import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
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

    public Set<TMaterial> getMaterials() {

        return materials;
    }

    public void setMaterials(Set<TMaterial> materials) {

        this.materials = materials;
    }

    public void addMaterial(TMaterial tMaterial) {

        if(materials == null) {
            materials = new HashSet<>();
        }
        materials.add(tMaterial);
    }

    public Set<TFarmLocation> getKeyPoints() {

        return keyPoints;
    }

    public void setKeyPoints(Set<TFarmLocation> keyPoints) {

        this.keyPoints = keyPoints;
    }

    public void addKeyPoint(TFarmLocation keyPoint) {

        if(keyPoints == null) {
            keyPoints = new HashSet<>();
        }
        keyPoints.add(keyPoint);
    }

    public Timestamp getLastRegeneration() {

        return lastRegeneration;
    }

    public void setLastRegeneration(Timestamp lastRegeneration) {

        this.lastRegeneration = lastRegeneration;
    }

    public World getBukkitWorld() {

        TFarmLocation[] keyPoints = getKeyPoints().toArray(new TFarmLocation[getKeyPoints().size()]);
        return Bukkit.getWorld(keyPoints[0].getWorld());
    }

    public boolean isPlayerInside() {

        // TODO check take to much time
//        TFarmLocation[] kp = getKeyPoints().toArray(new TFarmLocation[getKeyPoints().size()]);
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
