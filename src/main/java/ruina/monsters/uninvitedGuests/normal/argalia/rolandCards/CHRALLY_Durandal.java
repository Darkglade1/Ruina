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
public class CHRALLY_Durandal extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_Durandal.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_Durandal(Roland parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.durandalDamage;
        magicNumber = baseMagicNumber = parent.durandalHits;
        secondMagicNumber = baseSecondMagicNumber = parent.durandalStrength;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            if (i % 2 == 0) {
                parent.sword2Animation(m);
            } else {
                parent.sword3Animation(m);
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            parent.resetIdle();
        }
        applyToTarget(adp(), adp(), new StrengthPower(adp(), secondMagicNumber));
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_Durandal(parent);
    }
}