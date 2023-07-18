package net.playavalon.avnparty.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.playavalon.avnparty.AvNParty;
import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.utility.Util;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.playavalon.avnparty.AvNParty.debugPrefix;
import static net.playavalon.avnparty.AvNParty.plugin;

public class AvalonPlayer {

    private Player player;
    private Party party;

    private boolean partyChat;

    private Player inviteFrom;

    public AvalonPlayer(Player player) {
        this.player = player;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public boolean isPartyChat() {
        return partyChat;
    }

    public void setPartyChat(boolean partyChat) {
        this.partyChat = partyChat;
    }
    public void togglePartyChat() {
        if (isPartyChat()) {
            setPartyChat(false);
            player.sendMessage(debugPrefix + Util.colorize("&bDisabled party chat."));
        } else {
            setPartyChat(true);
            player.sendMessage(debugPrefix + Util.colorize("&bEnabled party chat."));
        }
    }

    public Player getInviteFrom() {
        return inviteFrom;
    }

    public void setInviteFrom(Player inviteFrom) {
        this.inviteFrom = inviteFrom;
        if (inviteFrom == null) return;

        TextComponent debugPrefix = new TextComponent(TextComponent.fromLegacyText(ChatColor.of("#3aace0") + "[Party] "));
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(Util.colorize("&6" + inviteFrom.getName() + " &binvited you to their party. ")));
        TextComponent joinParty = new TextComponent(TextComponent.fromLegacyText(Util.colorize("&e&l[JOIN]")));
        joinParty.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dparty join"));

        debugPrefix.addExtra(message);
        debugPrefix.addExtra(joinParty);

        player.spigot().sendMessage(ChatMessageType.CHAT, debugPrefix);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (getInviteFrom() == null) return;

                player.sendMessage(AvNParty.debugPrefix + Util.colorize("&cThe party invite from &6" + inviteFrom.getName() + " &chas expired..."));
                setInviteFrom(null);

            }
        }.runTaskLater(plugin, 4800);
    }
}
