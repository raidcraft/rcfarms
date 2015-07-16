package de.raidcraft.rcfarms.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.rcfarms.RCFarmsPlugin;
import de.raidcraft.rcfarms.tables.TFarm;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class UpdateFarmSchematicAction implements Action<Player> {

    @Override
    @Information(
            value = "farm.update",
            desc = "Updates the schematic of the farm using it as the new base schematic.",
            conf = {
                    "farm",
                    "upgrade-level: defaults to 0"
            },
            aliases = "UPDATE_FARM_SCHEMATIC"
    )
    public void accept(Player player, ConfigurationSection config) {

        String farmId = ConversationVariable.getString(player, "farm_id").orElse(config.getString("farm"));
        TFarm farm = RaidCraft.getComponent(RCFarmsPlugin.class).getFarmManager().getFarm(farmId);
        if (farm == null) {
            player.sendMessage(ChatColor.RED + "Ungültige Farm selektiert!");
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
            return;
        }

        try {
            RaidCraft.getComponent(RCFarmsPlugin.class).getSchematicManager().createSchematic(farm, config.getInt("upgrade-level", 0));
            player.sendMessage(ChatColor.GREEN + "Farm Schematic wurde aktualisiert!");
        } catch (RaidCraftException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
            Conversations.endActiveConversation(player, ConversationEndReason.ERROR);
        }
    }
}
