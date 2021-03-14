package ruina.events.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;

import static ruina.RuinaMod.makeEventPath;

public class DistortedYan extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(DistortedYan.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("DistortedYan.png");

    private static final int GOLD_REWARD = 50;

    private int screenNum = 0;

    public DistortedYan() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "r") + " " + FontHelper.colorString(OPTIONS[3], "g"));
        imageEventText.setDialogOption(OPTIONS[1]);
        noCardsInRewards = true;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new yanDistortion());
                        RewardItem reward = new RewardItem();
                        reward.cards = AbstractEgoCard.getRandomEgoCards(reward.cards.size());
                        for (AbstractCard c : reward.cards) {
                            UnlockTracker.markCardAsSeen(c.cardID);
                            c.upgrade();
                        }
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addCardReward(reward);
                        AbstractDungeon.getCurrRoom().addGoldToRewards(GOLD_REWARD);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
                        this.enterCombatFromImage();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
