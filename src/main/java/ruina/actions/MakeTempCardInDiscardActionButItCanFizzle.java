package ruina.actions;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MakeTempCardInDiscardActionButItCanFizzle extends MakeTempCardInDiscardAction {
    private AbstractMonster source;

    public MakeTempCardInDiscardActionButItCanFizzle(AbstractCard card, int amount, AbstractMonster source) {
        super(card, amount);
        this.source = source;
    }

    public MakeTempCardInDiscardActionButItCanFizzle(AbstractCard card, boolean sameUUID, AbstractMonster source) {
        super(card, sameUUID);
        this.source = source;
    }

    public void update() {
        if (source != null && source.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
        }
    }
}
