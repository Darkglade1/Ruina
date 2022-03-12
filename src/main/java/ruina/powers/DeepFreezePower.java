package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.actions.FreezeCardInHandAction;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;

public class DeepFreezePower extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(DeepFreezePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int FREEZE_AMOUNT = 1;
    public static final int THRESHOLD = 5;
    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("DeepFreeze84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("DeepFreeze32.png"));

    public DeepFreezePower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        atb(new FreezeCardInHandAction(owner, FREEZE_AMOUNT));
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], FREEZE_AMOUNT, THRESHOLD);
    }
}