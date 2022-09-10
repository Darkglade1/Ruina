package ruina.actions;

import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class VampireDamageActionButItCanFizzle extends VampireDamageAction {
    private DamageInfo info;

    public VampireDamageActionButItCanFizzle(AbstractCreature target, DamageInfo info, AttackEffect effect) {
        super(target, info, effect);
        this.info = info;
    }

    public void update() {
        if (info.owner.isDeadOrEscaped() || target.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
        }
    }
}
