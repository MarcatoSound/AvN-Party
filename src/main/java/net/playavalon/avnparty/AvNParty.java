package net.playavalon.avnparty;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.party.PartyManager;
import net.playavalon.avnparty.player.AvalonPlayer;
import net.playavalon.avnparty.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AvNParty extends JavaPlugin {

    public static AvNParty plugin;
    public static String debugPrefix;
    public FileConfiguration config;

    public PlayerManager players;
    public PartyManager parties;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        config = this.getConfig();
        debugPrefix = Util.fullColor("{#3aace0}[Party] ");

        players = new PlayerManager();
        parties = new PartyManager();

        Bukkit.getPluginManager().registerEvents(new AvalonListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        Player player;
        AvalonPlayer aPlayer;
        Player targetPlayer;
        Party party;

        switch (command.getName().toLowerCase()) {
            case "party":

                if (args.length == 0) {
                    if (!(sender instanceof Player)) return false;
                    player = (Player)sender;
                    aPlayer = getAvalonPlayer(player);

                    party = aPlayer.getParty();
                    if (party == null) {
                        sender.sendMessage(Util.colorize(debugPrefix + "Avalon Parties - Version 1.0 Beta"));
                        return false;
                    }

                    party.sendPartyInfo(player);

                    return false;
                }

                switch (args[0]) {
                    case "invite":
                        if (args.length != 2) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;

                        targetPlayer = Bukkit.getPlayer(args[1]);
                        if (targetPlayer == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cNo player found by name '&6" + args[1] + "&c'"));
                            return false;
                        }

                        aPlayer = getAvalonPlayer(targetPlayer);

                        aPlayer.setInviteFrom(player);

                        player.sendMessage(debugPrefix + Util.colorize("&bInvited &6" + targetPlayer.getName() + " &bto your party."));

                        break;

                    case "join":
                        if (args.length != 1) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        aPlayer = getAvalonPlayer(player);

                        Player inviteFrom = aPlayer.getInviteFrom();
                        if (inviteFrom == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently invited to a party."));
                            return false;
                        }

                        // If they're already in a party, remove them from the old one first.
                        Party oldParty = getParty(player);
                        if (oldParty != null) oldParty.removePlayer(player);

                        party = getParty(inviteFrom);

                        if (party == null) party = new Party(inviteFrom);

                        party.addPlayer(player);
                        aPlayer.setInviteFrom(null);

                        break;

                    case "leave":
                        if (args.length != 1) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        aPlayer = getAvalonPlayer(player);

                        party = aPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        party.removePlayer(player);
                        break;

                    case "givelead":
                        if (args.length != 2) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        aPlayer = getAvalonPlayer(player);

                        party = aPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (aPlayer != party.getLeader()) {
                            player.sendMessage(debugPrefix + Util.colorize("&cOnly party leaders can change the leader."));
                            return false;
                        }

                        targetPlayer = Bukkit.getPlayer(args[1]);
                        if (targetPlayer == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cNo player found by name '&6" + args[1] + "&c'"));
                            return false;
                        }

                        party.setLeader(targetPlayer);
                        break;

                    case "chat":
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        aPlayer = getAvalonPlayer(player);

                        party = aPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (args.length == 1) {
                            aPlayer.togglePartyChat();
                            return false;
                        }

                        StringBuilder msg = new StringBuilder();
                        for (int m = 1; m < args.length; m++) {
                            msg.append(args[m] + " ");
                        }

                        party.sendChatMessage(player, msg.toString());

                        break;
                }

                break;
        }
        return false;
    }



    public AvalonPlayer getAvalonPlayer(Player player) {
        return players.get(player);
    }

    public Party getParty(Player player) {
        return parties.get(player);
    }

}
