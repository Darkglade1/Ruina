package ruina.actions;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MakeTempCardInDrawPileActionButItCanFizzle extends MakeTempCardInDrawPileAction {
    private AbstractMonster source;

    public MakeTempCardInDrawPileActionButItCanFizzle(AbstractCard card, int amount, boolean randomSpot, boolean autoPosition, boolean toBottom, float cardX, float cardY, AbstractMonster source) {
        super(card, amount, randomSpot, autoPosition, toBottom, cardX, cardY);
        this.source = source;
    }

    public MakeTempCardInDrawPileActionButItCanFizzle(AbstractCard card, int amount, boolean randomSpot, boolean autoPosition, boolean toBottom, AbstractMonster source) {
        this(card, amount, randomSpot, autoPosition, toBottom, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, source);
    }

    public MakeTempCardInDrawPileActionButItCanFizzle(AbstractCard card, int amount, boolean shuffleInto, boolean autoPosition, AbstractMonster source) {
        this(card, amount, shuffleInto, autoPosition, false, source);
    }

    public void update() {
        if (source != null && source.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
        }
    }
}
