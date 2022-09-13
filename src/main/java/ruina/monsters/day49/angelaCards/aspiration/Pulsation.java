package ruina.monsters.day49.angelaCards.aspiration;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;
import ruina.powers.Pneumonia;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Pulsation extends AbstractRuinaCard {
    public final static String ID = makeID(Pulsation.class.getSimpleName());
    private Act2Angela parent;

    public Pulsation(Act2Angela parent) {
        super(ID, 2, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.pulsationStrength;
        this.parent = parent;
        switch (parent.pulsationBuffCount){
            case 0:
            case 1:
            case 2:
                secondMagicNumber = baseSecondMagicNumber = parent.pulsationPneumoniaDecrease;
                rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0];
                break;
            case 3:
                secondMagicNumber = baseSecondMagicNumber = parent.pulsationFourthBuffStrength;
                rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
                break;
            default:
                secondMagicNumber = baseSecondMagicNumber = parent.pulsationFifthBuffStrength;
                rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
                break;
        }
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Pulsation(parent); }
}