package de.raidcraft.rcfarms.tables;

import com.sk89q.worldedit.Vector;
import de.raidcraft.rcfarms.api.location.FarmLocation;
import org.bukkit.Bukkit;
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
public class TFarmLocation implements FarmLocation {

    @Id
    private int id;
    @ManyToOne
    private TFarm farm;
    private String world;
    private int x;
    private int y;
    private int z;

    public TFarmLocation() {
        // required
    }

    public TFarmLocation(Location location, TFarm farm) {

        this.farm = farm;
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

    public TFarm getFarm() {

        return farm;
    }

    public void setFarm(TFarm farm) {

        this.farm = farm;
    }

    @Override
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

    public Location getLocation() {

        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public Vector getSk89qVector() {

        return new Vector(x, y, z);
    }
}
