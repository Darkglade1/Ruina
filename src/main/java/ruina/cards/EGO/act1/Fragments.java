package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;

public class Fragments extends AbstractEgoCard {
    public final static String ID = makeID(Fragments.class.getSimpleName());
    public static final int DAMAGE = 17;
    private static final int UP_DAMAGE = 3;

    public Fragments() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public float getTitleFontSize() {
        return 13;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int blockToGain = Math.max(m.currentBlock, 0);
        atb(new RemoveAllBlockAction(m, p));
        block(p, blockToGain);
        dmg(m, AbstractGameAction.AttackEffect.POISON);
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        selfRetain = true;
    }
}