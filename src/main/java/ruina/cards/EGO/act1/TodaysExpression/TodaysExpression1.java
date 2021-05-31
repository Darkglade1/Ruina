package ruina.cards.EGO.act1.TodaysExpression;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.block;

@AutoAdd.Ignore
public class TodaysExpression1 extends TodaysExpression {
    public final static String ID = makeID(TodaysExpression1.class.getSimpleName());

    public TodaysExpression1() {
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    public void onChoseThisOption(){
        block(adp(), block);
    }
}