package net.playavalon.avnparty.party;

import net.playavalon.avnparty.events.PartyCreateEvent;
import net.playavalon.avnparty.events.PartyJoinEvent;
import net.playavalon.avnparty.events.PartyKickEvent;
import net.playavalon.avnparty.events.PartyLeaveEvent;
import net.playavalon.avnparty.player.AvalonPlayer;
import net.playavalon.avnparty.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.playavalon.avnparty.AvNParty.debugPrefix;
import static net.playavalon.avnparty.AvNParty.plugin;

public class Party {

    private AvalonPlayer leader;
    private final List<AvalonPlayer> players;
    private final List<AvalonPlayer> onlinePlayers;
    private final HashMap<String, AvalonPlayer> playersByName;

    private Scoreboard partyScoreboard;
    private Objective partyMemberDisplay;

    public Party(Player leader) {
        players = new ArrayList<>();
        onlinePlayers = new ArrayList<>();
        playersByName = new HashMap<>();

        setLeader(leader);

        AvalonPlayer aLeader = plugin.getAvalonPlayer(leader);
        players.add(aLeader);
        onlinePlayers.add(aLeader);
        playersByName.put(leader.getName(), aLeader);
        aLeader.setParty(this);

        PartyCreateEvent event = new PartyCreateEvent(this, aLeader);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));

        /*if (Bukkit.getPluginManager().getPlugin("AvNCombat") == null) {
            partyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            partyMemberDisplay = partyScoreboard.registerNewObjective("partymembers", "dummy", Component.text("Party Members").color(TextColor.fromCSSHexString("#3aace0")));
            partyMemberDisplay.setDisplaySlot(DisplaySlot.SIDEBAR);
        }*/

    }

    public void updateScoreboard() {
        if (Bukkit.getPluginManager().getPlugin("AvNCombat") != null) return;
        if (partyScoreboard == null) return;

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
        String displayName = player.getDisplayName();
        for (AvalonPlayer aPlayer : onlinePlayers) {
            aPlayer.getPlayer().sendMessage(debugPrefix + Util.fullColor("&6" + displayName + "&f: &b" + message));
        }

        System.out.println(debugPrefix + Util.fullColor("&6" + displayName + "&f: &b" + message));

        for (AvalonPlayer aPlayer : plugin.spies) {
            aPlayer.getPlayer().sendMessage(Util.fullColor("&7[" + leader.getPlayer().getName() + "'s Party] &f" + displayName + "&f: &7" + message));
        }
    }

    public void partyMessage(String message) {
        for (AvalonPlayer player : onlinePlayers) {
            player.getPlayer().sendMessage(debugPrefix + Util.fullColor(message));
        }
    }
    public void leaderMessage(String message) {
        leader.getPlayer().sendMessage(debugPrefix + Util.fullColor(message));
    }


    public void addPlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);
        PartyJoinEvent event = new PartyJoinEvent(this, aPlayer);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        players.add(aPlayer);
        onlinePlayers.add(aPlayer);
        playersByName.put(player.getName(), aPlayer);
        aPlayer.setParty(this);

        updateScoreboard();

        partyMessage("&6" + player.getName() + " &bjoined the party!");
    }

    public void removePlayer(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);
        PartyLeaveEvent event = new PartyLeaveEvent(this, aPlayer);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));

        players.remove(aPlayer);
        onlinePlayers.remove(aPlayer);
        playersByName.remove(player.getName());

        aPlayer.setParty(null);
        aPlayer.setPartyChat(false);

        if (partyScoreboard != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            partyScoreboard.resetScores(Util.colorize("&7" + player.getName()));
            partyScoreboard.resetScores(Util.colorize("&b" + player.getName()));
        }
        updateScoreboard();


        if (players.size() < 1) {
            disband();
            return;
        }

        partyMessage("&6" + player.getName() + " &bleft the party!");
        if (aPlayer == leader) defactoLeader();
    }

    public void setPlayerOnline(Player player, boolean online) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);
        if (!players.contains(aPlayer)) return;

        if (online) {
            onlinePlayers.add(aPlayer);
            partyMessage("&6" + player.getName() + " &bhas returned to the party.");
        }
        else {
            onlinePlayers.remove(aPlayer);
            partyMessage("&6" + player.getName() + " &bhas logged off.");

            if (onlinePlayers.isEmpty()) {
                disband();
                return;
            }

            if (aPlayer == leader) defactoLeader();
        }
    }
    public boolean isPlayerOnline(Player player) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);
        if (!players.contains(aPlayer)) return false;

        return onlinePlayers.contains(aPlayer);
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
        if (players.size() > 1) partyMessage("&bThe party was disbanded.");
        leader = null;
        List<AvalonPlayer> aPlayers = new ArrayList<>(players);
        for (AvalonPlayer aPlayer : aPlayers) {
            removePlayer(aPlayer.getPlayer());
        }
    }


    public AvalonPlayer getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(leader);
        if (aPlayer == null) return;

        this.leader = aPlayer;
    }

    public void changeLeader(Player leader) {
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(leader);

        if (!players.contains(aPlayer)) {
            leaderMessage("&cPlayer " + leader.getName() + " is not a party member.");
            return;
        }

        setLeader(leader);
    }

    private void defactoLeader() {
        Player newLeader = onlinePlayers.get(0).getPlayer();
        setLeader(newLeader);
        partyMessage("&6" + newLeader.getName() + " &bis the new party leader.");
    }

    public AvalonPlayer getPlayer(String name) {
        return playersByName.get(name);
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
