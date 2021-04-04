package ruina.monsters.uninvitedGuests.philip;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.puppeteer.Chesed;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class CryingChild extends AbstractRuinaMonster
{
    public static final String ID = makeID(CryingChild.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte WING_STROKE = 0;
    private static final byte MURMUR = 1;

    private final int WEAK = calcAscensionSpecial(1);
    private final int DAMAGE_REDUCTION = 50;

    public boolean attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
    private final Philip philip;
    private final Malkuth malkuth;

    public static final String POWER_ID = makeID("TorchedHeart");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CryingChild() {
        this(0.0f, 0.0f, null);
    }

    public CryingChild(final float x, final float y, Philip philip) {
        super(NAME, ID, 40, -5.0F, 0, 100.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CryingChild/Spriter/CryingChild.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(92), calcAscensionTankiness(98));
        addMove(WING_STROKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(MURMUR, Intent.ATTACK, calcAscensionDamage(11));
        this.philip = philip;
        this.malkuth = philip.malkuth;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DAMAGE_REDUCTION) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                //handles attack damage
                if (type == DamageInfo.DamageType.NORMAL) {
                    return calculateDamageTakenAmount(damage, type);
                } else {
                    return damage;
                }
            }

            private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
                if (owner.hasPower(VulnerablePower.POWER_ID)) {
                    return damage;
                } else {
                    return damage * (1 - ((float)DAMAGE_REDUCTION / 100));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        atb(new RemoveAllBlockAction(this, this));
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        AbstractCreature target;
        if (!malkuth.isDead && !malkuth.isDying && attackingAlly) {
            target = malkuth;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case WING_STROKE: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                resetIdle();
                break;
            }
            case MURMUR: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(WING_STROKE)) {
            possibilities.add(WING_STROKE);
        }
        if (!this.lastTwoMoves(MURMUR)) {
            possibilities.add(MURMUR);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
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
        if (malkuth != null && !malkuth.isDead && !malkuth.isDying && attackingAlly) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = malkuth;
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

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "CryStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "CryHori", enemy, this);
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (!malkuth.isDead && !malkuth.isDying && attackingAlly && !this.isDeadOrEscaped()) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(Malkuth.targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < philip.minions.length; i++) {
            if (philip.minions[i] == this) {
                philip.minions[i] = null;
                break;
            }
        }
    }

}