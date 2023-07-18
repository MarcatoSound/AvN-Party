package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.event.Cancellable;

public class PartyKickEvent extends PartyEvent implements Cancellable {
    private boolean cancelled;

    private final AvalonPlayer kickedPlayer;
    private final AvalonPlayer whoKicked;

    public PartyKickEvent(Party party, AvalonPlayer kickedPlayer, AvalonPlayer whoKicked) {
        super(party);
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
