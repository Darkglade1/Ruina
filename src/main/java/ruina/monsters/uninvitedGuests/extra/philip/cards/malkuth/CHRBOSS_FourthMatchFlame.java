package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FourthMatchFlame extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_FourthMatchFlame.class.getSimpleName());
    public static final int DAMAGE = 60;
    public static final int BURN = 10;
    public static final int UPG_DAMAGE = 10;
    public static final int UPG_BURN = 10;
    public static final int STR_SCALING = 3;

    public CHRBOSS_FourthMatchFlame() {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BURN;
        secondMagicNumber = baseSecondMagicNumber = STR_SCALING;
        tags.add(RuinaMod.Enums.ABNO_SG);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.FIRE);
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
        upgradeMagicNumber(UPG_BURN);
    }

    public void applyPowers() {
        AbstractPower strength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
        if (strength != null) { strength.amount *= STR_SCALING; }
        super.applyPowers();
        if (strength != null) { strength.amount /= STR_SCALING; }
    }

    public void calculateCardDamage(AbstractMonster mo) {
        AbstractPower strength = AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
        if (strength != null) { strength.amount *= STR_SCALING; }
        super.calculateCardDamage(mo);
        if (strength != null) { strength.amount /= STR_SCALING; }
    }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_FourthMatchFlame(); }
}