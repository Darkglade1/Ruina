package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cardmods.ManifestMod;
import ruina.cardmods.RetainMod;
import ruina.cardmods.UnplayableMod;
import ruina.cards.cardvars.SecondMagicNumber;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class Gift extends AbstractRuinaCard {
    public final static String ID = makeID(Guilt.class.getSimpleName());
    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 5;
    private static final int COST = 2;
    private static final int COST_DOWN = -1;


    public Gift() {
        super(ID, COST, CardType.CURSE, CardRarity.SPECIAL, CardTarget.NONE, CardColor.CURSE);
        magicNumber = baseMagicNumber = DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = COST_DOWN;
        CardModifierManager.addModifier(this, new UnplayableMod());
        CardModifierManager.addModifier(this, new RetainMod());
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void onRetained() {
        this.modifyCostForCombat(secondMagicNumber);
        if (this.costForTurn == 0) {
            // damage
            atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.NORMAL)));
            atb(new ExhaustSpecificCardAction(this, adp().hand));
        }
    }

    public void upp() {
        upgradeMagicNumber(UPG_DAMAGE);
    }
}