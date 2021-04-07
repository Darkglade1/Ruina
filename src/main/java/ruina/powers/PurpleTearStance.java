package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.greta.Hod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;

public class PurpleTearStance extends AbstractUnremovablePower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID("PurpleTearStance");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture slash84 = TexLoader.getTexture(makePowerPath("SlashStance84.png"));
    private static final Texture slash32 = TexLoader.getTexture(makePowerPath("SlashStance32.png"));

    private static final Texture pierce84 = TexLoader.getTexture(makePowerPath("PierceStance84.png"));
    private static final Texture pierce32 = TexLoader.getTexture(makePowerPath("PierceStance32.png"));

    private static final Texture guard84 = TexLoader.getTexture(makePowerPath("GuardStance84.png"));
    private static final Texture guard32 = TexLoader.getTexture(makePowerPath("GuardStance32.png"));

    private int stance;

    public PurpleTearStance(AbstractCreature owner, int stance) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.stance = stance;
        this.priority = 99;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && stance == Hod.SLASH) {
            return damage * (1 + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && stance == Hod.PIERCE) {
            amount++;
            if (amount >= Hod.pierceTriggerHits) {
                flash();
                amount = 0;
                for (AbstractPower power : target.powers) {
                    if (power.type == PowerType.DEBUFF) {
                        atb(new ApplyPowerAction(target, owner, power, power.amount));
                    }
                }
            }
        }
    }

    public void changeStance(int stance) {
        this.stance = stance;
        if (stance == Hod.SLASH) {
            amount = Hod.slashDamageBonus;
            this.region128 = new TextureAtlas.AtlasRegion(slash84, 0, 0, 84, 84);
            this.region48 = new TextureAtlas.AtlasRegion(slash32, 0, 0, 32, 32);
        } else {
            if (stance == Hod.PIERCE) {
                this.region128 = new TextureAtlas.AtlasRegion(pierce84, 0, 0, 84, 84);
                this.region48 = new TextureAtlas.AtlasRegion(pierce32, 0, 0, 32, 32);
            } else {
                this.region128 = new TextureAtlas.AtlasRegion(guard84, 0, 0, 84, 84);
                this.region48 = new TextureAtlas.AtlasRegion(guard32, 0, 0, 32, 32);
            }
            amount = 0;
        }
        updateDescription();
        AbstractDungeon.onModifyPower();
        AbstractRuinaMonster.playSound("PurpleChange");
        flash();
        if (owner instanceof Hod) {
            ((Hod) owner).stance = stance;
            ((Hod) owner).IdlePose();
            ((Hod) owner).rollMove();
            ((Hod) owner).createIntent();
        }
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (stance == Hod.GUARD) {
            return power.type != PowerType.DEBUFF;
        } else {
            return true;
        }
    }

    @Override
    public void updateDescription() {
        if (stance == Hod.SLASH) {
            name = DESCRIPTIONS[0];
            description = DESCRIPTIONS[1] + Hod.slashDamageBonus + DESCRIPTIONS[2];
        } else if (stance == Hod.PIERCE) {
            name = DESCRIPTIONS[3];
            description = DESCRIPTIONS[4] + Hod.pierceTriggerHits + DESCRIPTIONS[5];
        } else {
            name = DESCRIPTIONS[6];
            description = DESCRIPTIONS[7];
        }
    }
}
