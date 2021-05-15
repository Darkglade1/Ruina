package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Marionette extends AbstractEgoCard {
    public final static String ID = makeID(Marionette.class.getSimpleName());
    public static final int COST = 1;
    public static final int SET_COST = 2;
    public static final int DAMAGE = 16;
    public static final int UPG_DAMAGE = 4;

    public Marionette() {
        super(ID, COST, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = SET_COST;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard c : p.hand.group) {
                    if (c.costForTurn >= 0) {
                        if (c.costForTurn != magicNumber) {
                            c.isCostModified = true;
                        }
                        c.cost = magicNumber;
                        c.costForTurn = magicNumber;
                    }
                }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeDamage(UPG_DAMAGE); }
}