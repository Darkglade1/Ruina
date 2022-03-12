package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.actions.FreezeCardInHandAction;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;

public class Chill extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Chill.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int STR_DEX_THRESHOLD = 2;
    public static final int STR_DEX_DOWN = 2;
    public static final int ENERGY_THRESHOLD = 4;
    public static final int ENERGY_AMOUNT = 1;
    private static final int DECAY = 1;
    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("PromiseOfWinter84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("PromiseOfWinter32.png"));

    public Chill(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        if(amount >= ENERGY_THRESHOLD){
            atb(new LoseEnergyAction(ENERGY_AMOUNT));
            atb(new ApplyPowerAction(owner, owner, new StrengthPower(owner, -STR_DEX_DOWN)));
            atb(new ApplyPowerAction(owner, owner, new DexterityPower(owner, -STR_DEX_DOWN)));
        }
        else if (amount >= STR_DEX_THRESHOLD){
            atb(new ApplyPowerAction(owner, owner, new StrengthPower(owner, -STR_DEX_DOWN)));
            atb(new ApplyPowerAction(owner, owner, new DexterityPower(owner, -STR_DEX_DOWN)));
        }
        atb(new ReducePowerAction(owner, owner, this, DECAY));
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], STR_DEX_THRESHOLD, STR_DEX_DOWN, ENERGY_THRESHOLD, DECAY);
    }
}