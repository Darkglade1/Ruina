package ruina.cards.EGO.act1.TodaysExpression;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class TodaysExpression2 extends TodaysExpression {
    public final static String ID = makeID(TodaysExpression2.class.getSimpleName());

    public TodaysExpression2() {
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    public void onChoseThisOption(){
        if (target != null) {
            dmg(target, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }
    }
}