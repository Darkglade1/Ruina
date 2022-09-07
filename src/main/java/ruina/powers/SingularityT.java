package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;
import ruina.RuinaMod;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class SingularityT extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SingularityT.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int degradedThreshold = 12;
    private int degradedRamp = 4;
    private int unlockedThreshold = 6;
    private int unlockedRamp = 3;
    private boolean unlocked = false;

    private int currentThreshold;
    private int currentRamp;

    public SingularityT(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.currentThreshold = degradedThreshold;
        this.currentRamp = degradedRamp;
        this.name = DESCRIPTIONS[3];
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.amount++;
        if (this.amount >= currentThreshold) {
            flash();
            this.amount = 0;
            currentThreshold -= currentRamp;
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
            updateDescription();
        } else {
            flashWithoutSound();
        }
    }

    public void unlock() {
        unlocked = true;
        currentThreshold = unlockedThreshold;
        currentRamp = unlockedRamp;
        if (this.amount >= currentThreshold) {
            this.amount = currentThreshold - 1;
        }
        this.name = NAME;
        updateDescription();
        flash();
        this.addToBot(new TextAboveCreatureAction(this.owner, NAME));
    }

    @Override
    public void atEndOfRound() {
        if (unlocked) {
            currentThreshold = unlockedThreshold;
        } else {
            currentThreshold = degradedThreshold;
        }
        this.amount = 0;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.amount2 = currentThreshold;
        if (currentThreshold <= 1) {
            this.description = DESCRIPTIONS[4];
        } else {
            this.description = DESCRIPTIONS[0] + currentThreshold + DESCRIPTIONS[1] + currentRamp + DESCRIPTIONS[2];
        }
    }
}