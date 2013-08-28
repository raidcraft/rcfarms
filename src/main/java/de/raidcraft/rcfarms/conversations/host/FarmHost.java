package de.raidcraft.rcfarms.conversations.host;

import de.raidcraft.api.conversations.ConversationHost;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public class FarmHost implements ConversationHost {

    private Location location;

    public FarmHost(Location location) {

        this.location = location;
    }

    @Override
    public String getName() {

        return "FarmConversation";
    }

    @Override
    public Location getLocation() {

        return location;
    }

    @Override
    public String getUniqueId() {

        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":FarmConversation";
    }
}
