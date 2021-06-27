package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.UnplayableMod;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Gift extends AbstractRuinaCard {
    public final static String ID = makeID(Gift.class.getSimpleName());
    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 5;
    private static final int COST = 3;
    private static final int COST_DOWN = 1;
    private int counter;

    public Gift() {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
        magicNumber = baseMagicNumber = DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = COST_DOWN;
        selfRetain = true;
        counter = COST;
        CardModifierManager.addModifier(this, new UnplayableMod());
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void onRetained() {
        counter--;
        isCostModified = true;
        if (counter == 0) {
            atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.POISON));
            atb(new ExhaustSpecificCardAction(this, adp().hand));
        }
    }

    @Override
    public void update() {
        super.update();
        costForTurn = counter;
        freeToPlayOnce = false;
    }

    public void upp() {
        upgradeMagicNumber(UPG_DAMAGE);
    }
}