package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class GalaxyChild extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(GalaxyChild.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("GalaxyChild.png");

    private final AbstractCard curse = new Decay();

    private int screenNum = 0;

    public GalaxyChild() {
        super(NAME, DESCRIPTIONS[0], IMG);
        noCardsInRewards = true;
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[3], "g") + " " + FontHelper.colorString(OPTIONS[4] + curse.name + OPTIONS[5],"r"), curse);
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
                        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON);
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, r);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                        this.imageEventText.clearRemainingOptions();
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
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
