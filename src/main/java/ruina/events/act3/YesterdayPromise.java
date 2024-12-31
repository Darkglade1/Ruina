package ruina.events.act3;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.RuinaMod;
import ruina.relics.YesterdayPromiseRelic;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class YesterdayPromise extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(YesterdayPromise.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Pluto.png");

    private int screenNum = 0;
    private final AbstractRelic reward = new YesterdayPromiseRelic();
    private static final float MAX_HP_COST = 0.05F;
    private static final float HIGH_ASC_MAX_HP_COST = 0.07F;
    private final int maxHPCost;
    public YesterdayPromise() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            maxHPCost = (int)(HIGH_ASC_MAX_HP_COST * adp().maxHealth);
        } else {
            maxHPCost = (int)(MAX_HP_COST * adp().maxHealth);
        }
        imageEventText.setDialogOption(String.format(OPTIONS[0], maxHPCost), reward);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        imageEventText.setDialogOption(OPTIONS[2]);
                        screenNum = 1;
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().decreaseMaxHealth(maxHPCost);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, reward);
                        break;
                    case 1:
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        imageEventText.setDialogOption(OPTIONS[2]);
                        screenNum = 1;
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}