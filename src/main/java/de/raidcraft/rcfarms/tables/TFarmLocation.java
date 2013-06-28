package de.raidcraft.rcfarms.tables;

import org.bukkit.Location;

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

    public TFarmLocation(Location location, int farmId) {

        this.farmId = farmId;
        this.world = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getFarmId() {

        return farmId;
    }

    public void setFarmId(int farmId) {

        this.farmId = farmId;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getZ() {

        return z;
    }

    public void setZ(int z) {

        this.z = z;
    }
}
