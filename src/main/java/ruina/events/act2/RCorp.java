package ruina.events.act2;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import ruina.RuinaMod;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class RCorp extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(RCorp.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("RCorp.png");

    private int screenNum = 0;

    private static final float HP_COST = 0.10f;
    private static final float HIGH_ASC_HP_COST = 0.15f;
    private int hpCost;

    private boolean upgradeCard = false;
    private boolean removeCard = false;

    public RCorp() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            hpCost = (int)(HIGH_ASC_HP_COST * adp().maxHealth);
        } else {
            hpCost = (int)(HP_COST * adp().maxHealth);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g") + " " + FontHelper.colorString(OPTIONS[3] + hpCost + OPTIONS[4], "r"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + DESCRIPTIONS[3]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        upgrade();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2] + DESCRIPTIONS[3]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        remove();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (upgradeCard) {
            int numUpgrades = 0;
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                this.upgradeCard = false;
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    c.upgrade();
                    adp().bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    c.stopGlowing();
                    numUpgrades++;
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                if (numUpgrades > 0) {
                    CardCrawlGame.sound.play("BLUNT_FAST");
                    adp().damage(new DamageInfo(null, hpCost * numUpgrades));
                }
            }
        }
        if (removeCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                this.removeCard = false;
                for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                    AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                    adp().masterDeck.removeCard(c);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    private void upgrade() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        upgradeCard = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 99,true, OPTIONS[6]);
    }

    private void remove() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        removeCard = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[7], false, false, false, true);
    }
}
