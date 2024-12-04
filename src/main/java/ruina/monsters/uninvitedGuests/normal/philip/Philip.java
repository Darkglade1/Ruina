package ruina.monsters.uninvitedGuests.normal.philip;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.philip.philipCards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.FlameShield;
import ruina.powers.InvisibleBarricadePower;
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
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

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
    public final int damageBonus = calcAscensionSpecial(30);
    public final int damageReduction = calcAscensionSpecial(60);
    public final int damageReductionDecay = calcAscensionSpecial(10);
    public int TURNS_TILL_BONUS_INTENT = 3;
    public boolean gotBonusIntent = false;
    public int TURNS_TILL_BONUS_DAMAGE = 6;
    public boolean gotBonusDamage = false;
    public Malkuth malkuth;
    private int phase = 1;
    private boolean attackingAlly = AbstractDungeon.monsterRng.randomBoolean();

    AbstractCard status = new Burn();

    public AbstractMonster[] minions = new AbstractMonster[2];

    public static final String POWER_ID = makeID("Passion");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String DAMAGE_POWER_ID = makeID("Embers");
    public static final PowerStrings DAMAGE_powerStrings = CardCrawlGame.languagePack.getPowerStrings(DAMAGE_POWER_ID);
    public static final String DAMAGE_POWER_NAME = DAMAGE_powerStrings.NAME;
    public static final String[] DAMAGE_POWER_DESCRIPTIONS = DAMAGE_powerStrings.DESCRIPTIONS;

    public Philip() {
        this(0.0f, 0.0f);
    }

    public Philip(final float x, final float y) {
        super(NAME, ID, 700, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Philip/Spriter/Philip.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        maxAdditionalMoves = 2;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(700));

        addMove(EVENTIDE, Intent.DEBUFF);
        addMove(EMOTIONS, Intent.DEFEND_BUFF);
        addMove(STIGMATIZE, Intent.ATTACK, calcAscensionDamage(9), stigmatizeHits, true);
        addMove(SEARING, Intent.ATTACK_DEBUFF, calcAscensionDamage(17));
        addMove(SORROW, Intent.ATTACK, calcAscensionDamage(6), sorrowHits, true);

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
                malkuth = (Malkuth)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, damageReduction) {
            @Override
            public void onInitialApplication() {
                amount2 = damageReductionDecay;
                updateDescription();
            }

            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL) {
                    return damage * (1.0f - ((float)amount / 100));
                } else {
                    return damage;
                }
            }

            @Override
            public void atEndOfRound() {
                if (amount <= amount2) {
                    makePowerRemovable(this);
                    makePowerRemovable(owner, FlameShield.POWER_ID);
                    atb(new RemoveSpecificPowerAction(owner, owner, FlameShield.POWER_ID));
                }
                atb(new ReducePowerAction(owner, owner, this, amount2));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
            }
        });
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new FlameShield(this, 1));
        }
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
        animationAction("Blunt" + phase, "PhilipHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce" + phase, "PhilipStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash" + phase, "PhilipVert", enemy, this);
    }

    private void rangeAnimation(AbstractCreature enemy) {
        animationAction("Far" + phase, "PhilipExplosion", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Guard" + phase, "FireGuard", this);
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
                takeCustomTurn(additionalMove, malkuth);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                TURNS_TILL_BONUS_INTENT--;
                if (TURNS_TILL_BONUS_INTENT <= 0 && !gotBonusIntent) {
                    getAnotherIntent();
                }
                TURNS_TILL_BONUS_DAMAGE--;
                if (TURNS_TILL_BONUS_DAMAGE <= 0 && !gotBonusDamage) {
                    getBonusDamage();
                }
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    private void getAnotherIntent() {
        numAdditionalMoves++;
        gotBonusIntent = true;
        playSound("PhilipTransform", 2.0f);
        Summon();
        atb(new RollMoveAction(malkuth)); //to make her roll for mass attack if she can
    }

    private void getBonusDamage() {
        gotBonusDamage = true;
        phase = 2;
        playSound("PhilipTransform", 2.0f);
        runAnim("Idle" + phase);
        applyToTarget(this, this, new AbstractLambdaPower(DAMAGE_POWER_NAME, DAMAGE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, damageBonus) {
            @Override
            public void onInitialApplication() {
                this.priority = 99;
            }

            @Override
            public float atDamageGive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL) {
                    return damage * (1 + ((float)amount / 100));
                } else {
                    return damage;
                }
            }

            @Override
            public void updateDescription() {
                description = DAMAGE_POWER_DESCRIPTIONS[0] + amount + DAMAGE_POWER_DESCRIPTIONS[1];
            }
        });
        Summon();
        atb(new RollMoveAction(malkuth)); //to make her roll for mass attack if she can
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!gotBonusIntent) {
            if (!this.lastMove(EVENTIDE)) {
                possibilities.add(EVENTIDE);
            }
        }
        if (!this.gotBonusDamage) {
            if (!this.lastMove(SEARING)) {
                possibilities.add(SEARING);
            }
        }
        if (gotBonusIntent) {
            if (!this.lastMove(STIGMATIZE)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (gotBonusDamage) {
            if (!this.lastMove(SORROW)) {
                possibilities.add(SORROW);
            }
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SEARING, moveHistory)) {
            possibilities.add(SEARING);
        }
        if (whichMove == 0 && this.gotBonusIntent) {
            if (!this.lastMove(STIGMATIZE, moveHistory)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (whichMove == 1 && this.gotBonusDamage) {
            if (!this.lastMove(STIGMATIZE, moveHistory)) {
                possibilities.add(STIGMATIZE);
            }
        }
        if (whichMove == 0 && !gotBonusIntent && !gotBonusDamage) {
            if (!this.lastMove(EMOTIONS, moveHistory)) {
                possibilities.add(EMOTIONS);
            }
        }
        if (whichMove == 1 && gotBonusIntent && !gotBonusDamage) {
            if (!this.lastMove(EMOTIONS, moveHistory)) {
                possibilities.add(EMOTIONS);
            }
        }
        if (gotBonusDamage) {
            if (!this.lastMove(SORROW, moveHistory)) {
                possibilities.add(SORROW);
            }
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                if (i == 0) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, malkuth, malkuth.icon);
                } else {
                    if (attackingAlly) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, malkuth, malkuth.icon);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                }
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
        malkuth.onBossDeath();
    }

    public void Summon() {
        malkuth.massAttackCooldownCounter = 0;
        //float xPos_Farthest_L = -450.0f;
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

}