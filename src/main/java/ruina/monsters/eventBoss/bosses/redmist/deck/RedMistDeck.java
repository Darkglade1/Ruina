package ruina.monsters.eventBoss.bosses.redmist.deck;

import ruina.monsters.eventBoss.bosses.redmist.cards.CHR_UpstandingSlash;
import ruina.monsters.eventBoss.bosses.redmist.relic.RedMistInfoRelic;
import ruina.monsters.eventBoss.core.AbstractBossDeckArchetype;

public class RedMistDeck extends AbstractBossDeckArchetype {

    public RedMistDeck(String id) {
        super(id);
    }

    public void initialize() {
        addRelic(new RedMistInfoRelic());
        addCardToList(new CHR_UpstandingSlash());
        addCardToList(new CHR_UpstandingSlash());
        addCardToList(new CHR_UpstandingSlash());

    }
}
