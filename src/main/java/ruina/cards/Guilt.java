package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cardmods.ManifestMod;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class Guilt extends AbstractRuinaCard {
    public final static String ID = makeID(Guilt.class.getSimpleName());
    private static final int STRENGTH = 2;
    private static final int UP_STRENGTH = 1;
    private static final int COST = 4;
    private static final int UP_COST = 5;

    private boolean exhaustedByPlay = false;

    public Guilt() {
        super(ID, COST, CardType.CURSE, CardRarity.SPECIAL, CardTarget.NONE, CardColor.CURSE);
        magicNumber = baseMagicNumber = STRENGTH;
        CardModifierManager.addModifier(this, new ManifestMod());
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
            applyToTargetNextTurnTop(target, adp(), new StrengthPower(target, magicNumber));
            exhaust = false;
            freeToPlayOnce = true;
        } else {
            exhaustedByPlay = true;
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    @Override
    public void triggerOnExhaust() {
        if (!exhaustedByPlay) {
            intoDiscard(this.makeStatEquivalentCopy(), 1);
        }
    }

    public void upp() {
        upgradeMagicNumber(UP_STRENGTH);
        upgradeBaseCost(UP_COST);
    }
}