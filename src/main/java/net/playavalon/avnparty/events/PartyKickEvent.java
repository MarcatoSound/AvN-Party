package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PartyKickEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled;

    private final Party party;
    private final AvalonPlayer kickedPlayer;
    private final AvalonPlayer whoKicked;

    public PartyKickEvent(Party party, AvalonPlayer kickedPlayer, AvalonPlayer whoKicked) {
        this.party = party;
        this.kickedPlayer = kickedPlayer;
        this.whoKicked = whoKicked;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Party getParty() {
        return party;
    }

    public AvalonPlayer getKickedPlayer() {
        return kickedPlayer;
    }

    public AvalonPlayer getWhoKicked() {
        return whoKicked;
    }
}
