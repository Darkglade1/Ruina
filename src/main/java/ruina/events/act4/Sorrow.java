package ruina.events.act4;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.CeramicFish;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.DarkstonePeriapt;
import com.megacrit.cardcrawl.relics.JuzuBracelet;
import com.megacrit.cardcrawl.relics.Matryoshka;
import com.megacrit.cardcrawl.relics.MawBank;
import com.megacrit.cardcrawl.relics.MealTicket;
import com.megacrit.cardcrawl.relics.OldCoin;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.relics.TinyChest;
import com.megacrit.cardcrawl.relics.WingBoots;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import ruina.RuinaMod;

import java.util.ArrayList;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Sorrow extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Sorrow.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Sorrow.png");

    private int screenNum = 1;

    private boolean upgradeCard = false;
    private boolean removeCard = false;
    private boolean transformCard = false;

    private AbstractRelic relic1;
    private AbstractRelic relic2;

    private final ArrayList<String> blacklistedRelics = new ArrayList<>();

    public Sorrow() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"));
        imageEventText.setDialogOption(OPTIONS[0]);

        blacklistedRelics.add(WingBoots.ID);
        blacklistedRelics.add(PreservedInsect.ID);
        blacklistedRelics.add(DarkstonePeriapt.ID);
        blacklistedRelics.add(MealTicket.ID);
        blacklistedRelics.add(Courier.ID);
        blacklistedRelics.add(TinyChest.ID);
        blacklistedRelics.add(SmilingMask.ID);
        blacklistedRelics.add(JuzuBracelet.ID);
        blacklistedRelics.add(Omamori.ID);
        blacklistedRelics.add(Matryoshka.ID);
        blacklistedRelics.add(CeramicFish.ID);
        blacklistedRelics.add(MawBank.ID);
        blacklistedRelics.add(OldCoin.ID);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        transform();
                        break;
                    case 1:
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        upgrade();
                        break;
                    case 1:
                        break;
                }
                break;
            case 2:
                switch (buttonPressed) {
                    case 0:
                        remove();
                        break;
                    case 1:
                        break;
                }
                break;
            case 3:
            case 4:
            case 5:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic1);
                        break;
                    case 1:
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic2);
                        break;
                }
                break;
            default:
                this.openMap();
        }
        progress();
    }

    private void progress() {
        screenNum++;
        this.imageEventText.clearAllDialogs();
        switch (screenNum) {
            case 1:
                //remove transform part
//                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
//                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"));
//                imageEventText.setDialogOption(OPTIONS[0]);
                break;
            case 2:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[3], "g"));
                imageEventText.setDialogOption(OPTIONS[0]);
                break;
            case 3:
                relic1 = getValidRelic(AbstractRelic.RelicTier.COMMON);
                relic2 = getValidRelic(AbstractRelic.RelicTier.COMMON);
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic1.name + OPTIONS[5], "g"), relic1);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic2.name + OPTIONS[5], "g"), relic2);
                break;
            case 4:
                relic1 = getValidRelic(AbstractRelic.RelicTier.UNCOMMON);
                relic2 = getValidRelic(AbstractRelic.RelicTier.UNCOMMON);
                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic1.name + OPTIONS[5], "g"), relic1);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic2.name + OPTIONS[5], "g"), relic2);
                break;
            case 5:
                relic1 = getValidRelic(AbstractRelic.RelicTier.RARE);
                relic2 = getValidRelic(AbstractRelic.RelicTier.RARE);
                this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic1.name + OPTIONS[5], "g"), relic1);
                imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[4] + relic2.name + OPTIONS[5], "g"), relic2);
                break;
            default:
                imageEventText.setDialogOption(OPTIONS[0]);
                break;
        }
    }

    private AbstractRelic getValidRelic(AbstractRelic.RelicTier relicTier) {
        AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(relicTier);
        while (blacklistedRelics.contains(relic.relicId)) {
            System.out.println("Rerolled " + relic.relicId);
            relic = AbstractDungeon.returnRandomScreenlessRelic(relicTier);
        }
        return relic;
    }

    public void update() {
        super.update();
        if (transformCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                transformCard = false;
                float displayCount = 0.0F;
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                    card.untip();
                    card.unhover();
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng);
                    AbstractCard c = AbstractDungeon.getTransformedCard();
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM && c != null) {
                        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 3.0F + displayCount, (float)Settings.HEIGHT / 2.0F, false));
                        displayCount += (float)Settings.WIDTH / 6.0F;
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
            }
        } else if (upgradeCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                this.upgradeCard = false;
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    c.upgrade();
                    adp().bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    c.stopGlowing();
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        } else if (removeCard) {
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

    private void transform() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        transformCard = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[6], false, true, false, false);
    }

    private void upgrade() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        if (AbstractDungeon.player.masterDeck.getUpgradableCards().size() > 0) {
            upgradeCard = true;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[7], true, false, false, false);
        }
    }

    private void remove() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        if (AbstractDungeon.player.masterDeck.getPurgeableCards().size() > 0) {
            removeCard = true;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[8], false, false, false, true);
        }
    }
}
