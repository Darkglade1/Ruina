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
public class CHRALLY_Crystal extends AbstractRuinaCard {
    public final static String ID = makeID(CHRALLY_Crystal.class.getSimpleName());
    private final Roland parent;

    public CHRALLY_Crystal(Roland parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.crystalDamage;
        block = baseBlock = parent.crystalBlock;
        magicNumber = baseMagicNumber = parent.crystalHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        for (int i = 0; i < magicNumber; i++) {
            parent.slashAnimation(m);
            dmg(m, AbstractGameAction.AttackEffect.NONE);
            parent.resetIdle(0.25f);
            parent.waitAnimation(0.25f);
        }
    }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRALLY_Crystal(parent);
    }
}