package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTargetTop;

@AutoAdd.Ignore
public class FourthChair extends AbstractPerformanceCard {
    public final static String ID = makeID(FourthChair.class.getSimpleName());
    private static final int COST = 1;
    private static final int DEBUFF = 1;
    private static final int UP_DEBUFF = 1;
    private boolean triggerFirstEffect = true;

    public FourthChair() {
        super(ID, COST);
        magicNumber = baseMagicNumber = DEBUFF;
        updateDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            if (triggerFirstEffect) {
                applyToTargetTop(adp(), adp(), new StrengthPower(adp(), -magicNumber));
            } else {
                applyToTargetTop(adp(), adp(), new DexterityPower(adp(), -magicNumber));
            }
            triggerFirstEffect = !triggerFirstEffect;
            updateDescription();
        }
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }

    private void updateDescription() {
        if (triggerFirstEffect) {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
        } else {
            this.rawDescription = cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[1];
        }
        this.initializeDescription();
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DEBUFF);
    }
}