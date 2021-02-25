package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Enchanted extends AbstractPower {

    public static final String POWER_ID = RuinaMod.makeID("Enchanted");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("Enchanted84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("Enchanted32.png"));

    public Enchanted(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.DEBUFF;
        isTurnBased = false;

        this.priority = 100;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner == adp() && damageAmount > 0) {
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
        return damageAmount;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return amount;
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
