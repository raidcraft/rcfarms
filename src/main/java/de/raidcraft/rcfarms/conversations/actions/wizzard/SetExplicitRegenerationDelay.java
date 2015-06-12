package de.raidcraft.rcfarms.conversations.actions.wizzard;

import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentException;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "FARM_SET_REGENERATION_DELAY")
public class SetExplicitRegenerationDelay extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws ActionArgumentException {

        String input = conversation.getString("input");
        int interval_days;
        try {
            interval_days = Integer.valueOf(input);
        } catch(NumberFormatException e) {
            printError(conversation, "Die Regenerationszeit muss als Zahl in Tagen angegeben werden!");
            return;
        }

        if(interval_days < 7) {
            printError(conversation, "Die Regenerationszeit muss mindestens 7 Tage betragen!");
            return;
        }

        // delay in seconds
        long interval_seconds = interval_days * 24 * 60 * 60;

        conversation.set("regeneration_interval", interval_seconds);
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.AQUA + "Zeit in Tagen nochmals eingeben:");
        conversation.abortActionExecution(true);
    }
}
