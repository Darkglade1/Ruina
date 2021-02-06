package ruina.cards.EGO;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class BlindRage extends AbstractEgoCard {
    public final static String ID = makeID(BlindRage.class.getSimpleName());

    public static final int DAMAGE = 26;
    public static final int UP_DAMAGE = 4;
    public static final int SELF_DAMAGE = 6;
    public static final int UP_SELF_DAMAGE = -2;

    private boolean fromHPThreshold = false;

    public BlindRage() {
        super(ID, 2, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = SELF_DAMAGE;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new DamageAction(p, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON));
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (HPCheck()) {
            setCostForTurn(0);
            fromHPThreshold = true;
        } else {
            if (fromHPThreshold) {
                setCostForTurn(this.cost);
                this.isCostModifiedForTurn = false;
                fromHPThreshold = false;
            }
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        if (HPCheck()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    private boolean HPCheck() {
        return (float)adp().currentHealth <= (float)adp().maxHealth / 2.0F;
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeMagicNumber(UP_SELF_DAMAGE);
    }
}