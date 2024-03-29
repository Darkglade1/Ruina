package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.monsterList;

public class Smile extends AbstractEgoCard {
    public final static String ID = makeID(Smile.class.getSimpleName());

    public static final int DAMAGE = 16;
    public static final int UP_DAMAGE = 4;
    public static final int DEBUFF = 2;
    public static final int UP_DEBUFF = 1;

    public Smile() {
        super(ID, 2, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = DEBUFF;
        isInnate = true;
        exhaust = true;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.POISON);
        for (AbstractMonster mo : monsterList()) {
            applyToTarget(mo, p, new WeakPower(mo, magicNumber, false));
            applyToTarget(mo, p, new VulnerablePower(mo, magicNumber, false));
        }
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeMagicNumber(UP_DEBUFF);
    }
}