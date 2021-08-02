package net.playavalon.avnparty.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private HashMap<UUID, AvalonPlayer> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void put(AvalonPlayer aPlayer) {
        players.put(aPlayer.getPlayer().getUniqueId(), aPlayer);
    }

    public AvalonPlayer get(Player player) {
        return players.get(player.getUniqueId());
    }

}
