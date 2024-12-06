package ruina.powers.act5;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class SingularityT extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityT.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int degradedThreshold = 15;
    private int unlockedThreshold = 6;

    private int currentThreshold;

    public SingularityT(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.currentThreshold = degradedThreshold;
        this.name = DESCRIPTIONS[2];
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= currentThreshold) {
            flash();
            this.amount = 0;
            if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                if (owner instanceof AbstractMonster) {
                    ((AbstractMonster) owner).takeTurn();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            ((AbstractMonster) owner).createIntent();
                            this.isDone = true;
                        }
                    });
                }
            } else {
                addToBot(new ReducePowerAction(owner, owner, StunMonsterPower.POWER_ID, 1));
            }
        } else {
            flashWithoutSound();
        }
    }

    public void unlock() {
        currentThreshold = unlockedThreshold;
        if (this.amount >= currentThreshold) {
            this.amount = currentThreshold - 1;
        }
        this.name = NAME;
        updateDescription();
        flash();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + currentThreshold + DESCRIPTIONS[1];
    }
}