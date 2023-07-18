package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;
import org.bukkit.event.Cancellable;

public class PartyJoinEvent extends PartyEvent implements Cancellable {
    private boolean cancelled;

    private final AvalonPlayer joiningPlayer;

    public PartyJoinEvent(Party party, AvalonPlayer player) {
        super(party);
        joiningPlayer = player;
    }

    public AvalonPlayer getJoiningPlayer() {
        return joiningPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
