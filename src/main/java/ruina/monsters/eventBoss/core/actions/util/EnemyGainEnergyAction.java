package ruina.monsters.eventBoss.core.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import ruina.monsters.AbstractRuinaCardMonster;

public class EnemyGainEnergyAction extends AbstractGameAction {
    private int energyGain;
    private AbstractRuinaCardMonster boss;

    public EnemyGainEnergyAction(final int amount) {
        this(AbstractRuinaCardMonster.boss, amount);
    }

    public EnemyGainEnergyAction(final AbstractRuinaCardMonster target, final int amount) {
        this.setValues(target, target, 0);
        this.duration = Settings.ACTION_DUR_FAST;
        this.energyGain = amount;
        this.boss = target;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractRuinaCardMonster.boss != null){
                this.boss = AbstractRuinaCardMonster.boss;
                this.boss.gainEnergy(this.energyGain);
                for (final AbstractCard c : this.boss.hand.group) { c.triggerOnGainEnergy(this.energyGain, true); }
            }
            else  {
                this.isDone = true;
                return;
            }
        }
        this.tickDuration();
    }
}