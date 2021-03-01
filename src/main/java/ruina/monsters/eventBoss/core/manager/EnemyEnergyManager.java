package ruina.monsters.eventBoss.core.manager;


import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.ui.EnemyEnergyPanel;

public class EnemyEnergyManager extends EnergyManager {

    public EnemyEnergyManager(int e) {
        super(e);
    }
    public void prep() {
        this.energy = this.energyMaster;
        EnemyEnergyPanel.totalCount = 0;
    }
    public void recharge() {
        if (AbstractRuinaCardMonster.boss != null) {
            if (AbstractRuinaCardMonster.boss.hasRelic("Art of War")) {
                AbstractRuinaCardMonster.boss.getRelic("Art of War").onTrigger();
            }
            if (AbstractRuinaCardMonster.boss.hasRelic("Ice Cream")) {
                if (EnemyEnergyPanel.totalCount > 0) {
                    AbstractRuinaCardMonster.boss.getRelic("Ice Cream").flash();
                    AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractRuinaCardMonster.boss, AbstractRuinaCardMonster.boss.getRelic("Ice Cream")));
                }
                EnemyEnergyPanel.addEnergy(this.energy);
            } else { EnemyEnergyPanel.setEnergy(this.energy); }
        }
    }
    public void use(final int e) { EnemyEnergyPanel.useEnergy(e); }
}