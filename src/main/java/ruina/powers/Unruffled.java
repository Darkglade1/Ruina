package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.adp;

public class Unruffled extends AbstractPower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID("Unruffled");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("Unruffled84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("Unruffled32.png"));

    public Unruffled(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.BUFF;
        isTurnBased = false;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.owner == adp()) {
            return amount;
        } else {
            return damageAmount;
        }
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (source == adp() && power.type == PowerType.DEBUFF) {
            return false;
        }
        return true;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
