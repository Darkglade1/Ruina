package ruina.events.act1;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.RuinaMod;
import ruina.relics.LifeFiber;

import static ruina.RuinaMod.makeEventPath;

public class NightInTheBackstreets extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(NightInTheBackstreets.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Sweeper.png");

    private AbstractRelic relic = new LifeFiber();
    private int screenNum = 0;

    public NightInTheBackstreets() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0], relic);
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
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

}
