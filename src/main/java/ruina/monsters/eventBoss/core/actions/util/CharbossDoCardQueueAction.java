package ruina.monsters.eventBoss.core.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.monsters.AbstractRuinaCardMonster;

public class CharbossDoCardQueueAction extends AbstractGameAction {

    private AbstractCard c;

    public CharbossDoCardQueueAction(AbstractCard c) {
        super();
        this.c = c;
    }

    @Override
    public void update() {
        if (AbstractRuinaCardMonster.boss != null) { AbstractRuinaCardMonster.boss.useCard(c, AbstractRuinaCardMonster.boss, c.energyOnUse); }
        this.isDone = true;
    }

}