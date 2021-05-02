package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;
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
    private int pierceStanceCounter = 0;

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
            pierceStanceCounter++;
            amount = pierceStanceCounter;
            if (amount >= Hod.pierceTriggerHits) {
                flash();
                pierceStanceCounter = amount = 0;
                AbstractCreature enemy = target;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractPower power : enemy.powers) {
                            if (power.type == PowerType.DEBUFF) {
                                atb(new ApplyPowerAction(enemy, owner, power, power.amount));
                            }
                        }
                        this.isDone = true;
                    }
                });
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
                amount = pierceStanceCounter;
                this.region128 = new TextureAtlas.AtlasRegion(pierce84, 0, 0, 84, 84);
                this.region48 = new TextureAtlas.AtlasRegion(pierce32, 0, 0, 32, 32);
            } else {
                amount = 0;
                this.region128 = new TextureAtlas.AtlasRegion(guard84, 0, 0, 84, 84);
                this.region48 = new TextureAtlas.AtlasRegion(guard32, 0, 0, 32, 32);
            }
        }
        updateDescription();
        AbstractRuinaMonster.playSound("PurpleChange");
        if (owner instanceof Hod) {
            ((Hod) owner).stance = stance;
            ((Hod) owner).IdlePose();
            ((Hod) owner).rollMove();
            ((Hod) owner).createIntent();
        }
        AbstractDungeon.onModifyPower();
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
        description += DESCRIPTIONS[8];
    }
}
