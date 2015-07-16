package de.raidcraft.rcfarms.conversations;

import de.raidcraft.api.conversations.answer.ConfiguredAnswer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class SetExplicitRegenerationDelayInput extends ConfiguredAnswer {

    public SetExplicitRegenerationDelayInput(String type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection args) {


    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        long interval = (long) TimeUtil.ticksToSeconds(TimeUtil.parseTimeAsTicks(input));

        // 7 days
        if (interval < 24 * 60 * 60) {
            conversation.sendMessage(ChatColor.RED + "Die Regenerationszeit muss mindestens 1 Tag betragen!");
            return false;
        }

        conversation.set("regeneration_interval", interval);
        return true;
    }
}
