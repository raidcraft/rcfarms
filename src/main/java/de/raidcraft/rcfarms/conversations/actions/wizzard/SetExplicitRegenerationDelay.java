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
        int delay_days;
        try {
            delay_days = Integer.valueOf(input);
        } catch(NumberFormatException e) {
            printError(conversation, "Die Regenerationszeit muss als Zahl in Tagen angegeben werden!");
            return;
        }

        long delay_seconds = delay_days * 24 * 60 * 60;

        conversation.set("regeneration_delay", delay_seconds);
    }

    private void printError(Conversation conversation, String msg) {

        conversation.getPlayer().sendMessage(ChatColor.RED + msg);
        conversation.getPlayer().sendMessage(ChatColor.AQUA + "Zeit in Tagen nochmals eingeben:");
        conversation.abortActionExecution(true);
    }
}
