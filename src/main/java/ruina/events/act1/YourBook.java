package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.relics.EGOBook;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class YourBook extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(YourBook.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("YourBook.png");

    private AbstractRelic relic = new EGOBook();
    private final AbstractCard curse = new Writhe();
    private final AbstractCard card = new Metamorphosis();
    private static final float HP_LOSS_PERCENT = 0.20f;
    private static final float HIGH_ASC_HP_LOSS_PERCENT = 0.25f;
    private final int hpLoss;

    private int screenNum = 0;

    public YourBook() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            hpLoss = (int)(adp().maxHealth * HIGH_ASC_HP_LOSS_PERCENT);
        } else {
            hpLoss = (int)(adp().maxHealth * HP_LOSS_PERCENT);
        }
        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[2] + FontHelper.colorString(OPTIONS[4], "g") + " " + FontHelper.colorString(OPTIONS[5] + curse.name + OPTIONS[6],"r"), curse, relic);
                        imageEventText.setDialogOption(OPTIONS[3] + FontHelper.colorString(OPTIONS[7] + hpLoss + OPTIONS[8], "r") + " " + FontHelper.colorString(OPTIONS[9] + card.name + OPTIONS[6],"g"), card);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[10]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, hpLoss));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
