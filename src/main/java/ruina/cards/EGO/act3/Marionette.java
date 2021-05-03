package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.ReduceCostAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.IncreaseCostAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class Marionette extends AbstractEgoCard {
    public final static String ID = makeID(Marionette.class.getSimpleName());
    public static final int COST = 1;
    public static final int INC_COST = 1;
    public static final int DAMAGE = 25;
    public static final int UPG_DAMAGE = 5;

    public Marionette() {
        super(ID, COST, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = INC_COST;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        atb(new IncreaseCostAction(this, magicNumber));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractCard c: p.hand.group){ att(new IncreaseCostAction(c, magicNumber)); }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeDamage(UPG_DAMAGE); }
}