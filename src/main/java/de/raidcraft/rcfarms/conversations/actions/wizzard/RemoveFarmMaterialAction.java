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
@ActionInformation(name = "REMOVE_FARM_MATERIAL")
public class RemoveFarmMaterialAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String input = conversation.getString("input");

        ConfigurationSection materialSection = conversation.getConfigurationSection("material");
        if(materialSection == null) {
            printError(conversation, "Für die gewählte Farm sind keine Materialien konfiguriert!");
            return;
        }

        Material inputMaterial = ItemUtils.getItem(input);
        if(inputMaterial == null) {
            printError(conversation, "Kein passendes Material gefunden!");
            return;
        }

        for(String material : materialSection.getKeys(false)) {

            if(ItemUtils.getItem(material) == inputMaterial) {
                materialSection.set(material, null);
                conversation.getPlayer().sendMessage(ChatColor.GREEN + ItemUtils.getFriendlyName(inputMaterial, ItemUtils.Language.GERMAN)
                        + " wurde erfolgreich entfernt!");
                break;
            }
        }

        // update material list
        StringBuilder materialList = new StringBuilder();
        for(String material : materialSection.getKeys(false)) {
            if(materialSection.get(material) == null) continue;
            if(materialList.length() > 0) materialList.append(", ");
            materialList.append(ItemUtils.getFriendlyName(ItemUtils.getItem(material)));
        }
        conversation.set("material_list", materialList.toString());
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.AQUA + "Probiere es nochmal:");
        conversation.abortActionExecution(true);
    }
}
