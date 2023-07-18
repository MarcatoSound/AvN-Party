package net.playavalon.avnparty.listeners;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
                party.setPlayerOnline(player, true);
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

        Party party = aPlayer.getParty();
        if (party != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) return;
                    party.setPlayerOnline(player, false);
                    party.updateScoreboard();
                }
            }.runTaskLaterAsynchronously(plugin, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) return;
                    party.partyMessage("&6" + player.getName() + " &cwas kicked for being offline too long!");
                    party.removePlayer(player);
                }
            }.runTaskLaterAsynchronously(plugin, 6000);
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPartyChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        if (!aPlayer.isPartyChat()) return;
        Party party = aPlayer.getParty();

        String message = event.getMessage();
        party.sendChatMessage(player, message);

        event.setCancelled(true);
    }
}
