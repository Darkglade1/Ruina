package ruina.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

public class MirageDexterity extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(MirageDexterity.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MirageDexterity(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        updateDescription();
        loadRegion("dexterity");
        canGoNegative = true;
    }

    @Override
    public void updateDescription() {
        if (amount > 0) {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[1] + -amount + DESCRIPTIONS[2];
        }
    }


    public float modifyBlock(float blockAmount) {
        return (blockAmount += (float)this.amount) < 0.0F ? 0.0F : blockAmount;
    }
}
