package ruina.monsters.uninvitedGuests.normal.clown;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.clown.oswaldCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act4.Brainwash;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleDivinityParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Oswald extends AbstractCardMonster
{
    public static final String ID = makeID(Oswald.class.getSimpleName());

    private static final byte CLIMAX = 0;
    private static final byte FUN = 1;
    private static final byte CATCH = 2;
    private static final byte POW = 3;
    private static final byte BRAINWASH = 4;

    public final int funHits = 2;
    public final int climaxDamage = calcAscensionDamage(6);
    public int climaxHits = 4;
    public final int climaxHitIncrease = calcAscensionSpecial(1);

    public final int STATUS = calcAscensionSpecial(3);
    public final int STRENGTH = calcAscensionSpecial(1);
    public final int WEAK = calcAscensionSpecial(2);
    public final int ALLY_DEBUFF = 1;

    public Oswald() {
        this(0.0f, 0.0f);
    }

    public Oswald(final float x, final float y) {
        super(ID, ID, 700, -5.0F, 0, 200.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Oswald/Spriter/Oswald.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(700));

        addMove(CLIMAX, Intent.ATTACK, climaxDamage, climaxHits);
        addMove(FUN, Intent.ATTACK, calcAscensionDamage(11), funHits);
        addMove(CATCH, Intent.DEBUFF);
        addMove(POW, Intent.BUFF);
        addMove(BRAINWASH, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Tiph) {
                target = (Tiph)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new Brainwash(this, 1));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case CLIMAX: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == multiplier - 1) {
                        specialAttackAnimation(target);
                    } else if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    waitAnimation();
                }
                resetIdle();
                climaxHits += climaxHitIncrease;
                addMove(CLIMAX, Intent.ATTACK, climaxDamage, climaxHits, true);
                break;
            }
            case FUN: {
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
            case CATCH: {
                buffAnimation();
                intoDrawMo(new Wound(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case POW: {
                buffAnimation();
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case BRAINWASH: {
                specialAnimation();
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, ALLY_DEBUFF, true));
                applyToTarget(target, this, new VulnerablePower(target, ALLY_DEBUFF, true));
                resetIdle();
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "OswaldHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "OswaldStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "OswaldVert", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "OswaldAttract", this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special", "OswaldFinish", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Block", "OswaldLaugh", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CLIMAX)) {
            setMoveShortcut(FUN, MOVES[FUN], getMoveCardFromByte(FUN));
        } else if (this.lastMove(FUN)) {
            setMoveShortcut(CATCH, MOVES[CATCH], getMoveCardFromByte(CATCH));
        } else {
            setMoveShortcut(CLIMAX, MOVES[CLIMAX], getMoveCardFromByte(CLIMAX));
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(BRAINWASH, moveHistory) || this.lastMove(CATCH, moveHistory)) {
            setAdditionalMoveShortcut(FUN, moveHistory, getMoveCardFromByte(FUN));
        } else if (this.lastMove(FUN, moveHistory)) {
            setAdditionalMoveShortcut(POW, moveHistory, getMoveCardFromByte(POW));
        } else {
            if (firstMove || (target != null && !target.isDead && !target.isDying)) {
                setAdditionalMoveShortcut(BRAINWASH, moveHistory, getMoveCardFromByte(BRAINWASH));
            } else {
                setAdditionalMoveShortcut(CATCH, moveHistory, getMoveCardFromByte(CATCH));
            }
        }
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new Climax(this));
        list.add(new Fun(this));
        list.add(new Catch(this));
        list.add(new Pow(this));
        list.add(new WeNeedYou(this));
        return list.get(move);
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (additionalMove.nextMove == POW) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (target instanceof Tiph) {
            ((Tiph) target).onBossDeath();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(Brainwash.POWER_ID) && this.getPower(Brainwash.POWER_ID).amount > 0) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this, Color.GOLD.cpy()));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(Color.GOLD.cpy(), this));
            }
        }
    }

}