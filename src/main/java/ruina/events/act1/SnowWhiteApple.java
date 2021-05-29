package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Parasite;
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
import ruina.relics.Malice;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class SnowWhiteApple extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(SnowWhiteApple.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("SnowWhite.png");

    private AbstractRelic relic = new Malice();
    private final AbstractCard curse = new Parasite();
    private static final int MAX_HP_GAIN = 13;
    private static final float MAX_HP_LOSS_PERCENT = 0.07f;
    private static final float HIGH_ASC_MAX_HP_LOSS_PERCENT = 0.09f;
    private final int maxHPLoss;

    private int screenNum = 0;

    public SnowWhiteApple() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            maxHPLoss = (int)(adp().maxHealth * HIGH_ASC_MAX_HP_LOSS_PERCENT);
        } else {
            maxHPLoss = (int)(adp().maxHealth * MAX_HP_LOSS_PERCENT);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[5] + MAX_HP_GAIN + OPTIONS[7], "g") + " " + FontHelper.colorString(OPTIONS[3] + curse.name + OPTIONS[4],"r"), curse);
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6] + maxHPLoss + OPTIONS[7], "r") + " " + FontHelper.colorString(OPTIONS[2],"g"), relic);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        adp().increaseMaxHp(MAX_HP_GAIN, true);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().decreaseMaxHealth(this.maxHPLoss);
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
