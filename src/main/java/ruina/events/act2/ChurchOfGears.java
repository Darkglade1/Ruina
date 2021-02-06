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
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import ruina.RuinaMod;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class ChurchOfGears extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(ChurchOfGears.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Church.png");

    private int screenNum = 0;

    private static final float DUPE_HP_COST = 0.30f;
    private static final float HIGH_ASC_DUPE_HP_COST = 0.40f;
    private int dupeHpCost;

    private static final float TRANSFORM_HP_COST = 0.10f;
    private static final float HIGH_ASC_TRANSFORM_HP_COST = 0.15f;
    private int transformHpCost;

    private boolean upgradeThenDupe = false;
    private boolean transformThenUpgrade = false;

    public ChurchOfGears() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            dupeHpCost = (int)(HIGH_ASC_DUPE_HP_COST * adp().maxHealth);
            transformHpCost = (int)(HIGH_ASC_TRANSFORM_HP_COST * adp().maxHealth);
        } else {
            dupeHpCost = (int)(DUPE_HP_COST * adp().maxHealth);
            transformHpCost = (int)(TRANSFORM_HP_COST * adp().maxHealth);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[1] + dupeHpCost + OPTIONS[2], "r") + " " + FontHelper.colorString(OPTIONS[3], "g"));
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[1] + transformHpCost + OPTIONS[2], "r") + " " + FontHelper.colorString(OPTIONS[4], "g"));
        imageEventText.setDialogOption(OPTIONS[7]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Upgrade then Dupe
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, dupeHpCost));
                        dupe();
                        break;
                    case 1: // Transform then Upgrade
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, transformHpCost));
                        transform();
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (transformThenUpgrade) {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
                this.transformThenUpgrade = false;
                float displayCount = 0.0F;
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                    card.untip();
                    card.unhover();
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.transformCard(card, true, AbstractDungeon.miscRng);
                    AbstractCard c = AbstractDungeon.getTransformedCard();
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && c != null) {
                        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 3.0F + displayCount, (float)Settings.HEIGHT / 2.0F, false));
                        displayCount += (float)Settings.WIDTH / 6.0F;
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
            }
        }
        if (upgradeThenDupe) {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
                this.upgradeThenDupe = false;
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                c.upgrade();
                adp().bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractCard dupe = c.makeStatEquivalentCopy();
                dupe.inBottleFlame = false;
                dupe.inBottleLightning = false;
                dupe.inBottleTornado = false;
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(dupe, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    private void dupe() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        upgradeThenDupe = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[5], true, false, false, false);
    }

    private void transform() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        transformThenUpgrade = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[6], false, true, false, false);
    }
}
