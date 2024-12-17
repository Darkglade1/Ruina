package ruina.monsters.uninvitedGuests.normal.elena;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.elena.elenaCards.*;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Protection;
import ruina.powers.act4.BloodRed;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.ThirstEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Elena extends AbstractCardMonster
{
    public static final String ID = makeID(Elena.class.getSimpleName());

    private static final byte CIRCULATION = 0;
    private static final byte SANGUINE_NAILS = 1;
    private static final byte SIPHON = 2;
    private static final byte BLOODSPREADING = 3;
    private static final byte INJECT = 4;

    public final int sanguineNailsHits = 2;

    public final int PROTECTION = calcAscensionSpecial(3);
    public final int STRENGTH = calcAscensionSpecial(3);
    public final int FRAIL = calcAscensionSpecial(2);
    public final int BLEED = calcAscensionSpecial(10);
    public final int INJECT_STR = calcAscensionSpecial(1);
    public VermilionCross vermilionCross;

    public Elena() {
        this(0.0f, 0.0f);
    }

    public Elena(final float x, final float y) {
        super(ID, ID, 500, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Elena/Spriter/Elena.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(500));

        addMove(CIRCULATION, Intent.BUFF);
        addMove(SANGUINE_NAILS, Intent.ATTACK, calcAscensionDamage(9), sanguineNailsHits);
        addMove(SIPHON, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BLOODSPREADING, Intent.ATTACK, calcAscensionDamage(38));
        addMove(INJECT, Intent.STRONG_DEBUFF);

        cardList.add(new Circulation(this));
        cardList.add(new Nails(this));
        cardList.add(new Siphon(this));
        cardList.add(new Bloodspreading(this));
        cardList.add(new Inject(this));

        this.icon = TexLoader.getTexture(makeUIPath("ElenaIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble3");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Binah) {
                target = (Binah)mo;
            }
            if (mo instanceof VermilionCross) {
                vermilionCross = (VermilionCross)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new BloodRed(this));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case CIRCULATION: {
                buffAnimation();
                AbstractCreature buffTarget = vermilionCross;
                if (buffTarget.isDeadOrEscaped()) {
                    buffTarget = this;
                }
                applyToTarget(buffTarget, this, new StrengthPower(buffTarget, STRENGTH));
                applyToTarget(buffTarget, this, new Protection(buffTarget, PROTECTION));
                resetIdle(1.0f);
                break;
            }
            case SANGUINE_NAILS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case SIPHON: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new FrailPower(target, FRAIL, true));
                resetIdle();
                break;
            }
            case BLOODSPREADING: {
                specialAnimationNoSound();
                final AbstractGameEffect[] vfx = {null};
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(vfx[0] == null){
                            vfx[0] = new ThirstEffect();
                            AbstractDungeon.topLevelEffectsQueue.add(vfx[0]);
                        }
                        else {
                            isDone = vfx[0].isDone;
                        }
                    }
                });
                specialAttackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case INJECT: {
                specialAnimation();
                applyToTarget(target, this, new Bleed(target, BLEED));
                applyToTarget(this, this, new StrengthPower(this, INJECT_STR));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "SwordHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SwordVert", enemy, this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special", "ElenaStrongAtk", enemy, this);
    }

    private void specialAnimationNoSound() {
        animationAction("Special", null, this);
    }

    private void specialAnimation() {
        animationAction("Special", "ElenaStrongUp", this);
    }

    private void buffAnimation() {
        animationAction("Block", "ElenaStrongStart", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SANGUINE_NAILS) && !this.lastMoveBefore(SANGUINE_NAILS)) {
            possibilities.add(SANGUINE_NAILS);
        }
        if (!this.lastMove(SIPHON) && !this.lastMoveBefore(SIPHON)) {
            possibilities.add(SIPHON);
        }
        if (!this.lastMove(CIRCULATION) && !this.lastMoveBefore(CIRCULATION)) {
            possibilities.add(CIRCULATION);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BLOODSPREADING, moveHistory) && !this.lastMoveBefore(BLOODSPREADING, moveHistory)) {
            possibilities.add(BLOODSPREADING);
        }
        if (!this.lastMove(INJECT, moveHistory) && !this.lastMoveBefore(INJECT, moveHistory)) {
            possibilities.add(INJECT);
        }
        if (!this.lastMove(SANGUINE_NAILS, moveHistory) && !this.lastMoveBefore(SANGUINE_NAILS, moveHistory)) {
            possibilities.add(SANGUINE_NAILS);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (additionalMove.nextMove == SANGUINE_NAILS) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
        }
    }

    public void onVermilionDeath() {
        if (!this.isDeadOrEscaped()) {
            atb(new TalkAction(this, DIALOG[1]));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        target.target = vermilionCross;
        if (vermilionCross.isDeadOrEscaped() && target instanceof Binah) {
            ((Binah) target).onBossDeath();
        }
    }

}