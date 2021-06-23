package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.ForgottenAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Forgotten extends AbstractEgoCard {
    public final static String ID = makeID(Forgotten.class.getSimpleName());
    public static final int DAMAGE = 8;
    private static final int UP_DAMAGE = 3;

    public Forgotten() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ForgottenAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}