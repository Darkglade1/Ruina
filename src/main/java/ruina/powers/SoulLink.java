package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;

public class SoulLink extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SoulLink.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("Vibration84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("Vibration32.png"));

    public SoulLink(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        updateDescription();
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void updateDescription() { description = String.format(DESCRIPTIONS[0]); }
}