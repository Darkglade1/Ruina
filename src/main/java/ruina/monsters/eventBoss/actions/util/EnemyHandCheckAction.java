package ruina.monsters.eventBoss.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import ruina.monsters.AbstractRuinaCardMonster;

public class EnemyHandCheckAction extends AbstractGameAction {
    private AbstractRuinaCardMonster player;

    public EnemyHandCheckAction() {
        this.player = AbstractRuinaCardMonster.boss;
    }

    @Override
    public void update() {
        this.player.hand.applyPowers();
        this.player.hand.glowCheck();
        this.isDone = true;
    }
}