package ruina.monsters.eventBoss.bosses.redmist.deck;

import ruina.monsters.eventBoss.bosses.redmist.cards.CHR_GreaterSplitVertical;
import ruina.monsters.eventBoss.bosses.redmist.cards.CHR_LevelSlash;
import ruina.monsters.eventBoss.bosses.redmist.cards.CHR_Spear;
import ruina.monsters.eventBoss.bosses.redmist.cards.CHR_UpstandingSlash;
import ruina.monsters.eventBoss.bosses.redmist.relic.RedMistInfoRelic;
import ruina.monsters.eventBoss.core.AbstractBossDeckManager;

import static ruina.RuinaMod.makeID;

public class RedMistDeck extends AbstractBossDeckManager {
    private static String ID = makeID(RedMistDeck.class.getSimpleName());
    public RedMistDeck() { super(ID); }

    public void initialize() {
        addRelic(new RedMistInfoRelic());
        addCardToList(new CHR_UpstandingSlash());
        addCardToList(new CHR_UpstandingSlash());
        addCardToList(new CHR_UpstandingSlash());
        addCardToList(new CHR_LevelSlash());
        addCardToList(new CHR_LevelSlash());
        addCardToList(new CHR_LevelSlash());
        addCardToList(new CHR_Spear());
        addCardToList(new CHR_Spear());
        addCardToList(new CHR_Spear());
        addCardToList(new CHR_GreaterSplitVertical());
        // onrush? maybe.
    }
}
