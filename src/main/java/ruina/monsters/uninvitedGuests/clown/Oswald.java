package ruina.monsters.uninvitedGuests.clown;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Oswald extends AbstractCardMonster
{
    public static final String ID = makeID(Oswald.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte CLIMAX = 0;
    private static final byte FUN = 1;
    private static final byte CATCH = 2;
    private static final byte POW = 3;
    private static final byte BRAINWASH = 4;

    public final int funHits = 2;
    public final int climaxDamage = calcAscensionDamage(4);
    public int climaxHits = 3;

    public final int STATUS = calcAscensionSpecial(2);
    public final int STRENGTH = calcAscensionSpecial(3);
    public final int WEAK = calcAscensionSpecial(2);
    public final int BRAINWASH_LENGTH = 2;
    public final int BRAINWASH_HITS = 4;
    public Tiph tiph;

    public static final String POWER_ID = makeID("Brainwash");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Oswald() {
        this(0.0f, 0.0f);
    }

    public Oswald(final float x, final float y) {
        super(NAME, ID, 650, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Oswald/Spriter/Oswald.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(CLIMAX, Intent.ATTACK, climaxDamage, climaxHits, true);
        addMove(FUN, Intent.ATTACK, calcAscensionDamage(8), funHits, true);
        addMove(CATCH, Intent.DEBUFF);
        addMove(POW, Intent.BUFF);
        addMove(BRAINWASH, Intent.STRONG_DEBUFF);

//        cardList.add(new Circulation(this));
//        cardList.add(new Nails(this));
//        cardList.add(new Siphon(this));
//        cardList.add(new Bloodspreading(this));
//        cardList.add(new Inject(this));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Tiph) {
                tiph = (Tiph)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
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
                    resetIdle();
                }
                climaxHits++;
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
                resetIdle();
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
                applyToTarget(target, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.DEBUFF, false, target, BRAINWASH_LENGTH) {
                    boolean justApplied = true;

                    @Override
                    public void onInitialApplication() {
                        if (owner instanceof AbstractAllyMonster) {
                            owner.halfDead = false;
                            ((AbstractAllyMonster) owner).isAlly = false;
                            ((AbstractAllyMonster) owner).setAnimationFlip(false, false);
                        }
                        if (owner instanceof Tiph) {
                            ((Tiph) owner).onBrainwashed();
                        }
                        amount2 = BRAINWASH_HITS;
                        updateDescription();
                    }

                    @Override
                    public int onAttacked(DamageInfo info, int damageAmount) {
                        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != owner && damageAmount > 0) {
                            flash();
                            amount2--;
                            if (amount2 <= 0) {
                                atb(new RemoveSpecificPowerAction(owner, owner, this));
                            }
                            updateDescription();
                        }
                        return damageAmount;
                    }

                    @Override
                    public void onRemove() {
                        if (owner instanceof AbstractAllyMonster) {
                            owner.halfDead = true;
                            ((AbstractAllyMonster) owner).isAlly = true;
                            ((AbstractAllyMonster) owner).setAnimationFlip(true, false);
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            atb(new ReducePowerAction(owner, owner, this, 1));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
                    }
                });
                resetIdle(1.0f);
                break;
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
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, tiph);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CLIMAX)) {
            setMoveShortcut(FUN, MOVES[FUN], cardList.get(FUN).makeStatEquivalentCopy());
        } else if (this.lastMove(FUN)) {
            setMoveShortcut(CATCH, MOVES[CATCH], cardList.get(CATCH).makeStatEquivalentCopy());
        } else {
            setMoveShortcut(CLIMAX, MOVES[CLIMAX], cardList.get(CLIMAX).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(BRAINWASH, moveHistory) || this.lastMove(CATCH, moveHistory)) {
            setAdditionalMoveShortcut(FUN, moveHistory, cardList.get(FUN).makeStatEquivalentCopy());
        } else if (this.lastMove(FUN, moveHistory)) {
            setAdditionalMoveShortcut(POW, moveHistory, cardList.get(POW).makeStatEquivalentCopy());
        } else {
            if (!tiph.isDead && !tiph.isDying) {
                setAdditionalMoveShortcut(BRAINWASH, moveHistory, cardList.get(BRAINWASH).makeStatEquivalentCopy());
            } else {
                setAdditionalMoveShortcut(CATCH, moveHistory, cardList.get(CATCH).makeStatEquivalentCopy());
            }
        }
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        switch (move){
            default: return new Madness();
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                if (additionalMove.nextMove == BRAINWASH) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, tiph, tiph.allyIcon);
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        tiph.onBossDeath();
    }

}