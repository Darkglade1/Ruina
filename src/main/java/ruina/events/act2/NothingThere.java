package ruina.events.act2;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.relics.Goodbye;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class NothingThere extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(NothingThere.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("NothingThere.png");

    private AbstractRelic relic = new Goodbye();
    private final AbstractCard curse = new Writhe();
    private static final int MIN_GOLD = 80;
    private static final int MAX_GOLD = 120;
    private int gold;

    private int screenNum = 0;

    public NothingThere() {
        super(NAME, DESCRIPTIONS[0], IMG);
        gold = AbstractDungeon.eventRng.random(MIN_GOLD, MAX_GOLD);
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3] + curse.name + OPTIONS[4],"r"), curse, relic);
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + gold + OPTIONS[6], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.effectList.add(new RainingGoldEffect(gold));
                        adp().gainGold(gold);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
