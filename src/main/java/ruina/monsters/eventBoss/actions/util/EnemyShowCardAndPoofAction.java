package ruina.monsters.eventBoss.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import ruina.monsters.AbstractRuinaCardMonster;

public class EnemyShowCardAndPoofAction extends AbstractGameAction {
    private static final float PURGE_DURATION = 0.2f;
    private AbstractCard card;

    public EnemyShowCardAndPoofAction(final AbstractCard card) {
        this.card = null;
        this.setValues(AbstractRuinaCardMonster.boss, null, 1);
        this.card = card;
        this.duration = 0.2f;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        if (this.duration == 0.2f) {
            AbstractDungeon.effectList.add(new ExhaustCardEffect(this.card));
            if (AbstractRuinaCardMonster.boss.limbo.contains(this.card)) { AbstractRuinaCardMonster.boss.limbo.removeCard(this.card); }
            AbstractRuinaCardMonster.boss.cardInUse = null;
        }
        this.tickDuration();
    }
}