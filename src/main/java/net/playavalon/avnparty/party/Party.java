package net.playavalon.avnparty.party;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.playavalon.avnparty.Util;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.playavalon.avnparty.AvNParty.debugPrefix;
import static net.playavalon.avnparty.AvNParty.plugin;

public class Party {

    private AvalonPlayer leader;
    private final List<AvalonPlayer> players;

    public Party(Player leader) {
        players = new ArrayList<>();

        setLeader(leader);

        AvalonPlayer aLeader = plugin.getAvalonPlayer(leader);
        players.add(aLeader);
        aLeader.setParty(this);
    }

    public void sendChatMessage(Player player, String message) {
        String displayName = PlainComponentSerializer.plain().serialize(player.displayName());
        for (AvalonPlayer aPlayer : players) {
            aPlayer.getPlayer().sendMessage(debugPrefix + Util.fullColor("&6" + displayName + "&f: &b" + message));
        }

        System.out.println(debugPrefix + Util.fullColor("&6" + displayName + "&f: &b" + message));
    }

    public void partyMessage(String message) {
        for (AvalonPlayer player : players) {
            player.getPlayer().sendMessage(debugPrefix + Util.fullColor(message));
        }
    }
    public void leaderMessage(String message) {
        leader.getPlayer().sendMessage(debugPrefix + Util.fullColor(message));
    }


    public void addPlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        players.add(aPlayer);
        aPlayer.setParty(this);

        partyMessage("&6" + player.getName() + " &bjoined the party!");
    }

    public void removePlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        players.remove(aPlayer);

        aPlayer.setPartyChat(false);

        partyMessage("&6" + player.getName() + " &bleft the party!");
    }


    public AvalonPlayer getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = plugin.getAvalonPlayer(leader);
    }

    public void changeLeader(Player leader) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(leader);

        if (!players.contains(aPlayer)) {
            leaderMessage("&cPlayer " + leader.getName() + " is not a party member.");
            return;
        }

        setLeader(leader);
    }

    public List<AvalonPlayer> getPlayers() {
        return players;
    }


    public void sendPartyInfo(Player player) {
        player.sendMessage(Util.colorize("&b&lParty owned by " + leader.getPlayer().getName()));

        for (AvalonPlayer aPlayer : players) {
            player.sendMessage(Util.colorize("&9" + aPlayer.getPlayer().getName()));
        }

    }

}
