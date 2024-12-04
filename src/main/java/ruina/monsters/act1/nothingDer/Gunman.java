package ruina.monsters.act1.nothingDer;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.TexLoader;
import ruina.vfx.FlexibleDivinityParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Gunman extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Gunman.class.getSimpleName());

    public static final String LASER = RuinaMod.makeMonsterPath("Gunman/Laser.png");
    private static final Texture LASER_TEXTURE = TexLoader.getTexture(LASER);

    private static final byte RUTHLESS_BULLETS = 0;
    private static final byte INEVITABLE_BULLET = 1;
    private static final byte SILENT_SCOPE = 2;
    private static final byte MAGIC_BULLET = 3;
    private static final byte DEATH_MARK = 4;

    private static final int MASS_ATTACK_COOLDOWN = 2;
    private int counter = MASS_ATTACK_COOLDOWN;

    private final int BLOCK = calcAscensionTankiness(10);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int VULNERABLE = 1;
    private final int SEVENGTH_BULLET = 7;
    private boolean powerTriggered = false;

    public static final String POWER_ID = makeID("SeventhBullet");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Gunman() {
        this(100.0f, 0.0f);
    }

    public Gunman(final float x, final float y) {
        super(ID, ID, 180, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gunman/Spriter/Gunman.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(180));

        addMove(RUTHLESS_BULLETS, IntentEnums.MASS_ATTACK, calcAscensionDamage(17));
        addMove(INEVITABLE_BULLET, Intent.ATTACK, calcAscensionDamage(7));
        addMove(SILENT_SCOPE, Intent.DEFEND_DEBUFF);
        addMove(MAGIC_BULLET, Intent.ATTACK, 15);
        addMove(DEATH_MARK, Intent.DEBUFF);

        this.icon = makeUIPath("GunIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof NothingThere) {
                target = (NothingThere)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                if (card.type == AbstractCard.CardType.ATTACK) {
                    flashWithoutSound();
                    amount++;
                    if (amount % SEVENGTH_BULLET == 0) {
                        flash();
                        powerTriggered = true;
                        amount = 0;
                        updateDescription();
                    }
                }
            }

            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && damageAmount > 0 && powerTriggered) {
                    flash();
                    att(new LoseHPAction(target, Gunman.this, damageAmount));
                }
            }

            @Override
            public void atEndOfRound() {
                if (powerTriggered) {
                    powerTriggered = false;
                    updateDescription();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + SEVENGTH_BULLET + POWER_DESCRIPTIONS[1];
                if (powerTriggered) {
                    description += POWER_DESCRIPTIONS[2];
                }
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        super.takeCustomTurn(move, target);
        switch (move.nextMove) {
            case RUTHLESS_BULLETS: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                massAttackAnimation(target);
                waitAnimation();
                massAttackEffect();
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                waitAnimation();
                counter = MASS_ATTACK_COOLDOWN + 1;
                break;
            }
            case INEVITABLE_BULLET: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case SILENT_SCOPE: {
                blockAnimation();
                block(this, BLOCK);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                resetIdle(1.0f);
                break;
            }
            case MAGIC_BULLET: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case DEATH_MARK: {
                specialAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void massAttackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BulletFinalShot", enemy, this);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BulletShot", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Dodge", null, this);
    }

    private void specialAnimation() {
        animationAction("Block", "BulletFlame", this);
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[1]));
        }
        super.takeTurn();
        counter--;
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (counter <= 0) {
            setMoveShortcut(RUTHLESS_BULLETS, MOVES[RUTHLESS_BULLETS]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(INEVITABLE_BULLET)) {
                possibilities.add(INEVITABLE_BULLET);
            }
            if (!this.lastMove(SILENT_SCOPE)) {
                possibilities.add(SILENT_SCOPE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (counter == 1) {
            setAdditionalMoveShortcut(DEATH_MARK, moveHistory);
        } else {
            setAdditionalMoveShortcut(MAGIC_BULLET, moveHistory);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!target.isDeadOrEscaped() && target instanceof NothingThere) {
            ((NothingThere)target).onGunManDeath();
            AbstractDungeon.onModifyPower();
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID)) {
            if (this.getPower(POWER_ID).amount == SEVENGTH_BULLET - 1 || powerTriggered) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(DivinityStance.STANCE_ID, this));
                }
            }
        }
    }

    public void onNothingDeath() {
        atb(new TalkAction(this, DIALOG[2]));
    }

    private void massAttackEffect() {
        float duration = 0.7f;
        AbstractGameEffect effect = new VfxBuilder(LASER_TEXTURE, (float)Settings.WIDTH * 1.5f, this.hb.cY, duration)
                .moveX((float)Settings.WIDTH * 1.5f, -(float)Settings.WIDTH / 2)
                .build();
        atb(new VFXAction(effect, duration));
    }

}