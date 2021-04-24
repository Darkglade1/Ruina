package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRALLY_ALLAS extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_ALLAS.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_ALLAS(Roland parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.ALLAS_DAMAGE;
        magicNumber = baseMagicNumber = parent.ALLAS_DEBUFF;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        parent.pierceAnimation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle();
        applyToTarget(m, adp(), new WeakPower(m, parent.ALLAS_DEBUFF, false));
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_ALLAS(parent);
    }
}