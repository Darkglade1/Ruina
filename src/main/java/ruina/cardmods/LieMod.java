package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static ruina.RuinaMod.makeID;

@AbstractCardModifier.SaveIgnore
public class LieMod extends AbstractCardModifier {
    public static final String ID = makeID(LieMod.class.getSimpleName());

    public AbstractCard source;

    public LieMod(AbstractCard source) {
        this.source = source;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseBlock >= 0) {
            card.baseBlock += AbstractDungeon.miscRng.random(1, 10);
        }
        if (card.baseDamage >= 0) {
            card.baseDamage += AbstractDungeon.miscRng.random(1, 10);
        }
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new LieMod(source.makeStatEquivalentCopy());
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return cardName + "?";
    }
}