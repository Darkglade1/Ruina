package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.RedEyesPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

public class RedEyes extends AbstractEgoCard {
    public final static String ID = makeID(RedEyes.class.getSimpleName());
    public static final int DAMAGE = 9;
    private static final int UP_DAMAGE = 4;

    public RedEyes() {
        super(ID, 1, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.POISON);
        applyToTarget(m, p, new RedEyesPower(m));

    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}