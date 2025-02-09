package ruina.monsters.uninvitedGuests.normal.philip;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.philip.philipCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act4.Embers;
import ruina.powers.act4.Passion;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Philip extends AbstractCardMonster
{
    public static final String ID = makeID(Philip.class.getSimpleName());

    private static final byte EVENTIDE = 0;
    private static final byte EMOTIONS = 1;
    private static final byte STIGMATIZE = 2;
    private static final byte SEARING = 3;
    private static final byte SORROW = 4;

    public final int stigmatizeHits = 2;
    public final int sorrowHits = 3;

    public final int BLOCK = calcAscensionTankiness(10);
    public final int EVENTIDE_BURNS = 3;
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int SEARING_BURNS = 1;
    public final int damageBonus = calcAscensionSpecial(33);
    public final int damageReduction = calcAscensionSpecial(60);
    public final int damageReductionDecay = calcAscensionSpecial(10);

    public int TURNS_FOR_BONUS_INTENT = 3;
    public static final int BONUS_INTENT_PHASE = 2;
    public int TURNS_FOR_BONUS_DAMAGE = 6;
    public static final int BONUS_DAMAGE_PHASE = 3;

    AbstractCard status = new Burn();

    public AbstractMonster[] minions = new AbstractMonster[2];

    public Philip() {
        this(0.0f, 0.0f);
    }

    public Philip(final float x, final float y) {
        super(ID, ID, 700, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Philip/Spriter/Philip.scml"));
        setNumAdditionalMoves(2);
        this.numAdditionalMoves = 1; //only starts with 1 extra intent
        this.setHp(calcAscensionTankiness(700));

        addMove(EVENTIDE, Intent.DEBUFF);
        addMove(EMOTIONS, Intent.DEFEND_BUFF);
        addMove(STIGMATIZE, Intent.ATTACK, calcAscensionDamage(9), stigmatizeHits);
        addMove(SEARING, Intent.ATTACK_DEBUFF, calcAscensionDamage(17));
        addMove(SORROW, Intent.ATTACK, calcAscensionDamage(6), sorrowHits);

        cardList.add(new Eventide(this));
        cardList.add(new Emotions(this));
        cardList.add(new Stigmatize(this));
        cardList.add(new Searing(this));
        cardList.add(new Sorrow(this));

        if (AbstractDungeon.ascensionLevel >= 19) {
            status.upgrade();
        }
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
            if (mo instanceof Malkuth) {
                target = (Malkuth)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new Passion(this, damageReduction, damageReductionDecay));
        applyToTarget(this, this, new InvisibleBarricadePower(this));

        if (RuinaMod.isMultiplayerConnected() && phase >= BONUS_INTENT_PHASE) {
            numAdditionalMoves++;
            runAnim("Idle" + convertPhaseToAnim());
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case EVENTIDE: {
                buffAnimation();
                intoDiscardMo(status.makeStatEquivalentCopy(), EVENTIDE_BURNS, this);
                resetIdle();
                break;
            }
            case EMOTIONS: {
                buffAnimation();
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case STIGMATIZE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        pierceAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case SEARING: {
                slashAnimation(target);
                dmg(target, info);
                intoDrawMo(status.makeStatEquivalentCopy(), SEARING_BURNS, this);
                resetIdle();
                break;
            }
            case SORROW: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == multiplier - 1) {
                        rangeAnimation(target);
                    } else if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    waitAnimation();
                }
                resetIdle();
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt" + convertPhaseToAnim(), "PhilipHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce" + convertPhaseToAnim(), "PhilipStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash" + convertPhaseToAnim(), "PhilipVert", enemy, this);
    }

    private void rangeAnimation(AbstractCreature enemy) {
        animationAction("Far" + convertPhaseToAnim(), "PhilipExplosion", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Guard" + convertPhaseToAnim(), "FireGuard", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (GameActionManager.turn >= TURNS_FOR_BONUS_INTENT && phase == DEFAULT_PHASE) {
                    getAnotherIntent();
                }
                if (GameActionManager.turn >= TURNS_FOR_BONUS_DAMAGE && phase == BONUS_INTENT_PHASE) {
                    getBonusDamage();
                }
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = getMonsterAIRng().randomBoolean();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    private void getAnotherIntent() {
        numAdditionalMoves++;
        setPhase(BONUS_INTENT_PHASE);
        playSound("PhilipTransform", 2.0f);
        Summon();
        atb(new RollMoveAction(target)); //to make malkuth roll for mass attack if she can
    }

    private void getBonusDamage() {
        setPhase(BONUS_DAMAGE_PHASE);
        playSound("PhilipTransform", 2.0f);
        runAnim("Idle" + convertPhaseToAnim());
        applyToTarget(this, this, new Embers(this, damageBonus));
        Summon();
        atb(new RollMoveAction(target)); //to make her roll for mass attack if she can
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + convertPhaseToAnim());
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (phase == DEFAULT_PHASE) {
            if (!this.lastMove(EVENTIDE)) {
                possibilities.add(EVENTIDE);
            }
        }
        if (phase < BONUS_DAMAGE_PHASE) {
            if (!this.lastMove(SEARING)) {
                possibilities.add(SEARING);
            }
        }
        if (phase >= BONUS_INTENT_PHASE) {
            if (!this.lastMove(STIGMATIZE)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (phase == BONUS_DAMAGE_PHASE) {
            if (!this.lastMove(SORROW)) {
                possibilities.add(SORROW);
            }
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SEARING, moveHistory)) {
            possibilities.add(SEARING);
        }
        if (whichMove == 0 && phase >= BONUS_INTENT_PHASE) {
            if (!this.lastMove(STIGMATIZE, moveHistory)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (whichMove == 1 && phase == BONUS_DAMAGE_PHASE) {
            if (!this.lastMove(STIGMATIZE, moveHistory)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (whichMove == 0 && phase == DEFAULT_PHASE) {
            if (!this.lastMove(EMOTIONS, moveHistory)) {
                possibilities.add(EMOTIONS);
            }
        }
        if (whichMove == 1 && phase == BONUS_INTENT_PHASE) {
            if (!this.lastMove(EMOTIONS, moveHistory)) {
                possibilities.add(EMOTIONS);
            }
        }
        if (phase == BONUS_DAMAGE_PHASE) {
            if (!this.lastMove(SORROW, moveHistory)) {
                possibilities.add(SORROW);
            }
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (index == 0) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        } else {
            if (attackingAlly) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
            } else {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof CryingChild) {
                atb(new SuicideAction(mo));
            }
        }
        if (target instanceof Malkuth) {
            ((Malkuth) target).onBossDeath();
        }
    }

    public void Summon() {
        if (target instanceof Malkuth) {
            target.moveHistory.clear();
        }
        float xPos_Middle_L = -175.0f;
        float xPos_Short_L = 0F;
        float y = 125.0f;

        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof CryingChild) {
                //heals all existing minions to full if they're still alive
                atb(new HealAction(mo, this, mo.maxHealth));
            }
        }

        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new CryingChild(xPos_Middle_L, y, this);
                } else {
                    minion = new CryingChild(xPos_Short_L, y, this);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                minions[i] = minion;
            }
        }
    }

    private int convertPhaseToAnim() {
        if (phase == BONUS_DAMAGE_PHASE) {
            return 2;
        }
        return 1;
    }

}