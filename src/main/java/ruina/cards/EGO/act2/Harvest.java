package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Harvest extends AbstractEgoCard {
    public final static String ID = makeID(Harvest.class.getSimpleName());

    public static final int DAMAGE = 40;
    public static final int UP_DAMAGE = 10;

    public Harvest() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = (int)(baseDamage * hpMultiplier());
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = (int)(baseDamage * hpMultiplier());
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    private float hpMultiplier() {
        return (float)adp().currentHealth / adp().maxHealth;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}