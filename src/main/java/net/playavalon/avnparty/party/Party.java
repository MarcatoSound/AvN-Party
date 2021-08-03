package net.playavalon.avnparty.party;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.playavalon.avnparty.Util;
import net.playavalon.avnparty.events.PartyKickEvent;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.playavalon.avnparty.AvNParty.debugPrefix;
import static net.playavalon.avnparty.AvNParty.plugin;

public class Party {

    private AvalonPlayer leader;
    private final List<AvalonPlayer> players;
    private final HashMap<String, AvalonPlayer> playersByName;

    private Scoreboard partyScoreboard;
    private Objective partyMemberDisplay;

    public Party(Player leader) {
        players = new ArrayList<>();
        playersByName = new HashMap<>();

        setLeader(leader);

        AvalonPlayer aLeader = plugin.getAvalonPlayer(leader);
        players.add(aLeader);
        playersByName.put(leader.getName(), aLeader);
        aLeader.setParty(this);


        if (Bukkit.getPluginManager().getPlugin("AvNCombat") == null) {
            partyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            partyMemberDisplay = partyScoreboard.registerNewObjective("partymembers", "dummy", Component.text("Party Members").color(TextColor.fromCSSHexString("#3aace0")));
            partyMemberDisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

    }

    public void updateScoreboard() {
        if (Bukkit.getPluginManager().getPlugin("AvNCombat") != null) return;

        for (AvalonPlayer aPlayer : players) {
            Player player = aPlayer.getPlayer();

            player.setScoreboard(partyScoreboard);

            String scoreboardName;
            if (!player.isOnline()) {
                partyScoreboard.resetScores(Util.colorize("&b" + player.getName()));
                scoreboardName = Util.colorize("&7" + player.getName());
            }
            else {
                partyScoreboard.resetScores(Util.colorize("&7" + player.getName()));
                scoreboardName = Util.colorize("&b" + player.getName());
            }

            Score score = partyMemberDisplay.getScore(scoreboardName);
            score.setScore(0);
        }
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
        playersByName.put(player.getName(), aPlayer);
        aPlayer.setParty(this);

        updateScoreboard();

        partyMessage("&6" + player.getName() + " &bjoined the party!");
    }

    public void removePlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        players.remove(aPlayer);
        playersByName.remove(player.getName());

        aPlayer.setParty(null);
        aPlayer.setPartyChat(false);

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        partyScoreboard.resetScores(Util.colorize("&7" + player.getName()));
        partyScoreboard.resetScores(Util.colorize("&b" + player.getName()));


        updateScoreboard();

        if (players.size() < 1) {
            disband();
            return;
        }

        partyMessage("&6" + player.getName() + " &bleft the party!");

        if (aPlayer == leader) {
            Player newLeader = players.get(0).getPlayer();
            setLeader(newLeader);
            partyMessage("&6" + newLeader.getName() + " &bis the new party leader.");
        }
    }

    public void kickPlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);

        PartyKickEvent event = new PartyKickEvent(this, aPlayer, leader);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        player.sendMessage(debugPrefix + Util.colorize("&cYou were kicked from &6" + leader.getPlayer().getName() + "'s &cparty."));
        removePlayer(player);
        partyMessage("&6" + player.getName() + " &cwas kicked from the party.");
    }
    public void kickPlayer(String name) {
        AvalonPlayer aPlayer = playersByName.get(name);
        Player player = aPlayer.getPlayer();

        PartyKickEvent event = new PartyKickEvent(this, aPlayer, leader);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if (player.isOnline()) player.sendMessage(debugPrefix + Util.colorize("&cYou were kicked from &6" + leader.getPlayer().getName() + "'s &cparty."));

        removePlayer(player);

        partyMessage("&6" + player.getName() + " &cwas kicked from the party.");
    }

    public void disband() {
        partyMessage(debugPrefix + "&bThe party was disbanded.");
        leader = null;
        for (AvalonPlayer aPlayer : players) {
            removePlayer(aPlayer.getPlayer());
        }
    }


    public AvalonPlayer getLeader() {
        return leader;
    }

    public AvalonPlayer getPlayer(String name) {
        return playersByName.get(name);
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
