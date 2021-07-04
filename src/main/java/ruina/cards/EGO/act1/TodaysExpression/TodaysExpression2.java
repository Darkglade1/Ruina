package ruina.cards.EGO.act1.TodaysExpression;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

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
       applyToTarget(adp(), adp(), new NextTurnBlockPower(adp(), block));
    }
}