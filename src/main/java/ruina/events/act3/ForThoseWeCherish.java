package ruina.events.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import ruina.RuinaMod;
import ruina.relics.LowellsGift;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class ForThoseWeCherish extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(ForThoseWeCherish.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Lowell.png");

    private static final int GOLD_COST = 120;
    AbstractRelic relic = new LowellsGift();

    private int screenNum = 0;

    public ForThoseWeCherish() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (adp().gold >= GOLD_COST) {
            imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + GOLD_COST + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4], "g"), relic);
        } else {
            imageEventText.setDialogOption(OPTIONS[7], true);
        }
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                        this.imageEventText.clearRemainingOptions();
                        adp().loseGold(GOLD_COST);
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                        this.imageEventText.clearRemainingOptions();
                        if (!AbstractDungeon.player.masterDeck.getUpgradableCards().isEmpty()) {
                            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[8], true, false, false, false);
                        }
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}
