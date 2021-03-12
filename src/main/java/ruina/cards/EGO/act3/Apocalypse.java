package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Apocalypse extends AbstractEgoCard {
    public final static String ID = makeID(Apocalypse.class.getSimpleName());

    public static final int DAMAGE = 22;
    public static final int UP_DAMAGE = 4;
    public static final int BONUS_DAMAGE = 50;
    public static final int HP_THRESHOLD = 50;

    public Apocalypse() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BONUS_DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = HP_THRESHOLD;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.FIRE);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = (int)(baseDamage * playerHPMultiplier());
        this.baseDamage = (int)(baseDamage * enemyHPMultiplier(mo));
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage = (int)(baseDamage * playerHPMultiplier());
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    private float enemyHPMultiplier(AbstractMonster mo) {
        float hpThreshold = (float)HP_THRESHOLD / 100;
        if (mo.currentHealth <= mo.maxHealth * hpThreshold) {
            return 1 + ((float)BONUS_DAMAGE / 100);
        }
        return 1;
    }

    private float playerHPMultiplier() {
        if (isPlayerUnderThreshold()) {
            return 1 + ((float)BONUS_DAMAGE / 100);
        }
        return 1;
    }

    private boolean isPlayerUnderThreshold() {
        float hpThreshold = (float)HP_THRESHOLD / 100;
        return adp().currentHealth <= adp().maxHealth * hpThreshold;
    }

    @Override
    public void triggerOnGlowCheck() {
        if (isPlayerUnderThreshold()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}