package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Mimicry extends AbstractEgoCard {
    public final static String ID = makeID(Mimicry.class.getSimpleName());

    public static final int DAMAGE = 13;
    public static final int UP_DAMAGE = 3;

    public Mimicry() {
        super(ID, 3, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        exhaust = true;
        tags.add(CardTags.HEALING);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new VampireDamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}