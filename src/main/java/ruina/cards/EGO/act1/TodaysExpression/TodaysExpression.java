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

    private static final int DAMAGE = 20;
    private static final int UP_DAMAGE = 5;
    private static final int BLOCK = 17;
    private static final int UP_BLOCK = 5;

    protected AbstractMonster target;

    public TodaysExpression() {
        super(ID, 2, CardType.SKILL, CardTarget.ENEMY);
        this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        baseDamage = DAMAGE;
        baseBlock = BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        target = m;
        ArrayList<AbstractCard> options = new ArrayList<>();
        options.add(new TodaysExpression1());
        options.add(new TodaysExpression2());
        atb(new ChooseOneAction(options));
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeBlock(UP_BLOCK);
    }
}