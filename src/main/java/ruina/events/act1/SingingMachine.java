package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.helpers.PotionHelper.getPotionsByRarity;
import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class SingingMachine extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(SingingMachine.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("SingingMachine.png");

    private AbstractRelic relic;
    private static final int HP_LOSS = 10;
    private static final int MAX_HP_LOSS = 4;
    private static final int NUM_POTIONS = 2;
    private AbstractCard offeredCard;
    private boolean isMad;
    private boolean alreadyUpgraded;
    private AbstractPotion.PotionRarity potionRewardRarity;

    private int screenNum = 0;

    public SingingMachine() {
        super(NAME, DESCRIPTIONS[0], IMG);
        noCardsInRewards = true;
        if (hasValidCards()) {
            imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            imageEventText.setDialogOption(OPTIONS[10], true);
        }
        imageEventText.setDialogOption(OPTIONS[9]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        CardGroup validCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard card : adp().masterDeck.getPurgeableCards().group) {
                            if (card.rarity != AbstractCard.CardRarity.BASIC) {
                                validCards.addToBottom(card);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(validCards, 1, OPTIONS[11], false, false, false, true);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[8]);
                        screenNum = 2;
                        this.imageEventText.updateDialogOption(0, OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            case 1:
                screenNum = 2;
                this.imageEventText.updateDialogOption(0, OPTIONS[9]);
                this.imageEventText.clearRemainingOptions();
                switch (buttonPressed) {
                    case 0:
                        if (isMad) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[6]);
                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        }
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(offeredCard, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        if (alreadyUpgraded) {
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(offeredCard.makeStatEquivalentCopy(), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        }
                        break;
                    case 1:
                        if (isMad) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                            CardCrawlGame.sound.play("BLUNT_FAST");
                            adp().damage(new DamageInfo(null, HP_LOSS));
                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        }
                        break;
                    case 2:
                        if (isMad) {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                            CardCrawlGame.sound.play("BLUNT_FAST");
                            adp().decreaseMaxHealth(MAX_HP_LOSS);
                        } else {
                            this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                            AbstractDungeon.getCurrRoom().rewards.clear();
                            ArrayList<AbstractPotion> validPotions  = getPotionsByRarity(potionRewardRarity);
                            Collections.shuffle(validPotions, AbstractDungeon.eventRng.random);
                            for (int i = 0; i < NUM_POTIONS; i++) {
                                if (i < validPotions.size()) {
                                    AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(validPotions.get(i)));
                                }
                            }
                            AbstractDungeon.combatRewardScreen.open();
                        }
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public static boolean hasValidCards() {
        for (AbstractCard card : adp().masterDeck.getPurgeableCards().group) {
            if (card.rarity != AbstractCard.CardRarity.BASIC) {
                return true;
            }
        }
        return false;
    }

    private void setDialog() {
        screenNum = 1;
        this.imageEventText.clearAllDialogs();
        if (isMad) {
            this.imageEventText.updateBodyText(DESCRIPTIONS[5]);
            imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[1] + offeredCard.name + OPTIONS[2], "r"), offeredCard.makeStatEquivalentCopy());
            imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[6] + HP_LOSS + OPTIONS[7], "r"));
            imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[6] + MAX_HP_LOSS + OPTIONS[8], "r"));
        } else {
            this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
            if (alreadyUpgraded) {
                imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[1] + offeredCard.name + OPTIONS[12], "g"), offeredCard.makeStatEquivalentCopy());
            } else {
                offeredCard.upgrade();
                imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[1] + offeredCard.name + OPTIONS[2], "g"), offeredCard.makeStatEquivalentCopy());
            }
            String potionRarity;
            if (offeredCard.rarity == AbstractCard.CardRarity.RARE) {
                relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE);
                potionRarity = OPTIONS[5];
                potionRewardRarity = AbstractPotion.PotionRarity.RARE;

            } else if (offeredCard.rarity == AbstractCard.CardRarity.UNCOMMON) {
                relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);
                potionRarity = OPTIONS[4];
                potionRewardRarity = AbstractPotion.PotionRarity.UNCOMMON;
            } else {
                relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON);
                potionRarity = OPTIONS[3];
                potionRewardRarity = AbstractPotion.PotionRarity.COMMON;
            }
            imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[1] + relic.name + OPTIONS[2], "g"), relic);
            imageEventText.setDialogOption(FontHelper.colorString(OPTIONS[1] + NUM_POTIONS + potionRarity, "g"));
        }
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                adp().masterDeck.removeCard(c);
                offeredCard = c.makeStatEquivalentCopy();
                if (c.color == AbstractCard.CardColor.CURSE || c.type == AbstractCard.CardType.STATUS) {
                    isMad = true;
                }
                if (c.upgraded) {
                    alreadyUpgraded = true;
                }
                setDialog();
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}
