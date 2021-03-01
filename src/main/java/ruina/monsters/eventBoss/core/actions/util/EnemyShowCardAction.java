package ruina.monsters.eventBoss.core.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.monsters.AbstractRuinaCardMonster;

public class EnemyShowCardAction extends AbstractGameAction {
    private static final float PURGE_DURATION = 0.2f;
    private AbstractCard card;

    public EnemyShowCardAction(final AbstractCard card) {
        this.card = null;
        this.setValues(AbstractRuinaCardMonster.boss, null, 1);
        this.card = card;
        this.duration = 0.2f;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        if (this.duration == 0.2f) {
            if (AbstractRuinaCardMonster.boss.limbo.contains(this.card)) {
                AbstractRuinaCardMonster.boss.limbo.removeCard(this.card);
            }
            AbstractRuinaCardMonster.boss.cardInUse = null;
        }
        this.tickDuration();
    }
}