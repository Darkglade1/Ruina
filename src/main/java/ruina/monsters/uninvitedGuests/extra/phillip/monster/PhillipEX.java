package ruina.monsters.uninvitedGuests.extra.phillip.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
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
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth.*;
import ruina.monsters.uninvitedGuests.normal.philip.CryingChild;
import ruina.monsters.uninvitedGuests.normal.philip.Malkuth;
import ruina.monsters.uninvitedGuests.normal.philip.philipCards.*;
import ruina.monsters.uninvitedGuests.normal.pluto.monster.Pluto;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.PatronLibrarian;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class PhillipEX extends AbstractCardMonster
{
    public static final String ID = makeID(PhillipEX.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte DESPERATION = 0;
    private static final byte EVENTIDE = 1;
    private static final byte STIGMATIZE = 2;
    private static final byte EMOTIONS = 3;
    private static final byte REKINDLED = 4;
    private static final byte RESOLUTION = 5;
    private static final byte SORROW = 6;
    private static final byte FLAMES = 7;

    public enum PHASE{
        T1,
        T2,
        T3,
        T4
    }

    public PHASE currentPhase = PHASE.T1;

    public final int desperationDamage = calcAscensionDamage(35);
    public final int desperationDelayedDamage = 10;

    public final int eventideDamage = calcAscensionDamage(10);
    public final int EVENTIDE_BURNS = 2;

    public final int stigmatizeDamage = calcAscensionDamage(9);
    public final int stigmatizeHits = 3;

    public final int rekindledBlock = calcAscensionTankiness(15);
    public final int rekindledDamage = calcAscensionDamage(25);
    public final int rekindledDelayedDamage = 5;

    public final int searingDamage = calcAscensionDamage(15);
    public final int searingDelayedDamage = 20;

    public final int sorrowDamage = calcAscensionDamage(6);
    public final int sorrowHits = 4;

    public final int flamesDamage = calcAscensionDamage(12);
    public final int flamesHits = 3;


    public final int BLOCK = calcAscensionTankiness(10);
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int SEARING_BURNS = 1;
    public final int damageBonus = calcAscensionSpecial(30);
    public final int damageReduction = 60;
    public final int damageReductionDecay = 10;
    public boolean gotBonusIntent = false;
    public int TURNS_TILL_BONUS_DAMAGE = 6;
    public boolean gotBonusDamage = false;
    private int phase = 1;
    private boolean attackingAlly = AbstractDungeon.monsterRng.randomBoolean();

    public AbstractMonster[] minions = new AbstractMonster[2];

    public static final String POWER_ID = makeID("Passion");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String DAMAGE_POWER_ID = makeID("Embers");
    public static final PowerStrings DAMAGE_powerStrings = CardCrawlGame.languagePack.getPowerStrings(DAMAGE_POWER_ID);
    public static final String DAMAGE_POWER_NAME = DAMAGE_powerStrings.NAME;
    public static final String[] DAMAGE_POWER_DESCRIPTIONS = DAMAGE_powerStrings.DESCRIPTIONS;

    private MalkuthEX malkuthPlayer = null;

    public PhillipEX() {
        this(0.0f, 0.0f);
    }

    public PhillipEX(final float x, final float y) {
        super(NAME, ID, 700, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Philip/Spriter/Philip.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 2;
        maxAdditionalMoves = 2;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(DESPERATION, IntentEnums.MASS_ATTACK, desperationDamage);
        addMove(EVENTIDE, Intent.ATTACK_DEBUFF, eventideDamage);
        addMove(EMOTIONS, Intent.DEFEND_BUFF);
        addMove(REKINDLED, Intent.ATTACK_DEFEND, rekindledDamage);
        addMove(RESOLUTION, Intent.ATTACK_DEBUFF, searingDamage);
        addMove(SORROW, Intent.ATTACK, sorrowDamage, sorrowHits, true);
        addMove(FLAMES, IntentEnums.MASS_ATTACK, flamesDamage, flamesHits, true);

        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
        cardList.add(new Madness());
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        malkuthPlayer = new MalkuthEX(-1600.0F, -30.0f);
        malkuthPlayer.drawX = AbstractDungeon.player.drawX;
        PatronLibrarian power = new PatronLibrarian(AbstractDungeon.player, -1);
        power.updateDescription();
        AbstractDungeon.player.powers.add(power);

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.drawPile.group.clear();
                AbstractDungeon.player.discardPile.group.clear();
                AbstractDungeon.player.exhaustPile.group.clear();
                AbstractDungeon.player.hand.group.clear();
                this.isDone = true;
            }
        });
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> newStartingDeck = new ArrayList<>();
                for(int i = 0; i < 3; i += 1){
                    newStartingDeck.add(new CHRBOSS_CrumbledHope());
                    newStartingDeck.add(new CHRBOSS_TimidEndearment());
                    newStartingDeck.add(new CHRBOSS_Respite());
                }
                for(int i = 0; i < 2; i += 1){
                    newStartingDeck.add(new CHRBOSS_Predation());
                    newStartingDeck.add(new CHRBOSS_RoughLoving());
                    newStartingDeck.add(new CHRBOSS_Ember());
                    newStartingDeck.add(new CHRBOSS_BoostAggression());
                    newStartingDeck.add(new CHRBOSS_EncroachingMalice());
                    newStartingDeck.add(new CHRBOSS_GreenStems());
                }
                newStartingDeck.add(new CHRBOSS_FourthMatchFlame());
                newStartingDeck.add(new CHRBOSS_StrikeOfRetribution());
                newStartingDeck.add(new CHRBOSS_NostalgicEmbrace());
                newStartingDeck.add(new CHRBOSS_RaveningHunger());
                newStartingDeck.add(new CHRBOSS_StigmaOfGlory());
                newStartingDeck.add(new CHRBOSS_IntertwinedVines());
                AbstractDungeon.player.drawPile.group.addAll(newStartingDeck);
                AbstractDungeon.player.drawPile.shuffle();
                this.isDone = true;
            }
        });
        CustomDungeon.playTempMusicInstantly("Ensemble1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Malkuth) {
                //malkuth = (Malkuth)mo;
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
                }
                atb(new ReducePowerAction(owner, owner, this, amount2));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        int[] damageArray;
        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case DESPERATION:
                atb(new SFXAction("MONSTER_COLLECTOR_DEBUFF"));
                atb(new VFXAction(new CollectorCurseEffect(adp().hb.cX, adp().hb.cY)));
                for (int i = 0; i < monsterList().size(); i++) {
                    AbstractMonster mo = monsterList().get(i);
                    if (i == monsterList().size() - 2) {
                        //makes the special effects appear all at once for multiple monsters instead of one-by-one
                        atb(new VFXAction(new CollectorCurseEffect(mo.hb.cX, mo.hb.cY), 2.0F));
                    } else if (mo != this) {
                        atb(new VFXAction(new CollectorCurseEffect(mo.hb.cX, mo.hb.cY)));
                    }
                }
                damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                // apply power to all other later
                /*
                applyToTargetNextTurn(adp(), new Erosion(adp(), EROSION + 1));
                for (AbstractMonster mo : monsterList()) {
                    if (mo != this) {
                        applyToTargetNextTurn(mo, new Erosion(mo, EROSION + 1));
                    }
                }
                 */
                resetIdle();
                break;
            case EVENTIDE: {
                dmg(target, info);
                buffAnimation();
                intoDiscardMo(new Burn(), EVENTIDE_BURNS, this);
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
            case EMOTIONS: {
                buffAnimation();
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case REKINDLED:
                buffAnimation();
                block(this, BLOCK);
                bluntAnimation(target);
                dmg(target, info);
                break;
            case RESOLUTION: {
                slashAnimation(target);
                dmg(target, info);
                // power later
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
            case FLAMES: {
                damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {

                    } else if (i == 1){

                    } else {

                    }
                    atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    resetIdle(1.0f);
                }
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
                takeCustomTurn(additionalMove, adp());
                //takeCustomTurn(additionalMove, malkuth);
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
                switch (currentPhase){
                    case T1:
                        currentPhase = PHASE.T2;
                        break;
                    case T2:
                        currentPhase = PHASE.T3;
                        break;
                    case T3:
                        currentPhase = PHASE.T4;
                        break;
                    case T4:
                        currentPhase = PHASE.T1;
                        break;

                }
                TURNS_TILL_BONUS_DAMAGE--;
                if (TURNS_TILL_BONUS_DAMAGE <= 0 && !gotBonusDamage) { getBonusDamage(); }
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
        //atb(new RollMoveAction(malkuth)); //to make her roll for mass attack if she can
    }

    @Override
    protected void resetIdle(float duration) {
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
        switch (currentPhase){
            case T1:
                setMoveShortcut(DESPERATION, MOVES[DESPERATION], cardList.get(DESPERATION).makeStatEquivalentCopy());
                break;
            case T4:
            case T2:
                setMoveShortcut(EVENTIDE, MOVES[EVENTIDE], cardList.get(EVENTIDE).makeStatEquivalentCopy());
                break;
            case T3:
                setMoveShortcut(REKINDLED, MOVES[REKINDLED], cardList.get(DESPERATION).makeStatEquivalentCopy());
                break;

        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        switch (currentPhase){
            case T1: break;
            case T2:
                System.out.println(whichMove);
                if(whichMove == 0){ setAdditionalMoveShortcut(STIGMATIZE, moveHistory, cardList.get(STIGMATIZE).makeStatEquivalentCopy()); }
                else { setAdditionalMoveShortcut(EMOTIONS, moveHistory, cardList.get(EMOTIONS).makeStatEquivalentCopy()); }
                break;
            case T3:
                if(whichMove == 0){ setAdditionalMoveShortcut(RESOLUTION, moveHistory, cardList.get(RESOLUTION).makeStatEquivalentCopy()); }
                else { setAdditionalMoveShortcut(SORROW, moveHistory, cardList.get(SORROW).makeStatEquivalentCopy()); }
                break;
            case T4:
                if(whichMove == 0){ setAdditionalMoveShortcut(FLAMES, moveHistory, cardList.get(FLAMES).makeStatEquivalentCopy()); }
                else { setAdditionalMoveShortcut(EMOTIONS, moveHistory, cardList.get(EMOTIONS).makeStatEquivalentCopy()); }
                break;

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
                if (i == 0) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    //applyPowersToAdditionalIntent(additionalMove, additionalIntent, malkuth, malkuth.allyIcon);
                } else {
                    if (attackingAlly) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                        //applyPowersToAdditionalIntent(additionalMove, additionalIntent, malkuth, malkuth.allyIcon);
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
        //malkuth.onBossDeath();
    }

    public void Summon() {
        //malkuth.massAttackCooldownCounter = 0;
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

        /*
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
         */
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (malkuthPlayer != null) { malkuthPlayer.render(sb); }
    }

}