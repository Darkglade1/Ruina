package ruina.cards.EGO.act2.FadedMemories;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.UUID;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class FadedMemories1 extends FadedMemories {
    public final static String ID = makeID(FadedMemories1.class.getSimpleName());

    private UUID parentID;

    public FadedMemories1(UUID parentID) {
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
        this.parentID = parentID;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    public void onChoseThisOption(){
        for (AbstractMonster mo : monsterList()) {
            if (!mo.isDeadOrEscaped()) {
                atb(new DrawCardAction(adp(), magicNumber));
            }
        }
    }
}