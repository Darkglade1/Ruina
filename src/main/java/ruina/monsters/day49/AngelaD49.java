package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.act2.MeltedCorpses;
import ruina.monsters.act3.seraphim.GuardianApostle;
import ruina.monsters.day49.Aspiration.LungsOfCravingD49;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitHorizontal;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitVertical;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.Paralysis;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class AngelaD49 extends AbstractCardMonster
{
    public static final String ID = makeID(AngelaD49.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public enum PHASE {
        WRISTCUTTER,
        ASPIRATION,
        PINOCCHIO,
        SNOWQUEEN,
        SILENTGIRL
    }
    public PHASE currentPhase = PHASE.WRISTCUTTER;

    private int turnCounter = 1;

    // Unknown Intents
    private static final byte NONE = 99;
    private static final byte SPAWN_LUNGS = 98;
    // Phase Shifters
    private static final byte PHASE_SHIFT_ASPIRATION = 97;
    private static final byte PHASE_SHIFT_PINOCCHIO = 96;
    // BloodBath Stage
    private final int BLOODBATH_PHASE_HP = 400;
    private static final byte NUMBNESS = 0;
    private static final byte PALE_HANDS = 1;
    private static final byte PROFOUND_SORROW = 2;
    private static final byte SINKING = 3;
    private static final int numbnessBlock = 20;
    private static final int numbnessParalysis = 3;
    private static final int paleHandsDamage = 11;
    private static final int profoundSorrowDamage = 8;
    private static final int profoundSorrowHits = 2;
    private static final int profoundSorrowLoseTempStrength = -3;
    private static final int sinkingDamage = 40;
    private static final int sinkingDepression = 1;
    // Numbness -> Pale Hands -> Sorrow -> Numbness -> wristCutter

    // Aspiration Stage
    private final int ASPIRATION_PHASE_HP = 600;
    private final int ASPIRATION_PHASE_MINION_HPLOSS = 250;
    private static final byte PULSATION = 4;
    private static final byte ASPIRATION = 5;

    private static final int pulsationDamage = 40;
    private static final int aspirationDamage = 4;
    private static final int aspirationHits = 3;

    // null -> null -> null -> unknown intent (Lungs die here) -> offensive turn -> stun -> repeat cycle

    //  Pinocchio Stage
    private final int PINOCCHIO_PHASE_HP = 400;
    private static final byte SHYNESS = 9;
    private static final byte INVERTED_SHYNESS = 10;
    private static final byte BLOODY_WINGS = 11;
    private static final byte INVERTED_BLOODY_WINGS = 12;
    private static final byte TOKEN_OF_FRIENDSHIP = 13;
    private static final byte INVERTED_TOKEN_OF_FRIENDSHIP = 14;
    private static final byte DISPLAY_OF_AFFECTION = 15;
    private static final byte INVERTED_DISPLAY_OF_AFFECTION = 16;
    private static final byte COFFIN = 17;
    private static final byte INVERTED_COFFIN = 18;
    private static final int shynessBlock = 5;
    private static final int invertedShynessDamage = 5;
    private static final int bloodyWingsDamage = 12;
    private static final int bloodyWingsBleed = 3;
    private static final int invertedBloodyWingsBlock = 12;
    private static final int tokenOfFriendshipBlock = 8;
    private static final int tokenOfFriendshipDamage = 5;
    private static final int tokenOfFriendshipHeal = 6;
    private static final int invertedTokenOfFriendshipDamage = 8;
    private static final int invertedTokenOfFriendshipBlock = 5;

    // Inverted Deals delayed damage later.
    private static final int displayOfAffectionDamage = 11;
    private static final int displayOfAffectionHits = 2;

    // Inverted Gains Block on sap instead.
    private static final int coffinDamage = 10;

    // Faulty Strings - Gains Strength Next Turn equal to unblocked damage.
    // I don't have it! - Every Other Turn, Completely Negate all Strength on this creature.


    public AngelaD49() {
        this(150.0f, 0.0f);
    }
    public AngelaD49(final float x, final float y) {
        super(NAME, ID, 100, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mountain/Spriter/Mountain.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 2;
        maxAdditionalMoves = 99;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        setHp(BLOODBATH_PHASE_HP);
        runAnim("Idle3");
        addMove(NONE, Intent.NONE);
        addMove(NUMBNESS, Intent.DEFEND_DEBUFF);
        addMove(PALE_HANDS, Intent.ATTACK_DEBUFF, paleHandsDamage);
        addMove(PROFOUND_SORROW, Intent.ATTACK_DEBUFF, profoundSorrowDamage, profoundSorrowHits);
        addMove(SINKING, Intent.ATTACK_DEBUFF, sinkingDamage);
        addMove(PHASE_SHIFT_ASPIRATION, Intent.UNKNOWN);
        // Aspiration
        addMove(PULSATION, Intent.ATTACK, pulsationDamage);
        addMove(ASPIRATION, Intent.ATTACK, aspirationDamage, aspirationHits);
        addMove(SPAWN_LUNGS, Intent.UNKNOWN);
        addMove(PHASE_SHIFT_PINOCCHIO, Intent.UNKNOWN);
        // PINNOCHIO
        addMove(SHYNESS, Intent.DEBUFF);
        addMove(INVERTED_SHYNESS, Intent.ATTACK);
        addMove(BLOODY_WINGS, Intent.ATTACK_DEBUFF);
        addMove(INVERTED_BLOODY_WINGS, Intent.DEFEND_DEBUFF);
        addMove(TOKEN_OF_FRIENDSHIP, Intent.ATTACK_DEFEND, tokenOfFriendshipDamage);
        addMove(INVERTED_TOKEN_OF_FRIENDSHIP, Intent.ATTACK_DEFEND, tokenOfFriendshipDamage);
        addMove(DISPLAY_OF_AFFECTION, Intent.ATTACK, displayOfAffectionDamage, displayOfAffectionHits);
        addMove(INVERTED_DISPLAY_OF_AFFECTION, Intent.ATTACK, displayOfAffectionDamage, displayOfAffectionHits);
        addMove(INVERTED_TOKEN_OF_FRIENDSHIP, Intent.ATTACK_DEFEND, tokenOfFriendshipDamage);
        addMove(COFFIN, Intent.ATTACK, tokenOfFriendshipDamage);
        addMove(INVERTED_COFFIN, Intent.ATTACK, tokenOfFriendshipDamage);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Binah3");
        AbstractDungeon.getCurrRoom().cannotLose = true;
    }


    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        /*
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + currentStage);
                this.isDone = true;
            }
        });
         */
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        final int[] threshold = {0};
        switch (move.nextMove) {
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) { firstMove = false; }
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        switch (this.nextMove) {
            case PHASE_SHIFT_ASPIRATION:
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        currentHealth = 1;
                        halfDead = false;
                        turnCounter = 0;
                        maxHealth = ASPIRATION_PHASE_HP;
                        att(new HealAction(AngelaD49.this, AngelaD49.this, maxHealth));
                        isDone = true;
                    }
                });
                SummonLungs();
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                switch (currentPhase){
                    case WRISTCUTTER:
                        turnCounter += 1;
                        if(turnCounter > 4){ turnCounter = 1; }
                        break;
                    case ASPIRATION:
                        if(turnCounter == 0){  turnCounter = 1; }
                        else {
                            turnCounter += 1;
                            if (turnCounter > 5) {
                                turnCounter = 1;
                                firstMove = true;
                            }
                        }
                }
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (currentPhase){
            case WRISTCUTTER:
                if(turnCounter == 1){ setMoveShortcut(NUMBNESS, MOVES[NUMBNESS]); }
                else if(turnCounter == 2){ setMoveShortcut(PALE_HANDS, MOVES[PALE_HANDS]); }
                else if(turnCounter == 3){ setMoveShortcut(PROFOUND_SORROW, MOVES[PROFOUND_SORROW]); }
                else if(turnCounter == 4){ setMoveShortcut(SINKING, MOVES[SINKING]);}
                break;
            case ASPIRATION:
                if(firstMove){ setMoveShortcut(SPAWN_LUNGS); }
                else if (turnCounter == 5){ setMoveShortcut(PULSATION, MOVES[PULSATION]); }
                else { }
                break;
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (this.halfDead) { return; }
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        switch (currentPhase){
            case WRISTCUTTER:
                if(turnCounter == 1 && whichMove == 0){ setAdditionalMoveShortcut(PROFOUND_SORROW, moveHistory); }
                else if(turnCounter == 2){ setAdditionalMoveShortcut(PALE_HANDS, moveHistory); }
                else if(turnCounter == 3 && whichMove == 0){ setAdditionalMoveShortcut(NUMBNESS, moveHistory); }
                else {  }
                break;
            case ASPIRATION:
                if(turnCounter == 5){ setAdditionalMoveShortcut(ASPIRATION, moveHistory); }
                else {  }
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) { powersToRemove.add(power); }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
            additionalIntents.clear();
            additionalMoves.clear();
            phaseTransition();
        }
    }

    @Override
    public void die() { if (!AbstractDungeon.getCurrRoom().cannotLose) { super.die(); } }

    public void phaseTransition(){
        switch (currentPhase){
            case WRISTCUTTER:
                currentPhase = PHASE.ASPIRATION;
                setMoveShortcut(PHASE_SHIFT_ASPIRATION);
                this.createIntent();
                break;
        }
    }

    public void onMinionDeath() {
        atb(new LoseHPAction(this, this, ASPIRATION_PHASE_MINION_HPLOSS));
    }

    public void SummonLungs() {
        float xPos_Middle_L = -450F;
        float xPos_Short_L = -150F;
        AbstractMonster lung1 = new LungsOfCravingD49(xPos_Middle_L, 0.0f, this);
        atb(new SpawnMonsterAction(lung1, true));
        atb(new UsePreBattleActionAction(lung1));
        lung1.rollMove();
        lung1.createIntent();
        AbstractMonster lung2 = new LungsOfCravingD49(xPos_Short_L, 0.0f, this);
        atb(new SpawnMonsterAction(lung2, true));
        atb(new UsePreBattleActionAction(lung2));
        lung2.rollMove();
        lung2.createIntent();
    }

    public void summonIce(){

    }
}