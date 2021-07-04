package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class FirstChair extends AbstractPerformanceCard {
    public final static String ID = makeID(FirstChair.class.getSimpleName());
    private static final int COST = 1;
    private static final int DAMAGE = 2;
    private static final int UP_DAMAGE = 1;

    public FirstChair() {
        super(ID, COST);
        magicNumber = baseMagicNumber = DAMAGE;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        flash();
        atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DAMAGE);
    }
}