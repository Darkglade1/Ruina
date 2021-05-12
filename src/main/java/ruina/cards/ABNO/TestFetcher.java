package ruina.cards.ABNO;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class TestFetcher extends AbstractAbnormalityCard {
    public final static String ID = makeID(TestFetcher.class.getSimpleName());
    public static final int COST = 1;
    public static final int UP_COST = 0;
    public static final int HEAL = 8;

    public TestFetcher() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = HEAL;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.add(new TestCardOne());
        cards.add(new TestCardTwo());
        cards.add(new TestCardThree());
        atb(new ChooseOneAction(cards));
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}