package ruina.cards.EGO.act1;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.ManifestCallbackInterface;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class SolemnLament extends AbstractEgoCard implements ManifestCallbackInterface {
    public final static String ID = makeID(SolemnLament.class.getSimpleName());

    private static final int INTANGIBLE = 1;

    public SolemnLament() {
        super(ID, 2, CardType.SKILL, CardTarget.SELF);
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
        isEthereal = false;
    }

    @Override
    public void numCardsUsedToManifest(int num) {
        if (num >= costForTurn) {
            exhaust = false;
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    exhaust = true;
                    this.isDone = true;
                }
            });
        }
    }
}