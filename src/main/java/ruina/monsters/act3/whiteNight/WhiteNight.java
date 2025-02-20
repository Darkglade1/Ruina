package ruina.monsters.act3.whiteNight;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act3.WhiteNightBlessing;
import ruina.powers.act3.WingsOfGrace;
import ruina.powers.multiplayer.WhiteNightBlessingMultiplayer;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WhiteNight extends AbstractRuinaMonster {
    public static final String ID = makeID(WhiteNight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String GLOWING_CIRCLE = RuinaMod.makeMonsterPath("WhiteNight/GlowingCircle.png");
    private static final Texture GLOWING_CIRCLE_TEXTURE = new Texture(GLOWING_CIRCLE);
    private static final byte PRAYER = 0;
    private static final byte RISE_AND_SERVE = 1;
    private static final byte BENEDICTION = 2;
    private static final byte SALVATION = 3;
    private static final byte BEHOLD = 4;
    private static final byte SAVIOR = 5;

    public static final int AWAKE_PHASE = 2;

    private final int RITUAL = calcAscensionSpecial(1);
    private final int HEAL = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(8));
    private final int BLOCK = calcAscensionTankiness(15);
    private final int BLESSING_AMT = 12;
    private final int MAX_TURNS = 6;
    private final int WINGS = calcAscensionSpecial(8);

    public WhiteNight() {
        this(-50.0f, 0.0f);
    }

    public WhiteNight(final float x, final float y) {
        super(NAME, ID, 666, -5.0F, 170, 280.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WhiteNight/Spriter/WhiteNightEgg.scml"));
        this.setHp(666);

        addMove(PRAYER, Intent.BUFF);
        addMove(RISE_AND_SERVE, Intent.ATTACK, calcAscensionDamage(50));
        addMove(BENEDICTION, Intent.BUFF);
        addMove(SALVATION, Intent.DEFEND_BUFF);
        addMove(BEHOLD, Intent.ATTACK, calcAscensionDamage(21));
        addMove(SAVIOR, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela3");
        if (phase == AWAKE_PHASE) {
            phaseChange();
        } else {
            if (RuinaMod.isMultiplayerConnected()) {
                applyToTarget(this, this, new WhiteNightBlessingMultiplayer(this, RuinaMod.getMultiplayerPlayerCountScaling(BLESSING_AMT), MAX_TURNS, this));
            } else {
                applyToTarget(this, this, new WhiteNightBlessing(this, BLESSING_AMT, MAX_TURNS, this));
            }
        }
        if (AbstractDungeon.ascensionLevel >= 9) {
            applyToTarget(this, this, new WingsOfGrace(this, WINGS));
        }
    }

    public void awaken() {
        phaseChange();
        if (RuinaMod.isMultiplayerConnected()) {
            makePowerRemovable(this, WhiteNightBlessingMultiplayer.POWER_ID);
            atb(new RemoveSpecificPowerAction(this, this, WhiteNightBlessingMultiplayer.POWER_ID));
        } else {
            makePowerRemovable(this, WhiteNightBlessing.POWER_ID);
            atb(new RemoveSpecificPowerAction(this, this, WhiteNightBlessing.POWER_ID));
        }
        atb(new RollMoveAction(this));
    }

    private void phaseChange() {
        waitAnimation(1.0f);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.fadeIn(2.5f);
                CustomDungeon.playTempMusicInstantly("WhiteNightBGM");
                playSound("WhiteNightAppear");
                AbstractDungeon.effectsQueue.add(new WhiteNightAura(hb.cX + (10.0f * Settings.scale), hb.cY));
                WhiteNight.this.animation = null;
                loadAnimation(makeMonsterPath("WhiteNight/atlas/WhiteNight.atlas"), makeMonsterPath("WhiteNight/atlas/WhiteNight.json"), 1.5F);
                AnimationState.TrackEntry e = WhiteNight.this.state.setAnimation(0, "normal", true);
                e.setTime(e.getEndTime() * MathUtils.random());
                WhiteNight.this.state.setTimeScale(1.0F);
                WhiteNight.this.flipHorizontal = true;
                setPhase(AWAKE_PHASE);
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case PRAYER:
                playSoundAnimation("ProphetBless");
                applyToTarget(this, this, new RitualPower(this, RITUAL, false));
                waitAnimation();
                break;
            case RISE_AND_SERVE:
                shockwaveEffect();
                dmg(adp(), info);
                waitAnimation();
                Texture apostles = TexLoader.getTexture(makeMonsterPath("Seraphim/Apostles.png"));
                playSoundAnimation("WhiteNightSummon");
                flashImageVfx(apostles, 2.0f);
                Summon();
                break;
            case BENEDICTION:
                playSoundAnimation("ProphetBless");
                int ritAmt = 0;
                AbstractPower ritPower = this.getPower(RitualPower.POWER_ID);
                if (ritPower != null) {
                    ritAmt = ritPower.amount;
                }
                ritAmt = Math.max(ritAmt, RITUAL);
                for (AbstractMonster m : monsterList()) {
                    if (!m.isDeadOrEscaped()) {
                        if (m == this) {
                            applyToTarget(this, this, new StrengthPower(this, ritAmt));
                        } else {
                            applyToTarget(m, this, new RitualPower(m, ritAmt, false));
                        }
                    }
                }
                waitAnimation();
                break;
            case SALVATION:
                playSoundAnimation("ProphetBless");
                for (AbstractMonster m : monsterList()) {
                    if (!m.isDeadOrEscaped()) {
                        atb(new HealAction(m, this, HEAL));
                        block(m, BLOCK);
                    }
                }
                waitAnimation();
                break;
            case BEHOLD:
                shockwaveEffect();
                dmg(adp(), info);
                waitAnimation();
                break;
            case SAVIOR:
                playSoundAnimation("WhiteNightSummon");
                Summon();
                waitAnimation();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (phase == DEFAULT_PHASE) {
            setMoveShortcut(PRAYER);
        } else {
            if (lastMove(PRAYER)) {
                setMoveShortcut(RISE_AND_SERVE);
            } else if (lastMoveIgnoringMove(SALVATION, SAVIOR)) {
                setMoveShortcut(BEHOLD);
            } else if (!areMinionsAlive()) {
                setMoveShortcut(SAVIOR);
            } else if (lastMoveIgnoringMove(BENEDICTION, SAVIOR)) {
                setMoveShortcut(SALVATION);
            } else {
                setMoveShortcut(BENEDICTION);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PRAYER: {
                DetailedIntent detail = new DetailedIntent(this, RITUAL, DetailedIntent.RITUAL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case RISE_AND_SERVE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                detailsList.add(detail);
                break;
            }
            case BENEDICTION: {
                int ritAmt = 0;
                AbstractPower ritPower = this.getPower(RitualPower.POWER_ID);
                if (ritPower != null) {
                    ritAmt = ritPower.amount;
                }
                ritAmt = Math.max(ritAmt, RITUAL);
                DetailedIntent detail = new DetailedIntent(this, ritAmt, DetailedIntent.RITUAL_TEXTURE, DetailedIntent.TargetType.ALL_MINIONS);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, ritAmt, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case SALVATION: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, HEAL, DetailedIntent.HEAL_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail2);
                break;
            }
            case SAVIOR: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (mo.hasPower(MinionPower.POWER_ID)) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
    }

    public void Summon() {
        float xPos_Middle_L = -650F;
        float xPos_Short_L = -350F;
        float xPos_Shortest_L = 250F;
        AbstractMonster apostle1 = new ScytheApostle(xPos_Middle_L, 0.0f);
        atb(new SpawnMonsterAction(apostle1, true));
        atb(new UsePreBattleActionAction(apostle1));

        AbstractMonster apostle2 = new SpearApostle(xPos_Short_L, 0.0f);
        atb(new SpawnMonsterAction(apostle2, true));
        atb(new UsePreBattleActionAction(apostle2));

        AbstractMonster apostle3 = new StaffApostle(xPos_Shortest_L, 0.0f);
        atb(new SpawnMonsterAction(apostle3, true));
        atb(new UsePreBattleActionAction(apostle3));
    }

    private void shockwaveEffect() {
        float y = this.hb.cY;
        float x = this.hb.cX;
        float chargeDuration = 1.2f;
        AbstractGameEffect shockwaveCharge = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, x, y, chargeDuration)
                .scale(4.0f, 0.0f, VfxBuilder.Interpolations.LINEAR)
                .playSoundAt(0.0f, makeID("WhiteNightCharge"))
                .rotate(-600f)
                .build();
        float burstDuration = 0.7f;
        AbstractGameEffect shockwaveBurst = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, x, y, burstDuration)
                .scale(0.0f, 8.0f, VfxBuilder.Interpolations.SWING)
                .playSoundAt(0.0f, makeID("WhiteNightFire"))
                .build();
        atb(new VFXAction(shockwaveCharge, chargeDuration));
        atb(new VFXAction(shockwaveBurst, burstDuration - 0.3f));
    }

    private boolean areMinionsAlive() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if ((mo instanceof ScytheApostle || mo instanceof SpearApostle || mo instanceof StaffApostle) && !mo.isDeadOrEscaped()) {
                return true;
            }
        }
        return false;
    }

}