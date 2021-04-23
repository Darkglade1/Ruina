package ruina.monsters.blackSilence.blackSilence3.angelicaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.Angelica;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class AllasWorkshop extends AbstractRuinaCard {
    public final static String ID = makeID(AllasWorkshop.class.getSimpleName());
    private Angelica parent;

    public AllasWorkshop(Angelica parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.allasDamage;
        magicNumber = baseMagicNumber = parent.allasDebuff;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new AllasWorkshop(parent); }
}