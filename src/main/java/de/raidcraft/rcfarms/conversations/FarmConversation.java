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
import java.util.stream.Collectors;

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

    public void addMaterial(Material material) {

        allowedMaterials.add(material);
        set("material_list", allowedMaterials.stream().map(Enum::name).collect(Collectors.joining(",")));
    }

    public void removeMaterial(Material material) {

        allowedMaterials.remove(material);
        set("material_list", allowedMaterials.stream().map(Enum::name).collect(Collectors.joining(",")));
    }

    public boolean containsMaterial(Material material) {

        return allowedMaterials.contains(material);
    }
}
