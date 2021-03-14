package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.RuinaMod;

public class BlockUpMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID("BlockUpMod");

    private final int blockIncrease;

    public BlockUpMod(int amount) {
        this.blockIncrease = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BlockUpMod(blockIncrease);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseBlock >= 0) {
            card.baseBlock += blockIncrease;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
