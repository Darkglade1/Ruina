package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.actions.HarmonyAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class Harmony extends AbstractEgoCard {
    public final static String ID = makeID(Harmony.class.getSimpleName());
    public static final int DAMAGE = 11;
    private static final int UP_DAMAGE = 5;
    private static final int BASE_STRENGTH = 1;
    private static final int STRENGTH_GAIN = 1;

    public Harmony() {
        super(ID, 1, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        misc = BASE_STRENGTH;
        magicNumber = baseMagicNumber = misc;
        secondMagicNumber = baseSecondMagicNumber = STRENGTH_GAIN;
        isInnate = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new HarmonyAction(m, new DamageInfo(p, damage, damageTypeForTurn), secondMagicNumber, uuid));
        applyToTarget(p, p, new StrengthPower(p, magicNumber));
    }

    @Override
    public void update() {
        super.update();
        magicNumber = baseMagicNumber = misc;
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}