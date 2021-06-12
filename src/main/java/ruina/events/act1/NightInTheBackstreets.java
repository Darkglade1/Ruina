package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.relics.LifeFiber;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class NightInTheBackstreets extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(NightInTheBackstreets.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Sweeper.png");

    private AbstractRelic relic = new LifeFiber();
    private final AbstractCard curse = new Injury();
    private final float HEAL_PERCENT = 0.3f;
    private final float HIGH_ASC_HEAL_PERCENT = 0.25f;
    private int heal;
    private int screenNum = 0;

    public NightInTheBackstreets() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            heal = (int)(adp().maxHealth * HIGH_ASC_HEAL_PERCENT);
        } else {
            heal = (int)(adp().maxHealth * HEAL_PERCENT);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3] + curse.name + OPTIONS[4], "r"), relic);
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + heal + OPTIONS[6], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[7]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        adp().heal(this.heal, true);
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[7]);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

}
