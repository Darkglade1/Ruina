package ruina.cards.EGO.act1;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.ManifestCallbackInterface;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.intoDiscard;

public class SolemnLament extends AbstractEgoCard implements ManifestCallbackInterface {
    public final static String ID = makeID(SolemnLament.class.getSimpleName());

    private static final int INTANGIBLE = 1;
    private static final int COST = 3;
    private static final int UP_COST = 2;

    public SolemnLament() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = INTANGIBLE;
        isEthereal = true;
        exhaust = true;
        CardModifierManager.addModifier(this, new ManifestMod());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(p, p, new IntangiblePlayerPower(p, magicNumber));
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }

    @Override
    public void numCardsUsedToManifest(int num) {
        if (num >= costForTurn) {
            intoDiscard(makeStatEquivalentCopy(), 1);
        }
    }
}