package ruina.powers.act5;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.powers.act4.BlackSilence;

import static ruina.util.Wiz.atb;

public class AllyOrlando extends Orlando {

    public AllyOrlando(AbstractCreature owner, int cardsPerTurn) {
        super(owner, cardsPerTurn);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= cardsPerTurn && owner instanceof AbstractMonster) {
            this.amount = 0;
            ((AbstractMonster) owner).takeTurn();
            AbstractPower power = owner.getPower(BlackSilence.POWER_ID);
            if (power != null) {
                power.atEndOfTurn(false);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    ((AbstractMonster) owner).createIntent();
                    this.isDone = true;
                }
            });
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    owner.halfDead = true;
                    this.isDone = true;
                }
            });
        }
    }
}
