package de.raidcraft.rcfarms.tables;

import de.raidcraft.rcfarms.api.material.FarmMaterial;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcfarms_materials")
public class TMaterial implements FarmMaterial {

    @Id
    private int id;
    @ManyToOne
    private TFarm farm;
    private String name;

    public TMaterial() {
        // required
    }

    public TMaterial(String name, TFarm farm) {

        this.name = name;
        this.farm = farm;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    @Override
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public TFarm getFarm() {

        return farm;
    }

    public void setFarm(TFarm farm) {

        this.farm = farm;
    }

    @Override
    public Material getBukkitMaterial() {

        return ItemUtils.getItem(name);
    }
}
