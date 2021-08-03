package net.playavalon.avnparty.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.playavalon.avnparty.AvNParty;
import net.playavalon.avnparty.Util;
import net.playavalon.avnparty.party.Party;
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

        Component debugPrefix = Component.text("[Party] ").color(TextColor.fromCSSHexString("#3aace0"));
        Component message = Component.text(Util.colorize("&6" + inviteFrom.getName() + " &binvited you to their party. "));
        Component joinParty = Component.text(Util.colorize("&e&l[JOIN]")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/party join"));

        message = debugPrefix.append(message).append(joinParty);

        player.sendMessage(message);

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
