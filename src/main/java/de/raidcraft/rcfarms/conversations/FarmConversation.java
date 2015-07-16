package de.raidcraft.rcfarms.conversations;

import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.conversations.conversations.PlayerConversation;
import de.raidcraft.rcfarms.tables.TFarm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FarmConversation extends PlayerConversation {

    private String farmName;
    private boolean allowAllMaterials = false;
    private long regenerationInterval;
    private Set<Material> allowedMaterials = new HashSet<>();

    private TFarm farmToDelete;

    private TFarm farm;

    public FarmConversation(Player player, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {

        super(player, conversationTemplate, conversationHost);
    }
}
