package de.raidcraft.rcfarms.conversations.host;

import de.raidcraft.api.conversations.AbstractConversationHost;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public class FarmHost extends AbstractConversationHost {

    private Location location;
    private String conversationName;
    private final static String NAME = "FarmConversation";

    public FarmHost(Location location, String conversationName) {

        super(NAME, conversationName);
        this.conversationName = conversationName;
        this.location = location;
    }

    @Override
    public String getName() {

        return NAME;
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
