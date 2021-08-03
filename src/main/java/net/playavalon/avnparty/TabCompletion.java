package net.playavalon.avnparty;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.playavalon.avnparty.AvNParty.plugin;

public class TabCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("party")) return null;
        if(!(sender instanceof Player)) return null;

        Player player = (Player)sender;
        AvalonPlayer aPlayer = plugin.getAvalonPlayer(player);
        Party party = aPlayer.getParty();

        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("invite");
            list.add("join");
            if (party != null) {
                list.add("leave");
                list.add("chat");
                if (party.getLeader() == aPlayer) {
                    list.add("givelead");
                    list.add("kick");
                    list.add("disband");
                }
            }
            /*if (Util.hasPermissionSilent(player, "avni.admin")) {
                //list.add("activepoints");
                list.add("give ");
                list.add("get ");
                list.add("info ");
                list.add("hunt");
                list.add("reload ");
                list.add("updateitems");
                list.add("generatepack");
                //list.add("changeallcooldowns");
            }*/
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "invite":
                    for (Player listPlayer : Bukkit.getOnlinePlayers()) {
                        list.add(listPlayer.getName());
                    }
                    break;
                case "kick":
                case "givelead":
                    if (party != null) {
                        for (AvalonPlayer listAPlayer : party.getPlayers()) {
                            list.add(listAPlayer.getPlayer().getName());
                        }
                    }
                    break;
            }
        }

        return list;
    }
}
