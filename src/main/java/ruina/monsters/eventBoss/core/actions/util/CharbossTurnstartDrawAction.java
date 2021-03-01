package ruina.monsters.eventBoss.core.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import ruina.monsters.AbstractRuinaCardMonster;

public class CharbossTurnstartDrawAction extends AbstractGameAction {

    @Override
    public void update() {
        if (AbstractRuinaCardMonster.boss != null) AbstractRuinaCardMonster.boss.endTurnStartTurn();
        this.isDone = true;
    }

}