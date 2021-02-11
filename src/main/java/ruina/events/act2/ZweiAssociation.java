package ruina.events.act2;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class ZweiAssociation extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(ZweiAssociation.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Zwei.png");

    private int maxHpLoss;
    private static final float MAX_HP_LOSS = 0.08F;
    private static final float A15_MAX_HP_LOSS = 0.10F;

    private static final int GOLD_COST = 100;

    private int screenNum = 0;

    public ZweiAssociation() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.maxHpLoss = (int)((float)adp().maxHealth * A15_MAX_HP_LOSS);
        } else {
            this.maxHpLoss = (int)((float)adp().maxHealth * MAX_HP_LOSS);
        }
        if (adp().gold >= GOLD_COST) {
            imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + GOLD_COST + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[5], "g"));
        } else {
            imageEventText.setDialogOption(OPTIONS[9], true);
        }
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + maxHpLoss + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[6], "g"));
        imageEventText.setDialogOption(OPTIONS[7]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // Rare card
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1] + DESCRIPTIONS[4]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        adp().loseGold(GOLD_COST);
                        RewardItem reward = new RewardItem();
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for(int i = 0; i < reward.cards.size(); ++i) {
                            AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE).makeCopy();
                            boolean containsDupe = true;
                            while(containsDupe) {
                                containsDupe = false;
                                for (AbstractCard c : group.group) {
                                    if (c.cardID.equals(card.cardID)) {
                                        containsDupe = true;
                                        card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE).makeCopy();
                                        break;
                                    }
                                }
                            }
                            group.addToBottom(card);
                        }

                        for (AbstractCard c : group.group) {
                            UnlockTracker.markCardAsSeen(c.cardID);
                            for (AbstractRelic relic : adp().relics) {
                                relic.onPreviewObtainCard(c);
                            }
                        }
                        reward.cards = group.group;
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addCardReward(reward);
                        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                        AbstractDungeon.combatRewardScreen.open();
                        break;
                    case 1: // EGO card
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2] + DESCRIPTIONS[4]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().decreaseMaxHealth(this.maxHpLoss);
                        reward = new RewardItem();
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
                    case 2: // Nothing
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3] + DESCRIPTIONS[4]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
