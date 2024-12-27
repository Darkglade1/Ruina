package ruina.powers.act5;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

public class Orlando extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Orlando.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    protected final int cardsPerTurn;

    public Orlando(AbstractCreature owner, int cardsPerTurn) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.cardsPerTurn = cardsPerTurn;
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= cardsPerTurn) {
            flash();
            this.amount = 0;
            if (!owner.hasPower(StunMonsterPower.POWER_ID) && owner instanceof AbstractMonster) {
                ((AbstractMonster) owner).takeTurn();
            }
        } else {
            flashWithoutSound();
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + cardsPerTurn + POWER_DESCRIPTIONS[1];
    }
}
