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
public class AtelierLogic extends AbstractRuinaCard {
    public final static String ID = makeID(AtelierLogic.class.getSimpleName());
    private Angelica parent;

    public AtelierLogic(Angelica parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.atelierDamage;
        magicNumber = baseMagicNumber = parent.atelierHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new AtelierLogic(parent); }
}