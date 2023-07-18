package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import net.playavalon.avnparty.player.AvalonPlayer;

public class PartyCreateEvent extends PartyEvent {
    private final AvalonPlayer hostPlayer;


    public PartyCreateEvent(Party party, AvalonPlayer host) {
        super(party);
        this.hostPlayer = host;
    }


    public AvalonPlayer getHostPlayer() {
        return hostPlayer;
    }

}
