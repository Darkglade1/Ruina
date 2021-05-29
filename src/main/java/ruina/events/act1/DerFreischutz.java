package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import ruina.RuinaMod;
import ruina.relics.SeventhBullet;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class DerFreischutz extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(DerFreischutz.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("MagicBullet.png");

    private AbstractRelic relic = new SeventhBullet();
    private static final float HP_LOSS_PERCENT = 0.10f;
    private static final float HIGH_ASC_HP_LOSS_PERCENT = 0.12f;
    private final int hpLoss;

    private int screenNum = 0;

    public DerFreischutz() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            hpLoss = (int)(adp().maxHealth * HIGH_ASC_HP_LOSS_PERCENT);
        } else {
            hpLoss = (int)(adp().maxHealth * HP_LOSS_PERCENT);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"), relic);
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[3] + hpLoss + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[5],"g"));
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
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, hpLoss));
                        AbstractDungeon.gridSelectScreen.open(adp().masterDeck.getPurgeableCards(), 1, OPTIONS[6], false, false, false, true);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                adp().masterDeck.removeCard(c);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}
