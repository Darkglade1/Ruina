package ruina.monsters.blackSilence.blackSilence3.angelicaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.Angelica;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.monsters.blackSilence.blackSilence3.rolandCards.UnitedWorkshop;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class AshenBond extends AbstractRuinaCard {
    public final static String ID = makeID(AshenBond.class.getSimpleName());
    private Angelica parent;

    public AshenBond(Angelica parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.bondStrength;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new AshenBond(parent); }
}