package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "UPDATE_FARM_SCHEMATIC")
public class UpgradeFarmSchematicAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm((String)conversation.get("farm_id"));
        if(tFarm == null) {
            conversation.getPlayer().sendMessage(ChatColor.RED + "Keine Farm selektiert! Fehler bitte melden!");
            return;
        }

        try {
            RaidCraft.getComponent(RCFarmsPlugin.class).getSchematicManager().createSchematic(tFarm, 0);
            conversation.getPlayer().sendMessage(ChatColor.GREEN + "Farm Schematic wurde aktualisiert!");
        } catch (RaidCraftException e) {
            conversation.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
