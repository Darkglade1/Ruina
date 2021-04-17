package ruina.monsters.uninvitedGuests.extra.philip.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.BarricadePower;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class HappyTeddyBear extends AbstractEasyPower {
    public static final String POWER_ID = BarricadePower.POWER_ID;
    public static final String STRING_POWER_ID = RuinaMod.makeID(HappyTeddyBear.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(STRING_POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("HappyTeddyBear84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("HappyTeddyBear32.png"));

    public HappyTeddyBear(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    @Override
    public void updateDescription() { this.description = DESCRIPTIONS[0]; }

    public void atStartOfTurnPostDraw(){
        flash();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                att(new AddTemporaryHPAction(owner, owner, owner.currentBlock));
                att(new AbstractGameAction() {
                    @Override
                    public void update() {
                        owner.loseBlock();
                        isDone = true;
                    }
                });
                isDone = true;
            }
        });
    }
}
