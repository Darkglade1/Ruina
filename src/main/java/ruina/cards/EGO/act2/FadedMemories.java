package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.monsterList;

public class FadedMemories extends AbstractEgoCard {
    public final static String ID = makeID(FadedMemories.class.getSimpleName());
    public static final int DRAW = 1;

    public FadedMemories() {
        super(ID, 0, CardType.SKILL, CardTarget.NONE);
        magicNumber = baseMagicNumber = DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : monsterList()) {
            if (!mo.isDeadOrEscaped()) {
                atb(new DrawCardAction(p, magicNumber));
            }
        }
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}