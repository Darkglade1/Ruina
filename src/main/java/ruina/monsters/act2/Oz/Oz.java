package ruina.monsters.act2.Oz;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Fragile;
import ruina.powers.RuinaMetallicize;
import ruina.powers.act2.BearerOfGifts;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.FlexibleDivinityParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;
import java.util.function.BiFunction;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Oz extends AbstractRuinaMonster
{
    public static final String ID = makeID(Oz.class.getSimpleName());

    private static final byte WELCOME = 0;
    private static final byte BEHAVE = 1;
    private static final byte ARISE = 2;
    private static final byte NOISY = 3;
    private static final byte LIGHT = 4;
    private static final byte AWAY = 5;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int WEAK = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(10);
    private final int HEAL = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(20));
    private final int FRAGILE = calcAscensionSpecial(2);
    private final int METALLICIZE = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(5));

    public Oz() {
        this(150.0f, 0.0f);
    }

    public Oz(final float x, final float y) {
        super(ID, ID, 500, -5.0F, 0, 230.0f, 450.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Oz/Spriter/Oz.scml"));
        setHp(calcAscensionTankiness(280));
        addMove(WELCOME, Intent.ATTACK_BUFF, calcAscensionDamage(20));
        addMove(BEHAVE, Intent.ATTACK, calcAscensionDamage(7), 2);
        addMove(ARISE, Intent.UNKNOWN);
        addMove(NOISY, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(LIGHT, Intent.DEFEND_BUFF);
        addMove(AWAY, Intent.ATTACK_DEBUFF, calcAscensionDamage(24));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        applyToTarget(this, this, new BearerOfGifts(this, 0, STRENGTH));
        if (RuinaMod.isMultiplayerConnected()) {
            AbstractPower power = getPower(BearerOfGifts.POWER_ID);
            if (power instanceof BearerOfGifts) {
                if (((BearerOfGifts) power).nextGift == null) {
                    ((BearerOfGifts) power).generatePresent();
                }
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case WELCOME: {
                specialStartAnimation();
                OzCrystalEffect(adp());
                dmg(adp(), info);
                applyToTarget(this, this, new RuinaMetallicize(this, METALLICIZE));
                resetIdle(1.0f);
                break;
            }
            case BEHAVE: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation2(adp());
                    dmg(adp(), info);
                    hitEffect(adp());
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case ARISE: {
                buffAnimation();
                Summon();
                resetIdle(1.0f);
                break;
            }
            case NOISY: {
                attackAnimation(adp());
                dmg(adp(), info);
                hitEffect(adp());
                applyToTarget(adp(), this, new WeakPower(adp(), WEAK, true));
                resetIdle(1.0f);
                break;
            }
            case LIGHT: {
                buffAnimation();
                block(this, BLOCK);
                atb(new HealAction(this, this, HEAL));
                resetIdle(1.0f);
                break;
            }
            case AWAY: {
                specialStartAnimation();
                OzCrystalEffect(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Fragile(adp(), FRAGILE));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    public void Summon() {
        AbstractMonster minion = new ScowlingFace(-450.0F, 0.0f, true);
        atb(new SpawnMonsterAction(minion, true));
        atb(new UsePreBattleActionAction(minion));
        AbstractMonster minion2 = new ScowlingFace(-150F, 0.0f, false);
        atb(new SpawnMonsterAction(minion2, true));
        atb(new UsePreBattleActionAction(minion2));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(WELCOME);
        } else if (!areMinionsAlive()) {
            setMoveShortcut(ARISE);
        } else if (moveHistory.size() >= 4 && !this.lastMove(AWAY) && !this.lastMoveBefore(AWAY) && !this.lastMoveBeforeBefore(AWAY)) {
            setMoveShortcut(AWAY);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(NOISY) && !this.lastMoveBefore(NOISY)) {
                possibilities.add(NOISY);
            }
            if (!this.lastMove(LIGHT) && !this.lastMoveBefore(LIGHT)) {
                possibilities.add(LIGHT);
            }
            if (!this.lastMove(BEHAVE) && !this.lastMoveBefore(BEHAVE)) {
                possibilities.add(BEHAVE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    private boolean areMinionsAlive() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ScowlingFace && !mo.isDeadOrEscaped()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makePowerPath("Fragile32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case WELCOME: {
                DetailedIntent detail = new DetailedIntent(this, METALLICIZE, DetailedIntent.METALLICIZE_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case ARISE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                detailsList.add(detail);
                break;
            }
            case NOISY: {
                DetailedIntent detail = new DetailedIntent(this, WEAK, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case LIGHT: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, HEAL, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case AWAY: {
                DetailedIntent detail = new DetailedIntent(this, FRAGILE, texture);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof ScowlingFace) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        AbstractPower power = getPower(BearerOfGifts.POWER_ID);
        if (power instanceof BearerOfGifts) {
            AbstractCard nextGift = ((BearerOfGifts) power).nextGift;
            if (nextGift != null) {
                tips.add(new CardPowerTip(nextGift.makeStatEquivalentCopy()));
            }
        }
    }

    private void hitEffect(AbstractCreature target) {
        Texture texture = TexLoader.getTexture(RuinaMod.makeVfxPath("OzHitEffect.png"));
        float duration = 0.6f;
        AbstractGameEffect appear = new VfxBuilder(texture, target.hb.cX, target.hb.cY, duration)
                .fadeOut(duration)
                .build();
        atb(new VFXAction(appear, duration));
    }

    private void OzCrystalEffect(AbstractCreature target) {
        Texture fallingCrystal = TexLoader.getTexture(RuinaMod.makeVfxPath("OzCrystalFall.png"));
        Texture landedCrystal = TexLoader.getTexture(RuinaMod.makeVfxPath("OzCrystalHit.png"));
        Texture glowEffect = TexLoader.getTexture(RuinaMod.makeVfxPath("OzGlow.png"));
        float fallDuration = 1.0f;
        float landDuration = 1.0f;
        float targetY = AbstractDungeon.floorY + (200.0f * Settings.scale);
        Texture shard1 = TexLoader.getTexture(makeVfxPath("OzShard1.png"));
        Texture shard2 = TexLoader.getTexture(makeVfxPath("OzShard2.png"));
        ArrayList<Texture> shardTextures = new ArrayList<>();
        shardTextures.add(shard1);
        shardTextures.add(shard2);
        int numShards = AbstractDungeon.miscRng.random(16, 24);
        float shardDuration = 1.5f;
        int shardSpeed = 400;

        VfxBuilder builder = new VfxBuilder(fallingCrystal, target.hb.cX, 0f, fallDuration)
                .moveY(Settings.HEIGHT, targetY - 100.0f * Settings.scale, VfxBuilder.Interpolations.LINEAR)
                .triggerVfxAt(fallDuration, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                    @Override
                    public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                        playSound("OzStrongAtkDown");
                        return new VfxBuilder(landedCrystal, target.hb.cX, targetY, landDuration)
                                .triggerVfxAt(0, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                            @Override
                            public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                                return new VfxBuilder(glowEffect, target.hb.cX, targetY - 100.0f * Settings.scale, landDuration)
                                        .scale(1.0f, 2.5f, VfxBuilder.Interpolations.LINEAR).build();
                            }
                        }).triggerVfxAt(landDuration, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                                    @Override
                                    public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                                        playSound("OzStrongAtkFinish");
                                        return new VfxBuilder(shard1, target.hb.cX, target.hb.cY, shardDuration)
                                                .velocity(45 * MathUtils.random(0, 8), shardSpeed)
                                                .rotate(100)
                                                .fadeOut(shardDuration).build();
                                    }
                                })
                                .triggerVfxAt(landDuration, numShards, new BiFunction<Float, Float, AbstractGameEffect>() {
                                    @Override
                                    public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                                        Texture chosenShard = shardTextures.get(MathUtils.random(0, 1));
                                        int angle = MathUtils.random(0, 12);
                                        return new VfxBuilder(chosenShard, target.hb.cX, target.hb.cY, shardDuration)
                                                .velocity(30 * angle, shardSpeed)
                                                .rotate(100)
                                                .fadeOut(shardDuration).build();
                                    }
                                }).build();
                    }
                });
        float totalDuration = fallDuration + landDuration + shardDuration;
        atb(new VFXAction(builder.build(), totalDuration));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "OzAtkBoom", enemy, this);
    }

    private void attackAnimation2(AbstractCreature enemy) {
        animationAction("Ranged", "OzAtkUp", enemy, this);
    }

    private void specialStartAnimation() {
        animationAction("Special", "OzStrongAtkStart", this);
    }

    private void buffAnimation() {
        animationAction("Ranged", "OzMagic", this);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(BearerOfGifts.POWER_ID) && this.getPower(BearerOfGifts.POWER_ID).amount == 1) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this, Color.GREEN.cpy()));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(Color.GREEN.cpy(), this));
            }
        }
    }

}