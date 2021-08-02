package net.playavalon.avnparty.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyManager {

    private HashMap<UUID, Party> partiesByPlayer;

    public PartyManager() {
        partiesByPlayer = new HashMap<>();
    }

    public void add(Party party) {

    }

    public Party get(Player player) {
        return partiesByPlayer.get(player.getUniqueId());
    }

}
