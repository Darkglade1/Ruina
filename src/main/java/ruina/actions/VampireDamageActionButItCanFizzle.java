package ruina.actions;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import spireTogether.patches.combatsync.ActionPatches;

public class VampireDamageActionButItCanFizzle extends VampireDamageAction {
    private DamageInfo info;

    public VampireDamageActionButItCanFizzle(AbstractCreature target, DamageInfo info, AttackEffect effect) {
        super(target, info, effect);
        this.info = info;
        if (RuinaMod.isMultiplayerConnected()) {
            //ActionPatches.markActionForNoDataSync(this);
            //ActionPatches.markActionForNoDamageSync(this);
        }
    }

    public void update() {
        if (info.owner.isDeadOrEscaped() || target.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
            if (this.isDone) {
                if (RuinaMod.isMultiplayerConnected() && target instanceof AbstractMonster) {
                    this.addToTop(new HealAction(this.source, this.source, info.output));
                    this.addToTop(new WaitAction(0.1F));
                    if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                        AbstractDungeon.actionManager.clearPostCombatActions();
                    }
                }
            }
        }
    }
}
