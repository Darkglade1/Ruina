package ruina.actions;

import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.patches.ScryCardHook;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CallbackScryAction extends ScryAction {
    private Consumer<ArrayList<AbstractCard>> callback;

    public CallbackScryAction(int numCards, Consumer<ArrayList<AbstractCard>> callback) {
        super(numCards);
        this.callback = callback;
    }

    @Override
    public void update() {
        ScryCardHook.callback = callback;
        super.update();
        ScryCardHook.callback = null;
    }
}