package ruina.events.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.RuinaMod;
import ruina.cardmods.ContractsMod;
import ruina.relics.YesterdayPromiseRelic;

import static ruina.RuinaMod.makeEventPath;

public class YesterdayPromise extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(YesterdayPromise.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Pluto.png");

    private int screenNum = 0;
    private AbstractRelic reward = new YesterdayPromiseRelic();
    private int damage;
    public YesterdayPromise() {
        super(NAME, DESCRIPTIONS[0], IMG);
        damage = (int) (AbstractDungeon.player.maxHealth * 0.35F);
        imageEventText.setDialogOption(String.format(OPTIONS[0], damage), reward);
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
                        AbstractDungeon.player.damage(new DamageInfo(null, this.damage));
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