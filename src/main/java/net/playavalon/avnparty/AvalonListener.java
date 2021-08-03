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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static net.playavalon.avnparty.AvNParty.plugin;

public class AvalonListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        if (aPlayer != null) {
            // Update the existing AvalonPlayer
            aPlayer.setPlayer(player);

            Party party = aPlayer.getParty();
            if (party != null) {
                party.updateScoreboard();
            }
        } else {
            // Create a new AvalonPlayer and register it.
            aPlayer = new AvalonPlayer(player);
            plugin.players.put(aPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                Party party = aPlayer.getParty();
                if (party != null) {
                    party.updateScoreboard();
                }
            }
        }.runTaskLater(plugin, 1);
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
