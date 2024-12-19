package ruina.monsters.uninvitedGuests.normal.greta;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.WrathStance;
import ruina.BetterSpriterAnimation;
import ruina.actions.GretaStealCardAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.greta.gretaCards.*;
import ruina.powers.Bleed;
import ruina.powers.CenterOfAttention;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Paralysis;
import ruina.powers.act4.Sharkskin;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Greta extends AbstractCardMonster
{
    public static final String ID = makeID(Greta.class.getSimpleName());

    private static final byte BREAK_EGG = 0;
    private static final byte SLAP = 1;
    private static final byte MINCE = 2;
    private static final byte SEASON = 3;
    private static final byte TRIAL = 4;
    private static final byte SACK = 5;

    public final int minceHits = 2;

    public final int STRENGTH = calcAscensionSpecial(2);
    public final int PARALYSIS = calcAscensionSpecial(2);
    public final int BLEED = calcAscensionSpecial(4);
    public final int BLOCK = calcAscensionTankiness(20);
    public final int DEBUFF = calcAscensionSpecial(2);
    public final int damageReduction = 50;
    public final int debuffCleanseTurns = 3;

    public FreshMeat meat;

    public Greta() {
        this(0.0f, 0.0f);
    }

    public Greta(final float x, final float y) {
        super(ID, ID, 750, -5.0F, 0, 200.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Greta/Spriter/Greta.scml"));
        setNumAdditionalMoves(2);
        this.setHp(calcAscensionTankiness(750));

        addMove(BREAK_EGG, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        addMove(SLAP, Intent.DEFEND_DEBUFF);
        addMove(MINCE, Intent.ATTACK, calcAscensionDamage(9), minceHits);
        addMove(SEASON, Intent.DEBUFF);
        addMove(TRIAL, Intent.ATTACK_BUFF, calcAscensionDamage(19));
        addMove(SACK, Intent.STRONG_DEBUFF);

        cardList.add(new BreakEgg(this));
        cardList.add(new Slap(this));
        cardList.add(new Mince(this));
        cardList.add(new Season(this));
        cardList.add(new Trial(this));
        cardList.add(new Sack(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Hod) {
                target = (Hod)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new Sharkskin(this, 0, damageReduction, debuffCleanseTurns));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new StrengthPower(this, 1)); //hacky solution again LOL
        applyToTarget(this, this, new CenterOfAttention(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case BREAK_EGG: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new Paralysis(target, PARALYSIS));
                resetIdle();
                break;
            }
            case SLAP: {
                blockAnimation();
                block(this, BLOCK);
                if (target == adp()) {
                    applyToTarget(target, this, new Bleed(target, BLEED));
                } else {
                    applyToTarget(target, this, new Bleed(target, BLEED, true));
                }
                resetIdle();
                break;
            }
            case MINCE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case SEASON: {
                debuffAnimation();
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                applyToTarget(target, this, new VulnerablePower(target, DEBUFF, true));
                resetIdle();
                break;
            }
            case TRIAL: {
                specialAttackAnimation(target);
                atb(new VampireDamageActionButItCanFizzle(target, info, AbstractGameAction.AttackEffect.NONE));
                if (!target.isDeadOrEscaped()) {
                    applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                }
                resetIdle();
                break;
            }
            case SACK: {
                sackAnimation(target);
                atb(new GretaStealCardAction(this));
                resetIdle();
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BluntBlow", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special", "GretaEat", enemy, this);
    }

    private void sackAnimation(AbstractCreature enemy) {
        animationAction("Sack", null, enemy, this);
    }

    private void debuffAnimation() {
        animationAction("Range", null, this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear();
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BREAK_EGG) && !this.lastMoveBefore(BREAK_EGG)) {
            possibilities.add(BREAK_EGG);
        }
        if (!this.lastMove(MINCE) && !this.lastMoveBefore(MINCE)) {
            possibilities.add(MINCE);
        }
        if (!this.lastMove(SLAP) && !this.lastMoveBefore(SLAP)) {
            possibilities.add(SLAP);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(MINCE, moveHistory)) {
                possibilities.add(MINCE);
            }
            if (!this.lastMove(SLAP, moveHistory) && !this.lastMoveBefore(SLAP, moveHistory)) {
                possibilities.add(SLAP);
            }
            if (!this.lastMove(SEASON, moveHistory) && !this.lastMoveBefore(SEASON, moveHistory)) {
                possibilities.add(SEASON);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory);
        } else {
            if (meat != null && !meat.isDeadOrEscaped()) {
                setAdditionalMoveShortcut(TRIAL, moveHistory);
            } else {
                setAdditionalMoveShortcut(SACK, moveHistory);
            }
        }
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (index == 1) {
            if (additionalMove.nextMove == TRIAL) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, meat, meat.icon, index);
            } else {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
            }
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof FreshMeat) {
                atb(new SuicideAction(mo));
            }
        }
        if (target instanceof Hod) {
            ((Hod) target).onBossDeath();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(Sharkskin.POWER_ID)) {
            if (this.getPower(Sharkskin.POWER_ID).amount >= debuffCleanseTurns - 1) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(WrathStance.STANCE_ID, this));
                }
            }
        }
    }

}