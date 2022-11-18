package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.potions.EgoPotion;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Art extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Art.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Netzach.png");

    private static final int NUM_POTIONS = 2;
    private static final int RANDOM_EGO = 6;
    private static final int NORMAL_EGO = 7;
    private static final int UPGRADED_EGO = 10;
    private AbstractPotion potion;
    private AbstractPotion egoPotion = PotionHelper.getPotion(EgoPotion.POTION_ID);
    private int egoOption;

    private int screenNum = 0;

    public Art() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + NUM_POTIONS + " " + egoPotion.name + OPTIONS[3], "g"));
        this.potion = adp().getRandomPotion();
        if (potion != null) {
            if (potion.rarity == AbstractPotion.PotionRarity.COMMON) {
                egoOption = RANDOM_EGO;
            } else if (potion.rarity == AbstractPotion.PotionRarity.UNCOMMON) {
                egoOption = NORMAL_EGO;
            } else {
                egoOption = UPGRADED_EGO;
            }
            imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + potion.name + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[egoOption], "g"));
        } else {
            imageEventText.setDialogOption(OPTIONS[9], true);
        }
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
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        for (int i = 0; i < NUM_POTIONS; i++) {
                            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getPotion(egoPotion.ID)));
                        }
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.player.removePotion(this.potion);
                        switch (egoOption) {
                            case RANDOM_EGO:
                                AbstractCard card = AbstractEgoCard.getRandomEgoCards(1).get(0);
                                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                                break;
                            case NORMAL_EGO:
                                RewardItem reward = new RewardItem();
                                reward.cards = AbstractEgoCard.getRandomEgoCards(reward.cards.size());
                                for (AbstractCard c : reward.cards) {
                                    UnlockTracker.markCardAsSeen(c.cardID);
                                    for (AbstractRelic relic : adp().relics) {
                                        relic.onPreviewObtainCard(c);
                                    }
                                }
                                AbstractDungeon.getCurrRoom().rewards.clear();
                                AbstractDungeon.getCurrRoom().addCardReward(reward);
                                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                                AbstractDungeon.combatRewardScreen.open();
                                break;
                            case UPGRADED_EGO:
                                RewardItem upgradedReward = new RewardItem();
                                upgradedReward.cards = AbstractEgoCard.getRandomEgoCards(upgradedReward.cards.size());
                                for (AbstractCard c : upgradedReward.cards) {
                                    UnlockTracker.markCardAsSeen(c.cardID);
                                    c.upgrade();
                                    for (AbstractRelic relic : adp().relics) {
                                        relic.onPreviewObtainCard(c);
                                    }
                                }
                                AbstractDungeon.getCurrRoom().rewards.clear();
                                AbstractDungeon.getCurrRoom().addCardReward(upgradedReward);
                                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                                AbstractDungeon.combatRewardScreen.open();
                                break;
                        }
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
