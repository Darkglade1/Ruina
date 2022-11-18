package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Fragments extends AbstractEgoCard {
    public final static String ID = makeID(Fragments.class.getSimpleName());
    public static final int DAMAGE = 17;
    private static final int UP_DAMAGE = 3;
    private static final int WEAK = 1;
    private static final int UP_WEAK = 1;

    public Fragments() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = WEAK;
        selfRetain = true;
    }

    @Override
    public float getTitleFontSize() {
        return 13;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int blockToGain = Math.max(m.currentBlock, 0);
        if (blockToGain > 0) {
            atb(new RemoveAllBlockAction(m, p));
            block(p, blockToGain);
        }
        dmg(m, AbstractGameAction.AttackEffect.POISON);
        applyToTarget(m, p, new WeakPower(m, magicNumber, false));
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeMagicNumber(UP_WEAK);
    }
}