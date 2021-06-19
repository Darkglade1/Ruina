package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.HornetPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

public class Hornet extends AbstractEgoCard {
    public final static String ID = makeID(Hornet.class.getSimpleName());
    public static final int DAMAGE = 15;
    private static final int UP_DAMAGE = 7;

    public Hornet() {
        super(ID, 3, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        applyToTarget(p, p, new FreeAttackPower(p, 1));
        applyToTarget(p, p, new HornetPower(p));
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}