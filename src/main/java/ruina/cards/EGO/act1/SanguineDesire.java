package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.SanguineDesireAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class SanguineDesire extends AbstractEgoCard {
    public final static String ID = makeID(SanguineDesire.class.getSimpleName());

    public static final int DAMAGE = 18;
    public static final int UP_DAMAGE = 4;

    public SanguineDesire() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new SanguineDesireAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}