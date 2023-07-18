package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;

public class PartyLeaveEvent extends PartyEvent {

    private final AvalonPlayer leavingPlayer;

    public PartyLeaveEvent(Party party, AvalonPlayer leavingPlayer) {
        super(party);
        this.leavingPlayer = leavingPlayer;
    }


    public AvalonPlayer getLeavingPlayer() {
        return leavingPlayer;
    }
}
