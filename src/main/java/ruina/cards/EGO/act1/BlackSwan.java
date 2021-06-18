package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.patches.PlayerSpireFields;
import ruina.powers.Erosion;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class BlackSwan extends AbstractEgoCard {
    public final static String ID = makeID(BlackSwan.class.getSimpleName());

    public static final int EROSION = 4;
    public static final int UP_EROSION = 1;
    private boolean costReducedByEffect = false;

    public BlackSwan() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = EROSION;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (PlayerSpireFields.appliedDebuffThisTurn.get(adp())) {
            setCostForTurn(0);
            costReducedByEffect = true;
        } else {
            if (costReducedByEffect) {
                setCostForTurn(this.cost);
                this.isCostModifiedForTurn = false;
                costReducedByEffect = false;
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(m, p, new Erosion(m, magicNumber, false));
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (PlayerSpireFields.appliedDebuffThisTurn.get(adp())) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_EROSION);
        shuffleBackIntoDrawPile = true;
        uDesc();
    }
}