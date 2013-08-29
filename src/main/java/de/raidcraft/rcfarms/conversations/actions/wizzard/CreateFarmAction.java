package de.raidcraft.rcfarms.conversations.actions.wizzard;

import com.sk89q.worldedit.bukkit.selections.Selection;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.FarmBuilder;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "CREATE_FARM")
public class CreateFarmAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String farmName = conversation.getString("farm_name");
        String farmWelcome = conversation.getString("farm_welcome");
        String farmFarewell = conversation.getString("farm_farewell");

        if(farmName == null || farmName.isEmpty()) {
            printError(conversation, "Der angegebene Name ist leer!");
            return;
        }

        if(farmWelcome == null || farmWelcome.isEmpty()) {
            printError(conversation, "Die angegebene Willkommensnachricht ist leer!");
            return;
        }

        if(farmFarewell == null || farmFarewell.isEmpty()) {
            printError(conversation, "Die angegebene Abschiedsnachricht ist leer!");
            return;
        }

        ConfigurationSection materialSection = conversation.getConfigurationSection("material");

        if(materialSection == null) {
            printError(conversation, "Der Farm ist kein Material zugewiesen!");
            return;
        }

        List<Material> materials = new ArrayList<>();
        for(String materialName : materialSection.getKeys(false)) {
            Material material = Material.valueOf(materialName);
            if(material != null) {
                materials.add(material);
            }
        }
        if(materials.isEmpty()) {
            printError(conversation, "Der Farm ist kein gültiges Material zugewiesen!");
            return;
        }

        Selection selection = RaidCraft.getComponent(RCFarmsPlugin.class).getWorldEdit().getSelection(conversation.getPlayer());

        if(selection == null || selection.getMaximumPoint() == null || selection.getMinimumPoint() == null) {
            printError(conversation, "Kein Bereich selektiert!");
            return;
        }

        // create farm
        FarmBuilder farmBuilder = new FarmBuilder();
        farmBuilder.setName(farmName);
        farmBuilder.setCreator(conversation.getPlayer().getName());
        farmBuilder.setWelcomeMessage(farmWelcome);
        farmBuilder.setFarewellMessage(farmFarewell);
        farmBuilder.addMaterials(materials);
        farmBuilder.setMinimumPoint(selection.getMinimumPoint());
        farmBuilder.setMaximumPoint(selection.getMaximumPoint());
        try {
            farmBuilder.createFarm();
        } catch (RaidCraftException e) {
            printError(conversation, e.getMessage());
            return;
        }
        conversation.getPlayer().sendMessage(ChatColor.GREEN + "Die Farm '" + farmName + "' wurde erfolgreich erstellt!");
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.RED + "Das erstellen der Farm wurde abgebrochen!");
    }
}
