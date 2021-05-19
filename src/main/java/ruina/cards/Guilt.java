package ruina.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cardmods.ManifestMod;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTargetNextTurnTop;

public class Guilt extends AbstractRuinaCard {
    public final static String ID = makeID(Guilt.class.getSimpleName());
    private static final int STRENGTH = 2;
    private static final int UP_STRENGTH = 1;
    private static final int COST = 4;

    public Guilt() {
        super(ID, COST, CardType.CURSE, CardRarity.SPECIAL, CardTarget.NONE, CardColor.CURSE);
        magicNumber = baseMagicNumber = STRENGTH;
        CardModifierManager.addModifier(this, new ManifestMod());
        exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    public void triggerOnEndOfTurnForPlayingCard() {
        AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        applyToTargetNextTurnTop(target, new StrengthPower(target, magicNumber));
    }

    public void upp() {
        upgradeMagicNumber(UP_STRENGTH);
    }
}