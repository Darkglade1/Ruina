package ruina.powers;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.cardmods.ExhaustMod;
import ruina.cardmods.RetainMod;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.makeInHand;

public class PlayerBlackSilence extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(PlayerBlackSilence.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("BlackSilence84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("BlackSilence32.png"));

    public static final int THRESHOLD = 9;
    public Roland parent;

    public PlayerBlackSilence(AbstractCreature owner, Roland parent) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        this.parent = parent;
    }


    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + THRESHOLD + DESCRIPTIONS[1];
    }
}