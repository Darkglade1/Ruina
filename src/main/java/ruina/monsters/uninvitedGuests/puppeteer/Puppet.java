package ruina.monsters.uninvitedGuests.puppeteer;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.BetterPlatedArmor;
import ruina.powers.InvisibleBarricadePower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Puppet extends AbstractRuinaMonster
{
    public static final String ID = makeID(Puppet.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte FORCEFUL_GESTURE = 0;
    private static final byte REPRESSED_FLESH = 1;
    private static final byte REVIVING = 2;
    private static final byte REVIVE = 3;

    private final int BLOCK = calcAscensionTankiness(12);
    private final int PLATED_ARMOR = calcAscensionSpecial(11);

    public boolean attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
    private final Puppeteer puppeteer;
    private final Chesed chesed;

    public static final String POWER_ID = makeID("PuppetStrings");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Puppet() {
        this(0.0f, 0.0f, null);
    }

    public Puppet(final float x, final float y, Puppeteer puppeteer) {
        super(NAME, ID, 40, -5.0F, 0, 250.0f, 395.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppet/Spriter/Puppet.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(90), calcAscensionTankiness(98));
        addMove(FORCEFUL_GESTURE, Intent.ATTACK_DEFEND, calcAscensionDamage(15));
        addMove(REPRESSED_FLESH, Intent.ATTACK, calcAscensionDamage(9), 2, true);
        addMove(REVIVING, Intent.UNKNOWN);
        addMove(REVIVE, Intent.BUFF);
        this.puppeteer = puppeteer;
        this.chesed = puppeteer.chesed;
    }

    @Override
    public void usePreBattleAction() {
        block(this, PLATED_ARMOR);
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new BetterPlatedArmor(this, PLATED_ARMOR));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
                if (info.owner == puppeteer) {
                    att(new HealAction(owner, owner, info.output));
                    return 0;
                } else {
                    return damageAmount;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        atb(new RemoveAllBlockAction(this, this));
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        AbstractCreature target;
        if (!chesed.isDead && !chesed.isDying && attackingAlly) {
            target = chesed;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case FORCEFUL_GESTURE: {
                bluntAnimation(target);
                block(this, BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case REPRESSED_FLESH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case REVIVING: {
                break;
            }
            case REVIVE: {
                atb(new HealAction(this, this, this.maxHealth));
                this.halfDead = false;
                applyToTarget(this, this, new BetterPlatedArmor(this, PLATED_ARMOR));
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onSpawnMonster(this);
                }
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = !attackingAlly;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(REVIVING)) {
            setMoveShortcut(REVIVE, MOVES[REVIVE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(FORCEFUL_GESTURE)) {
                possibilities.add(FORCEFUL_GESTURE);
            }
            if (!this.lastTwoMoves(REPRESSED_FLESH)) {
                possibilities.add(REPRESSED_FLESH);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        if (chesed != null && !chesed.isDead && !chesed.isDying && attackingAlly) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = chesed;
            if (info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                Texture attackImg;
                if (multiplier > 0) {
                    attackImg = getAttackIntent(info.output * multiplier);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    attackImg = getAttackIntent(info.output);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        } else {
            super.applyPowers();
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (!chesed.isDead && !chesed.isDying && attackingAlly && !this.isDeadOrEscaped()) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(Chesed.targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            if (this.nextMove != REVIVING) {
                setMoveShortcut(REVIVING);
                this.createIntent();
                atb(new SetMoveAction(this, REVIVING, Intent.UNKNOWN));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(MinionPower.POWER_ID)) && !(power.ID.equals(StrengthPower.POWER_ID)) && !(power.ID.equals(GainStrengthPower.POWER_ID)) && !(power.ID.equals(POWER_ID)) && !(power.ID.equals(PlatedArmorPower.POWER_ID)) && !(power.ID.equals(BarricadePower.POWER_ID))) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

}