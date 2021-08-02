package net.playavalon.avnparty;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.playavalon.avnparty.AvNParty.plugin;

public class AvalonListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        if (aPlayer != null) {
            // Update the existing AvalonPlayer
            aPlayer.setPlayer(player);
        } else {
            // Create a new AvalonPlayer and register it.
            aPlayer = new AvalonPlayer(player);
            plugin.players.put(aPlayer);
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPartyChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        if (!aPlayer.isPartyChat()) return;
        Party party = aPlayer.getParty();

        String message = PlainComponentSerializer.plain().serialize(event.message());
        party.sendChatMessage(player, message);

        event.setCancelled(true);
    }
}
