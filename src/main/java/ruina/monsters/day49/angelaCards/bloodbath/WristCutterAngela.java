package ruina.monsters.day49.angelaCards.bloodbath;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class WristCutterAngela extends AbstractRuinaCard {
    public final static String ID = makeID(WristCutterAngela.class.getSimpleName());
    private Act1Angela parent;

    public WristCutterAngela(Act1Angela parent) {
        super(ID, 1, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_ALLAS.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.wristCutterHPLoss;
        secondMagicNumber = baseSecondMagicNumber = parent.wristCutterWounds;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new WristCutterAngela(parent); }
}