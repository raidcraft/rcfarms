package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import de.raidcraft.rcfarms.tables.TMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "EDIT_FARM")
public class EditFarmAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm((String) conversation.get("farm_id"));
        if(tFarm == null) {
            conversation.getPlayer().sendMessage(ChatColor.RED + "Keine Farm selektiert! Fehler bitte melden!");
            return;
        }

        // update name
        if(conversation.get("farm_name") != null) {
            tFarm.setName((String)conversation.get("farm_name"));
        }

        // collect new material
        ConfigurationSection materialSection = conversation.getConfigurationSection("material");
        if(materialSection == null) {
            printError(conversation, "Der Farm ist kein Material zugewiesen!");
            return;
        }
        Set<Material> materials = new HashSet<>();
        for(String materialName : materialSection.getKeys(false)) {
            if(materialSection.get(materialName) == null) continue;
            Material material = Material.valueOf(materialName);
            if(material != null) {
                materials.add(material);
            }
        }
        if(materials.isEmpty()) {
            printError(conversation, "Der Farm ist kein gültiges Material zugewiesen!");
            return;
        }

        // delete old material
        for(TMaterial tMaterial : tFarm.getMaterials()) {
            RaidCraft.getDatabase(RCFarmsPlugin.class).delete(tMaterial);
        }
        tFarm.setMaterials(null);

        // save new
        for(Material material : materials) {
            TMaterial tMaterial = new TMaterial(material.name(), tFarm);
            RaidCraft.getDatabase(RCFarmsPlugin.class).save(tMaterial);
            tFarm.addMaterial(tMaterial);
        }

        RaidCraft.getDatabase(RCFarmsPlugin.class).update(tFarm);
        conversation.getPlayer().sendMessage(ChatColor.GREEN + "Die Farm wurde erfolgreich aktualisiert!");
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.RED + "Das aktualisieren der Farm wurde abgebrochen!");
    }
}
