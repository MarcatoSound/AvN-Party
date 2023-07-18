package net.playavalon.avnparty.events;

import net.playavalon.avnparty.party.Party;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class PartyEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    protected final Party party;

    public PartyEvent(Party party) {
        this.party = party;
    }


    public Party getParty() {
        return party;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
