package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EnergizedBluePower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class Grinder extends AbstractEgoCard {
    public final static String ID = makeID(Grinder.class.getSimpleName());

    public static final String POWER_ID = makeID("Grinder");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int THRESHOLD = 15;

    public Grinder() {
        super(ID, 0, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = THRESHOLD;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), 1) {

            boolean triggered = false;

            @Override
            public void atStartOfTurn() {
                triggered = false;
                amount2 = 0;
            }

            @Override
            public void onInitialApplication() {
                amount2 = 0;
            }

            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && info.owner != target) {
                    if (!triggered) {
                        amount2 += damageAmount;
                        if (amount2 >= magicNumber) {
                            flash();
                            amount2 = 0;
                            triggered = true;
                            applyToTarget(p, p, new EnergizedBluePower(p, amount));
                        }
                    }
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + magicNumber + POWER_DESCRIPTIONS[1];
                for (int i = 0; i < amount; i++) {
                    description += "[E] ";
                }
                description += POWER_DESCRIPTIONS[2];
            }
        });
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}