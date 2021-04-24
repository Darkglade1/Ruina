package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRALLY_OLDBOYS extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_OLDBOYS.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_OLDBOYS(Roland parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.OLD_BOY_DAMAGE;
        block = baseBlock = parent.OLD_BOY_BLOCK;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 16;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        parent.attackAnimation(m);
        blck();
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle();
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_OLDBOYS(parent);
    }
}