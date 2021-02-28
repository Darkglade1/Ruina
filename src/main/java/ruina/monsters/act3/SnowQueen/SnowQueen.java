package ruina.monsters.act3.SnowQueen;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.CalmStance;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.cardmods.FrozenMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.FlexibleCalmParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SnowQueen extends AbstractRuinaMonster
{
    public static final String ID = makeID(SnowQueen.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BLIZZARD = 0;
    private static final byte FRIGID_GAZE = 1;
    private static final byte ICE_SPLINTERS = 2;

    private static final int THRESHOLD = 2;
    private final int DEBUFF = calcAscensionSpecial(3);
    private final int BLOCK = calcAscensionTankiness(16);

    public static final String POWER_ID = makeID("PromiseOfWinter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private PrisonOfIce prison;
    public boolean canBlizzard = true;
    private float particleTimer;
    private float particleTimer2;

    public SnowQueen() {
        this(0.0f, 0.0f);
    }

    public SnowQueen(final float x, final float y) {
        super(NAME, ID, 210, 0.0F, 0, 280.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SnowQueen/Spriter/SnowQueen.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(BLIZZARD, Intent.STRONG_DEBUFF);
        addMove(FRIGID_GAZE, Intent.ATTACK_DEFEND, calcAscensionDamage(15));
        addMove(ICE_SPLINTERS, Intent.ATTACK, calcAscensionDamage(23));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 1) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (this.amount >= THRESHOLD) {
                    this.flash();
                    FrozenMod mod = new FrozenMod();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            CardModifierManager.addModifier(card, mod.makeCopy());
                            this.isDone = true;
                        }
                    });
                    this.amount = 1;
                } else {
                    this.amount++;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        Summon();
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BLIZZARD: {
                specialAnimation(adp());
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        canBlizzard = false;
                        this.isDone = true;
                    }
                });
                break;
            }
            case FRIGID_GAZE: {
                attack2Animation(adp());
                block(this, BLOCK);
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case ICE_SPLINTERS: {
                attack1Animation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (canBlizzard) {
            setMoveShortcut(BLIZZARD, MOVES[BLIZZARD]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(FRIGID_GAZE)) {
                possibilities.add(FRIGID_GAZE);
            }
            if (!this.lastTwoMoves(ICE_SPLINTERS)) {
                possibilities.add(ICE_SPLINTERS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID)) {
            if (this.getPower(POWER_ID).amount == THRESHOLD) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleCalmParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(CalmStance.STANCE_ID, this));
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof PrisonOfIce) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void Summon() {
        float xPosition = -400.0F;
        prison = new PrisonOfIce(xPosition, 0.0f, this);
        atb(new SpawnMonsterAction(prison, true));
        atb(new UsePreBattleActionAction(prison));
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "SnowAttack", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "SnowAttackFar", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "SnowBlizzard", enemy, this);
    }

}