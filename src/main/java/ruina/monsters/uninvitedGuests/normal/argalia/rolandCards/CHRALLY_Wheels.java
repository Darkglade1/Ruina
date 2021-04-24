package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRALLY_Wheels extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_Wheels.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_Wheels(Roland parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.wheelsDamage;
        magicNumber = baseMagicNumber = parent.wheelsStrDown;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        parent.wheelsAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        applyToTarget(m, adp(), new StrengthPower(m, -magicNumber));
        parent.resetIdle();
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_Wheels(parent);
    }
}