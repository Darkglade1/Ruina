package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import ruina.monsters.act1.singingMachine.SingingMachine;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.makeInHand;

public class GrindingGearsAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private AbstractPlayer p;
    private SingingMachine machine;

    public GrindingGearsAction(int amount, SingingMachine machine) {
        this.p = AbstractDungeon.player;
        this.setValues(this.p, AbstractDungeon.player, amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
        this.machine = machine;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            CardGroup cardGroup = new CardGroup(CardGroupType.UNSPECIFIED);
            for (AbstractCard c : machine.machineCards) {
                cardGroup.addToRandomSpot(c);
            }

            if (cardGroup.size() == 0) {
                this.isDone = true;
            } else if (cardGroup.size() == amount) {
                for (AbstractCard c : cardGroup.group) {
                    makeInHand(c, 1);
                    machine.machineCards.remove(c);
                }
                this.isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(cardGroup, 1, TEXT[0], false);
                this.tickDuration();
            }
        } else {
            if (AbstractDungeon.gridSelectScreen.selectedCards.size() == amount) {
                for (AbstractCard chosenCard : AbstractDungeon.gridSelectScreen.selectedCards) {
                    makeInHand(chosenCard, 1);
                    machine.machineCards.remove(chosenCard);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.tickDuration();
        }
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("GrindingGearsAction"));
        TEXT = uiStrings.TEXT;
    }
}
