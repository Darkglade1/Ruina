package ruina.monsters.day49.angelaCards.silentGirlPhase1;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act3.Remorse;
import ruina.monsters.day49.Act5Angela;
import ruina.monsters.day49.angelaCards.aspiration.Pulsation;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class BrokenToPieces extends AbstractRuinaCard {
    public final static String ID = makeID(BrokenToPieces.class.getSimpleName());
    private Act5Angela parent;

    public BrokenToPieces(Act5Angela parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + Remorse.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.brokenDamage;
        magicNumber = baseMagicNumber = parent.brokenHits;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new BrokenToPieces(parent); }
}