package ruina.cards.ABNO;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class TestCardThree extends AbstractAbnormalityCard {
    public final static String ID = makeID(TestCardThree.class.getSimpleName());
    public static final int COST = 1;
    public static final int UP_COST = 0;
    public static final int HEAL = 8;

    public TestCardThree() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = HEAL;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new HealAction(p, p, magicNumber));
        atb(new RemoveDebuffsAction(p));
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}