package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

public class SenselessWrath extends AbstractPower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID("SenselessWrath");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int THRESHOLD = 2;
    private boolean justStrengthDown = false;
    private boolean negateGainStrengthUp = false;

//    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("Purity84.png"));
//    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("Purity32.png"));

    public SenselessWrath(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = 1;

        type = PowerType.BUFF;
        isTurnBased = false;
        this.loadRegion("focus");

//        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
//        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power.type == PowerType.DEBUFF) {
            //Handle temp Strength down effects
            if (power instanceof StrengthPower) {
                if (this.amount < THRESHOLD) {
                    justStrengthDown = true;
                } else {
                    negateGainStrengthUp = true;
                }
            }
            if (power instanceof GainStrengthPower) {
                if (justStrengthDown) {
                    justStrengthDown = false;
                    return true;
                }
                if (negateGainStrengthUp) {
                    negateGainStrengthUp = false;
                    return false;
                }
            }
            //Actual code
            if (this.amount >= THRESHOLD) {
                this.amount = 1;
                justStrengthDown = false;
                return false;
            } else {
                this.amount++;
                negateGainStrengthUp = false;
            }
        }
        return true;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
