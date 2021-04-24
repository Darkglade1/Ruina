package ruina.monsters.uninvitedGuests.normal.argalia.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRALLY_RANGA extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_RANGA.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_RANGA(Roland parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.RANGA_DAMAGE;
        magicNumber = baseMagicNumber = parent.RANGA_HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.RANGA_DEBUFF;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            if (i == 0) {
                parent.claw1Animation(m);
            } else if (i == 1) {
                parent.claw2Animation(m);
            } else {
                parent.knifeAnimation(m);
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            parent.resetIdle();
        }
        applyToTarget(m, adp(), new Bleed(m, secondMagicNumber));
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_RANGA(parent);
    }
}