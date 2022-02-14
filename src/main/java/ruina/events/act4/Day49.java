package ruina.events.act4;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import ruina.RuinaMod;
import ruina.relics.Book;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Day49 extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Day49.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Ensemble.png");

    AbstractRelic relic = new Book();

    private int screenNum = 0;

    public Day49() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0], relic);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        screenNum = 1;
                        if (adp().hasRelic(relic.relicId)) {relic = new Circlet();}
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        openMap();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
