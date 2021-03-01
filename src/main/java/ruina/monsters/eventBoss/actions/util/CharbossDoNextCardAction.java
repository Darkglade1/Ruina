package ruina.monsters.eventBoss.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import ruina.monsters.AbstractRuinaCardMonster;

public class CharbossDoNextCardAction extends AbstractGameAction {

    @Override
    public void update() {
        if (AbstractRuinaCardMonster.boss != null) AbstractRuinaCardMonster.boss.makePlay();
        this.isDone = true;
    }

}