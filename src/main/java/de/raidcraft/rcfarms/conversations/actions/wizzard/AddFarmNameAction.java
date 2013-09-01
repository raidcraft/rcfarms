package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.RaidCraft;
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
@ActionInformation(name = "ADD_FARM_NAME")
public class AddFarmNameAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String name = conversation.getString("input");

        if(name == null || name.isEmpty() || name.length() < 3) {
            printError(conversation, "Der Name muss mindestens 3 Zeichen lang sein!");
            return;
        }

        if(name.contains("_")) {
            printError(conversation, "Der Name darf keine Unterstriche enthalten!");
            return;
        }

        try {
            Integer.valueOf(name);
            printError(conversation, "Der Name darf keine Zahl sein!");
            return;
        }
        catch(NumberFormatException e) {}

        TFarm tFarm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(name);
        if(tFarm != null) {
            printError(conversation, "Eine Farm mit dem name '" + tFarm.getName() + "' existiert bereits!");
            return;
        }

        conversation.set("farm_name", name);
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.AQUA + "Name nochmals eingeben:");
        conversation.abortActionExecution(true);
    }
}
