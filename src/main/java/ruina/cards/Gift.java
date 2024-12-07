package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
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
    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 2;

    public Gift() {
        super(ID, -2, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
        magicNumber = baseMagicNumber = DAMAGE;
        isEthereal = true;
        CardModifierManager.addModifier(this, new UnplayableMod());
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void triggerOnExhaust() {
        atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.POISON));
    }

    public void upp() {
        upgradeMagicNumber(UPG_DAMAGE);
    }
}