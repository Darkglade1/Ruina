package ruina.cards.EGO;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.monsterList;

public class FadedMemories extends AbstractEgoCard {
    public final static String ID = makeID(FadedMemories.class.getSimpleName());

    public FadedMemories() {
        super(ID, 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractMonster mo : monsterList()) {
            atb(new DrawCardAction(p, 1));
        }
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}