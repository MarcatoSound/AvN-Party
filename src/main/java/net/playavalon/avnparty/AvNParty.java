package net.playavalon.avnparty;

import net.playavalon.avnparty.listeners.AvalonListener;
import net.playavalon.avnparty.listeners.TabCompletion;
import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.party.PartyManager;
import net.playavalon.avnparty.player.AvalonPlayer;
import net.playavalon.avnparty.player.PlayerManager;
import net.playavalon.avnparty.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AvNParty extends JavaPlugin {

    public static AvNParty plugin;
    public static String debugPrefix;
    //public FileConfiguration config;

    public PlayerManager players;
    public PartyManager parties;
    public List<AvalonPlayer> spies;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        //saveDefaultConfig();
        //config = this.getConfig();
        debugPrefix = Util.fullColor("{#3aace0}[Party] ");

        players = new PlayerManager();
        parties = new PartyManager();
        spies = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(new AvalonListener(), this);

        getCommand("party").setTabCompleter(new TabCompletion());
        getCommand("dparty").setTabCompleter(new TabCompletion());

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

            // PARTY CHAT
            case "p":
                if (!(sender instanceof Player)) return false;

                player = (Player)sender;
                aPlayer = getAvalonPlayer(player);

                party = aPlayer.getParty();
                if (party == null) {
                    player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                    return false;
                }

                if (args.length == 0) {
                    aPlayer.togglePartyChat();
                    return false;
                }

                StringBuilder msg = new StringBuilder();
                for (String arg : args) {
                    msg.append(arg + " ");
                }

                party.sendChatMessage(player, msg.toString());

                break;

            // GENERAL COMMAND
            case "dparty":
            case "party":

                if (args.length == 0) {
                    if (!(sender instanceof Player)) return false;
                    player = (Player)sender;
                    aPlayer = getAvalonPlayer(player);

                    party = aPlayer.getParty();
                    if (party == null) {
                        sender.sendMessage(Util.colorize(debugPrefix + "Dungeon Parties - Version 1.0"));
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
                        aPlayer = getAvalonPlayer(player);

                        targetPlayer = Bukkit.getPlayer(args[1]);
                        if (targetPlayer == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cNo player found by name '&6" + args[1] + "&c'"));
                            return false;
                        }

                        if (player.getName().equals(args[1])) {

                            List<String> inviteSelfMsg = new ArrayList<>();
                            inviteSelfMsg.add("You must be so lonely.");
                            inviteSelfMsg.add("Come on, go make some friends!");
                            inviteSelfMsg.add("No, you cannot invite yourself to a party.");
                            inviteSelfMsg.add("I suppose you think you're terribly clever.");
                            inviteSelfMsg.add("It's not a party if there's only one person.");
                            inviteSelfMsg.add("It's tough being the hero, huh?");
                            Random rand = new Random();

                            player.sendMessage(debugPrefix + Util.colorize("&d" + inviteSelfMsg.get(rand.nextInt(inviteSelfMsg.size()))));
                            return false;
                        }

                        AvalonPlayer targetAPlayer = getAvalonPlayer(targetPlayer);

                        if (targetAPlayer.getParty() != null) {
                            player.sendMessage(debugPrefix + Util.colorize("&6" + args[1] + " &cis already in a party!"));
                            return false;
                        }

                        Party currentParty = aPlayer.getParty();
                        if (currentParty != null) {
                            if (currentParty.getLeader() != aPlayer) {
                                player.sendMessage(debugPrefix + Util.colorize("&cOnly the party leader can invite players!"));
                                return false;
                            }
                            if (targetAPlayer.getParty() == currentParty) {
                                player.sendMessage(debugPrefix + Util.colorize("&cThat player is already in your party!"));
                                return false;
                            }
                        }

                        targetAPlayer.setInviteFrom(player);

                        player.sendMessage(debugPrefix + Util.colorize("&bInvited &6" + targetPlayer.getName() + " &bto your party."));

                        break;

                    case "join":
                        if (args.length != 1) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        Player inviteFrom = targetAPlayer.getInviteFrom();
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
                        targetAPlayer.setInviteFrom(null);

                        break;

                    case "leave":
                        if (args.length != 1) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        party = targetAPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        player.sendMessage(debugPrefix + Util.colorize("&bYou left &6" + party.getLeader().getPlayer().getName() + "'s &bparty."));

                        party.removePlayer(player);

                        break;

                    case "givelead":
                        if (args.length != 2) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        party = targetAPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (targetAPlayer != party.getLeader()) {
                            player.sendMessage(debugPrefix + Util.colorize("&cOnly party leaders can change the leader."));
                            return false;
                        }

                        AvalonPlayer newAPlayer = party.getPlayer(args[1]);
                        targetPlayer = newAPlayer.getPlayer();
                        if (targetPlayer == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cNo player found by name '&6" + args[1] + "&c' in the party."));
                            return false;
                        }

                        party.setLeader(targetPlayer);
                        break;

                    case "kick":
                        if (args.length != 2) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        party = targetAPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (targetAPlayer != party.getLeader()) {
                            player.sendMessage(debugPrefix + Util.colorize("&cOnly party leaders can kick players."));
                            return false;
                        }

                        if (party.getPlayer(args[1]) == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cNo player found by name '&6" + args[1] + "&c'"));
                            return false;
                        }

                        party.kickPlayer(args[1]);

                        if (player.getName().equals(args[1])) {
                            List<String> kickSelfMessages = new ArrayList<>();
                            kickSelfMessages.add("Stop hitting yourself!");
                            kickSelfMessages.add("What, did you slip?");
                            kickSelfMessages.add("How did you even do that-?");
                            kickSelfMessages.add("That bad, huh?");
                            kickSelfMessages.add("You know there's a leave command, right?");
                            kickSelfMessages.add("Nice.");
                            Random rand = new Random();

                            player.sendMessage(debugPrefix + Util.colorize("&d" + kickSelfMessages.get(rand.nextInt(kickSelfMessages.size()))));
                        }
                        break;

                    case "disband":
                        if (args.length != 1) return false;
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        party = targetAPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (targetAPlayer != party.getLeader()) {
                            player.sendMessage(debugPrefix + Util.colorize("&cOnly party leaders can disband the party."));
                            return false;
                        }

                        party.disband();

                        break;

                    case "chat":
                        if (!(sender instanceof Player)) return false;
                        if (!Util.hasPermission(sender, "dungeonparties.spy")) return false;

                        player = (Player)sender;
                        targetAPlayer = getAvalonPlayer(player);

                        party = targetAPlayer.getParty();
                        if (party == null) {
                            player.sendMessage(debugPrefix + Util.colorize("&cYou aren't currently in a party."));
                            return false;
                        }

                        if (args.length == 1) {
                            targetAPlayer.togglePartyChat();
                            return false;
                        }

                        StringBuilder message = new StringBuilder();
                        for (int m = 1; m < args.length; m++) {
                            message.append(args[m] + " ");
                        }

                        party.sendChatMessage(player, message.toString());

                        break;

                    case "spy":
                        if (!(sender instanceof Player)) return false;

                        player = (Player)sender;
                        aPlayer = getAvalonPlayer(player);

                        if (!spies.contains(aPlayer)) {
                            spies.add(aPlayer);
                            player.sendMessage(debugPrefix + Util.colorize("&aYou are now spying on party messages from other players!"));
                        } else {
                            spies.remove(aPlayer);
                            player.sendMessage(debugPrefix + Util.colorize("&cYou are no longer spying on party messages from other players."));
                        }

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
        AvalonPlayer aPlayer = getAvalonPlayer(player);
        return aPlayer.getParty();
    }

}
