package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "ADD_FARM_MATERIAL")
public class AddFarmMaterialAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String input = conversation.getString("input");
        Material material = ItemUtils.getItem(input);

        if(material == null) {
            conversation.getPlayer().sendMessage(ChatColor.RED + "Unbekanntes Material '" + input + "'! Eingabe wiederholen:");
            conversation.abortActionExecution(true);
            return;
        }

        // save the material as keys in a new config section within the conversation
        ConfigurationSection materialSection = conversation.getConfigurationSection("material");
        if(materialSection == null) {
            materialSection = conversation.createSection("material");
        }
        materialSection.set(material.name(), true);
    }
}
