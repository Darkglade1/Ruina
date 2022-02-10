package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Marionette extends AbstractEgoCard {
    public final static String ID = makeID(Marionette.class.getSimpleName());
    public static final int COST = 0;
    public static final int DRAW = 2;
    public static final int DAMAGE = 8;
    public static final int COST_INCREASE = 1;

    public Marionette() {
        super(ID, COST, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = DRAW;
        secondMagicNumber = baseSecondMagicNumber = COST_INCREASE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        atb(new DrawCardAction(magicNumber));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                modifyCostForCombat(secondMagicNumber);
                if (upgraded) {
                    baseMagicNumber += secondMagicNumber;
                    magicNumber += secondMagicNumber;
                }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { uDesc(); }
}