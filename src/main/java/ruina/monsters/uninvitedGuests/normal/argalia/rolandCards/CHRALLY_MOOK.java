package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRALLY_MOOK extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_MOOK.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_MOOK(Roland parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.MOOK_DAMAGE;
        magicNumber = baseMagicNumber = parent.MOOK_DEBUFF;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        parent.mook1Animation(m);
        parent.waitAnimation(0.25f);
        parent.mook2Animation(m);
        dmg(m, AbstractGameAction.AttackEffect.NONE);
        parent.resetIdle();
        applyToTarget(m, adp(), new VulnerablePower(m, magicNumber, false));
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_MOOK(parent);
    }
}