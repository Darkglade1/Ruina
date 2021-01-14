package ruina.actions;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ApplyPowerActionButItCanFizzle extends ApplyPowerAction {

    public ApplyPowerActionButItCanFizzle(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast, AttackEffect effect) {
        super(target, source, powerToApply, stackAmount, isFast, effect);
    }

    public ApplyPowerActionButItCanFizzle(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, boolean isFast) {
        this(target, source, powerToApply, stackAmount, isFast, AttackEffect.NONE);
    }

    public ApplyPowerActionButItCanFizzle(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply) {
        this(target, source, powerToApply, powerToApply.amount);
    }

    public ApplyPowerActionButItCanFizzle(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount) {
        this(target, source, powerToApply, stackAmount, false, AttackEffect.NONE);
    }

    public ApplyPowerActionButItCanFizzle(AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount, AttackEffect effect) {
        this(target, source, powerToApply, stackAmount, false, effect);
    }

    public void update() {
        if (source != null && source.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
        }
    }
}


