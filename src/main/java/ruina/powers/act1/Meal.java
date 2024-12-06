package ruina.powers.act1;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.monsters.act1.fairyFestival.FairyMass;
import ruina.monsters.act1.fairyFestival.FairyQueen;
import ruina.powers.AbstractUnremovablePower;

public class Meal extends AbstractUnremovablePower implements HealthBarRenderPower {

    public static final String POWER_ID = RuinaMod.makeID("Meal");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final FairyQueen queen;

    public Meal(AbstractCreature owner, int amount, FairyQueen queen) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.queen = queen;
    }

    @Override
    public void atEndOfRound() {
        if (owner.currentHealth <= amount && owner instanceof FairyMass) {
            queen.consumeMinion((AbstractMonster) owner);
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public int getHealthBarAmount() {
        return amount;
    }

    @Override
    public Color getColor() {
        return Color.BROWN.cpy();
    }
}
