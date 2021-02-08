package ruina.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

public class Home extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Home");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int CARD_THRESHOLD = 3;
    private static final float DAMAGE_MOD = 0.25f;

    public static final String HOUSE = RuinaMod.makeMonsterPath("RoadHome/House.png");
    private static final Texture HOUSE_TEXTURE = new Texture(HOUSE);
    private TextureRegion HOUSE_REGION;

    boolean increaseDamage = false;

    public Home(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.HOUSE_REGION = new TextureRegion(HOUSE_TEXTURE);
        this.priority = 99;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            if (increaseDamage) {
                return damage * (1 + DAMAGE_MOD);
            } else {
                return damage * (1 - DAMAGE_MOD);
            }
        } else {
            return damage;
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            if (increaseDamage) {
                return damage * (1 + DAMAGE_MOD);
            } else {
                return damage * (1 - DAMAGE_MOD);
            }
        } else {
            return damage;
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        amount++;
        if (amount >= CARD_THRESHOLD) {
            flash();
            increaseDamage = !increaseDamage;
            amount = 0;
            updateDescription();
            AbstractDungeon.onModifyPower();
        } else {
            flashWithoutSound();
        }
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, c);
        if (increaseDamage) {
            sb.setColor(Color.RED);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(HOUSE_REGION, owner.hb.cX - (float)HOUSE_REGION.getRegionWidth() / 2, owner.hb.y, 0.0F, 0.0F, this.HOUSE_REGION.getRegionWidth(), this.HOUSE_REGION.getRegionHeight(), Settings.scale, Settings.scale, 0.0F);
    }

    @Override
    public void updateDescription() {
        if (increaseDamage) {
            this.description = DESCRIPTIONS[0] + (int) (DAMAGE_MOD * 100) + DESCRIPTIONS[2] + DESCRIPTIONS[3] + CARD_THRESHOLD + DESCRIPTIONS[4];
        } else {
            this.description = DESCRIPTIONS[0] + (int) (DAMAGE_MOD * 100) + DESCRIPTIONS[1] + DESCRIPTIONS[3] + CARD_THRESHOLD + DESCRIPTIONS[4];
        }
    }
}
