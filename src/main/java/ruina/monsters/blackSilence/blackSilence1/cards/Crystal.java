package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Crystal;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Crystal extends AbstractRuinaCard {
    public final static String ID = makeID(Crystal.class.getSimpleName());

    BlackSilence1 parent;
    public Crystal(BlackSilence1 parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_Crystal.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.crystalDamage;
        block = baseBlock = parent.crystalBlock;
        magicNumber = baseMagicNumber = parent.crystalHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Crystal(parent);
    }
}