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
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "SELECT_FARM")
public class SelectFarmAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String input = conversation.getString("input");

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(input);
        if(tFarm == null) {
            printError(conversation, "Es wurde keine passende Farm gefunden!");
            return;
        }
        conversation.set("farm_id", tFarm.getId());

        ConfigurationSection materialSection = conversation.createSection("material");
        StringBuilder materialList = new StringBuilder();
        for(TMaterial tMaterial : tFarm.getMaterials()) {
            materialSection.set(tMaterial.getName(), true);
            if(materialList.length() > 0) materialList.append(", ");
            materialList.append(ItemUtils.getFriendlyName(tMaterial.getBukkitMaterial()));
        }
        conversation.set("material_list", materialList.toString());
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.AQUA + "Probiere es nochmal:");
        conversation.abortActionExecution(true);
    }
}
