package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.act3.ParadiseLostPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class ParadiseLost extends AbstractEgoCard {
    public final static String ID = makeID(ParadiseLost.class.getSimpleName());

    public static final int EXHAUST = 1;
    //public static final int UP_EXHAUST = 1;
    public static final int THRESHOLD = 12;
    public static final int DAMAGE = 150;
    public static final int UP_DAMAGE = 50;

    public static final String POWER_ID = makeID("ParadiseLost");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ParadiseLost() {
        super(ID, 3, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = EXHAUST;
        secondMagicNumber = baseSecondMagicNumber = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(adp(), adp(), new ParadiseLostPower(adp(), secondMagicNumber, magicNumber, THRESHOLD), secondMagicNumber));
    }

    @Override
    public void upp() {
        //upgradeMagicNumber(UP_EXHAUST);
        upgradeSecondMagic(UP_DAMAGE);
        //rawDescription = languagePack.getCardStrings(cardID).UPGRADE_DESCRIPTION;
        initializeDescription();
    }
}