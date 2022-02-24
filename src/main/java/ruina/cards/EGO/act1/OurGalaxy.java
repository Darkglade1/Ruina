package ruina.cards.EGO.act1;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.RegenPower;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.ManifestCallbackInterface;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class OurGalaxy extends AbstractEgoCard implements ManifestCallbackInterface {
    public final static String ID = makeID(OurGalaxy.class.getSimpleName());

    private static final int BLOCK = 5;
    private static final int REGEN = 3;
    private static final int UP_BLOCK = 2;
    private static final int UP_REGEN = 1;

    public OurGalaxy() {
        super(ID, 2, CardType.SKILL, CardTarget.SELF);
        baseBlock = BLOCK;
        magicNumber = baseMagicNumber = REGEN;
        exhaust = true;
        CardModifierManager.addModifier(this, new ManifestMod());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block(adp(), block);
        applyToTarget(adp(), adp(), new RegenPower(adp(), magicNumber));
    }

    @Override
    public void numCardsUsedToManifest(int num) {
        applyToTarget(adp(), adp(), new ArtifactPower(adp(), num));
    }

    @Override
    public void upp() {
        upgradeBlock(UP_BLOCK);
        upgradeMagicNumber(UP_REGEN);
    }
}