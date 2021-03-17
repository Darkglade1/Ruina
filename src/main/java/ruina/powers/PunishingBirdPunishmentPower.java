package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;

public class PunishingBirdPunishmentPower extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(PunishingBirdPunishmentPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean isPunishSet = false;

    private static final Texture passive84 = TexLoader.getTexture(makePowerPath("PBirdPassive84.png"));
    private static final Texture passive32 = TexLoader.getTexture(makePowerPath("PBirdPassive32.png"));
    private static final Texture punish84 = TexLoader.getTexture(makePowerPath("PBirdPunish84.png"));
    private static final Texture punish32 = TexLoader.getTexture(makePowerPath("PBirdPunish32.png"));

    public PunishingBirdPunishmentPower(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        setBirdIcon();
        updateDescription();
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            if (!isPunishSet) {
                flash();
                isPunishSet = true;
                setBirdIcon();
                updateDescription();
            }
        }
        return damageAmount;
    }

    public void updateDescription() {
        this.description = isPunishSet ? DESCRIPTIONS[1] : DESCRIPTIONS[0];
    }

    public boolean getPunishment() {
        return isPunishSet;
    }

    public void setPunishment(boolean v) {
        isPunishSet = v;
        setBirdIcon();
    }

    public void setBirdIcon(){
        if(isPunishSet){
            this.region128 = new TextureAtlas.AtlasRegion(punish84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(punish32, 0, 0, 32, 32);
        }
        else {
            this.region128 = new TextureAtlas.AtlasRegion(passive84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(passive32, 0, 0, 32, 32);
        }
    }
}
