package ruina.cards.EGO.act3;

import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.patches.PlayerSpireFields;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class SoundOfAStar extends AbstractEgoCard {
    public final static String ID = makeID(SoundOfAStar.class.getSimpleName());

    public static final int DAMAGE = 10;
    public static final int EXHAUSTIVE = 2;
    public static final int COST = 3;
    public static final int UP_COST = 2;

    public SoundOfAStar() {
        super(ID, COST, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        isMultiDamage = true;
        ExhaustiveVariable.setBaseValue(this, EXHAUSTIVE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += PlayerSpireFields.totalBlockGained.get(adp());
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += PlayerSpireFields.totalBlockGained.get(adp());
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}