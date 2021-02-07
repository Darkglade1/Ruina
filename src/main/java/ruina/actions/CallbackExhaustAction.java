package ruina.actions;

import ruina.patches.ExhaustCardHook;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CallbackExhaustAction extends ExhaustAction {
    private Consumer<ArrayList<AbstractCard>> callback;

    public CallbackExhaustAction(int numCards, boolean isRandom, boolean anyNumber, boolean canPickZero, Consumer<ArrayList<AbstractCard>> callback) {
        super(numCards, isRandom, anyNumber, canPickZero);
        this.callback = callback;
    }

    @Override
    public void update() {
        ExhaustCardHook.callback = callback;
        super.update();
        ExhaustCardHook.callback = null;
    }
}