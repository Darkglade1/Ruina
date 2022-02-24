package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

import static ruina.util.Wiz.att;

public class FlexibleDiscoveryAction extends AbstractGameAction {
    private boolean retrieveCard = false;
    private ArrayList<AbstractCard> cards;
    private boolean costsZero;

    public FlexibleDiscoveryAction(ArrayList<AbstractCard> cards, int amount, boolean costsZero) {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.cards = cards;
        this.amount = amount;
        this.costsZero = costsZero;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.discoveryOpen();
            AbstractDungeon.cardRewardScreen.rewardGroup = cards;

            for (AbstractCard tmp : AbstractDungeon.cardRewardScreen.rewardGroup) {
                UnlockTracker.markCardAsSeen(tmp.cardID);
            }
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
                        disCard.upgrade();
                    }
                    if (costsZero) {
                        disCard.setCostForTurn(0);
                    }
                    disCard.current_x = -1000.0F * Settings.scale;
                    att(new MakeTempCardInHandAction(disCard, amount));
                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                }

                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }
}
