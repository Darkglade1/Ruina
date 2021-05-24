package ruina.actions;

import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

public class ChooseOneActionButItCanFizzle extends ChooseOneAction {

    public ChooseOneActionButItCanFizzle(ArrayList<AbstractCard> choices, AbstractCreature source) {
        super(choices);
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
