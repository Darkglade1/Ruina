package ruina.cards;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import ruina.actions.GrindingGearsAction;
import ruina.monsters.act1.singingMachine.SingingMachineMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class GrindingGears extends AbstractRuinaCard {
    public final static String ID = makeID(GrindingGears.class.getSimpleName());
    private static final int COST = 1;
    private static final int SELF_DAMAGE = 3;
    private static final int UP_SELF_DAMAGE = 3;
    private static final int NUM_CARD = 1;

    private SingingMachineMonster machine;

    public GrindingGears(SingingMachineMonster machine) {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = SELF_DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = NUM_CARD;
        this.machine = machine;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.FAST_MODE) {
            atb(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            atb(new VFXAction(new OfferingEffect(), 0.5F));
        }
        atb(new DamageAction(p, new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.NONE));
        atb(new GrindingGearsAction(secondMagicNumber, machine));
        atb(new StunMonsterAction(machine, p));
    }

    @Override
    public AbstractCard makeCopy() {
        return new GrindingGears(machine);
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_SELF_DAMAGE);
    }
}