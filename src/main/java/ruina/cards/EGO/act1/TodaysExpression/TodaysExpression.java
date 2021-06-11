package ruina.cards.EGO.act1.TodaysExpression;

import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class TodaysExpression extends AbstractEgoCard {
    public final static String ID = makeID(TodaysExpression.class.getSimpleName());

    private static final int BLOCK = 18;
    private static final int UP_BLOCK = 5;

    protected static AbstractMonster target;

    public TodaysExpression() {
        super(ID, 2, CardType.SKILL, CardTarget.SELF);
        this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        baseBlock = BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        target = m;
        ArrayList<AbstractCard> options = new ArrayList<>();
        AbstractCard card1 = new TodaysExpression1();
        AbstractCard card2 = new TodaysExpression2();
        if (this.upgraded) {
            card1.upgrade();
            card2.upgrade();
        }
        card1.applyPowers();
        card1.calculateCardDamage(m);
        card2.applyPowers();
        card2.calculateCardDamage(m);
        options.add(card1);
        options.add(card2);
        atb(new ChooseOneAction(options));
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void upp() {
        upgradeBlock(UP_BLOCK);
    }
}