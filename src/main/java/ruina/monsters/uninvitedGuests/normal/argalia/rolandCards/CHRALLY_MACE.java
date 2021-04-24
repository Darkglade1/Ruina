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
public class CHRALLY_MACE extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_MACE.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_MACE(Roland parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.MACE_DAMAGE;
        magicNumber = baseMagicNumber = parent.MACE_HITS;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            if (i % 2 == 0) {
                parent.club1Animation(m);
            } else {
                parent.club2Animation(m);
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            parent.resetIdle();
        }
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_MACE(parent);
    }
}