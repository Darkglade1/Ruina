package ruina.actions;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import ruina.cardmods.FrozenMod;

import java.util.ArrayList;
import java.util.Iterator;

import static ruina.RuinaMod.makeID;

public class FreezeCardInHandAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private AbstractPlayer p;
    private int freezeAmount;
    private ArrayList<AbstractCard> cannotFreeze = new ArrayList();

    public FreezeCardInHandAction(AbstractCreature source, int amount) {
        this.setValues(AbstractDungeon.player, source, amount);// 26
        this.actionType = ActionType.DRAW;// 27
        this.duration = 0.25F;// 28
        this.p = AbstractDungeon.player;// 29
        this.freezeAmount = amount;// 30
    }// 31

    public void update() {
        Iterator var1;
        AbstractCard c;
        if (this.duration == Settings.ACTION_DUR_FAST) {// 36
            var1 = this.p.hand.group.iterator();// 38

            while(var1.hasNext()) {
                c = (AbstractCard)var1.next();
                if (!this.isFreezeable(c)) {// 39
                    this.cannotFreeze.add(c);// 40
                }
            }

            if (this.cannotFreeze.size() == this.p.hand.group.size()) {// 45
                this.isDone = true;// 46
                return;// 47
            }

            this.p.hand.group.removeAll(this.cannotFreeze);// 64
            if (this.p.hand.group.size() > 1) {// 66
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], freezeAmount, false, false, false, false);// 67
                this.tickDuration();// 68
                return;// 69
            }

            if (this.p.hand.group.size() == 1) {// 70
                CardModifierManager.addModifier(this.p.hand.getTopCard(), new FrozenMod());
                this.returnCards();// 74
                this.isDone = true;// 75
            }
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {// 80
            var1 = AbstractDungeon.handCardSelectScreen.selectedCards.group.iterator();// 81

            while(var1.hasNext()) {
                c = (AbstractCard)var1.next();
                CardModifierManager.addModifier(c, new FrozenMod());
                this.addToTop(new MakeTempCardInHandAction(c.makeStatEquivalentCopy()));
            }

            this.returnCards();// 88
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;// 90
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();// 91
            this.isDone = true;// 92
        }

        this.tickDuration();// 95
    }// 96

    private void returnCards() {
        Iterator var1 = this.cannotFreeze.iterator();// 99

        while(var1.hasNext()) {
            AbstractCard c = (AbstractCard)var1.next();
            this.p.hand.addToTop(c);// 100
        }

        this.p.hand.refreshHandLayout();// 102
    }// 103

    private boolean isFreezeable(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, FrozenMod.ID);
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("FrostSplinter"));// 16
        TEXT = uiStrings.TEXT;// 17
    }
}
