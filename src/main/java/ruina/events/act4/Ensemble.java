package ruina.events.act4;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import ruina.RuinaMod;
import ruina.relics.Book;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Ensemble extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Ensemble.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Ensemble.png");

    AbstractRelic relic = new Book();

    private int screenNum = 0;

    public Ensemble() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0], relic);
        // add trigger later. (Extra Content.)
        imageEventText.setDialogOption(OPTIONS[2], relic);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[1]);
                        this.imageEventText.clearRemainingOptions();
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        Class clazz = CardCrawlGame.dungeon.getClass();
                        Method retrieveItems = null;
                        try {
                            retrieveItems = clazz.getDeclaredMethod("makeAltMap");
                            retrieveItems.invoke(clazz.newInstance());
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) { e.printStackTrace(); }
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
