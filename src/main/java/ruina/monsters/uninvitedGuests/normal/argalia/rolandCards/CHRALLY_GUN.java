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
public class CHRALLY_GUN extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_GUN.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_GUN(Roland parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.GUN_DAMAGE;
        magicNumber = baseMagicNumber = parent.GUN_HITS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < magicNumber; i++) {
            if (i == 0) {
                parent.gun1Animation(m);
            } else if (i == 1) {
                parent.gun2Animation(m);
            } else {
                parent.gun3Animation(m);
            }
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            parent.resetIdle();
        }
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_GUN(parent);
    }
}