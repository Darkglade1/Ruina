package ruina.monsters.eventboss.yan.monster;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_BalefulBrand;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_Compress;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_DistortedBlade;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_FlurryOfFists;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_GiantFist;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_Lock;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanAttack;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.Paralysis;
import ruina.powers.Protection;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.monsters.eventboss.yan.monster.yanHand.BEHAVIOUR.LEFT;
import static ruina.monsters.eventboss.yan.monster.yanHand.BEHAVIOUR.RIGHT;
import static ruina.util.Wiz.*;

public class yanDistortion extends AbstractDeckMonster
{
    public static final String ID = makeID(yanDistortion.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PROTECTL = 0;
    private static final byte PROTECTR = 1;
    private static final byte ATTACKL = 2;
    private static final byte ATTACKR = 3;

    private static final byte GIANT_FIST = 4;
    private static final byte COMPRESS = 5;
    private static final byte FLURRY = 6;
    private static final byte BRAND = 7;
    private static final byte LOCK = 8;
    private static final byte DISTORTEDBLADE = 9;

    public boolean leftKilledFirst = false;
    public boolean rightKilledFirst = false;

    public final int fistDMG = calcAscensionDamage(4);
    public final int fistPara = calcAscensionSpecial(2);

    public final int compressBlock = calcAscensionTankiness(15);

    public final int flurryDamage = calcAscensionDamage(4);
    public final int flurryStr = calcAscensionSpecial(2);
    public final int flurryHits = 3;

    public final int lockDmg = calcAscensionDamage(10);
    public final int drawReduction = 1;

    public final int brandDmg = calcAscensionDamage(10);
    public final int brandErosion = calcAscensionSpecial(1);

    public final int bladeDMG = calcAscensionDamage(20);
    public final int bladeErosion = calcAscensionSpecial(2);

    public final int attackStr = calcAscensionSpecial(2);
    public final int defendEnd = calcAscensionSpecial(2);

    public static final String POWER_POWER_ID = makeID("OminousPower");
    public static final PowerStrings PowerPowerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_POWER_ID);
    public static final String POWER_POWER_NAME = PowerPowerStrings.NAME;
    public static final String[] POWER_POWER_DESCRIPTIONS = PowerPowerStrings.DESCRIPTIONS;

    private enum PHASE{
        SPLIT,
        MERGED
    }
    private PHASE currentphase = PHASE.SPLIT;
    private int phase = 1;
    private int turn = 5;

    public yanDistortion() {
        this(150.0f, 0.0f);
    }
    public yanDistortion(final float x, final float y) {
        super(NAME, ID, 250, -5.0F, 0, 250.0f, 355.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Yan/Spriter/Yan.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) { additionalMovesHistory.add(new ArrayList<>()); }

        addMove(PROTECTL, Intent.BUFF);
        addMove(PROTECTR, Intent.BUFF);
        addMove(ATTACKL, Intent.BUFF);
        addMove(ATTACKR, Intent.BUFF);

        addMove(GIANT_FIST, Intent.ATTACK_DEBUFF, fistDMG);
        addMove(COMPRESS, Intent.DEFEND);
        addMove(FLURRY, Intent.ATTACK_BUFF, flurryDamage, flurryHits, true);
        addMove(BRAND, Intent.ATTACK_DEBUFF, brandDmg);
        addMove(LOCK, Intent.ATTACK_DEBUFF, lockDmg);
        addMove(DISTORTEDBLADE, Intent.ATTACK_DEBUFF, bladeDMG);

        initializeDeck();
    }

    @Override
    public void usePreBattleAction()
    {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        AbstractPower ominouspower = new AbstractLambdaPower(POWER_POWER_NAME, POWER_POWER_ID, AbstractPower.PowerType.BUFF, false, this, (maxHealth / 2)) {
            @Override
            public void updateDescription() { description = String.format(POWER_POWER_DESCRIPTIONS[0], amount); }
        };
        applyToTarget(this, this, ominouspower);
        CustomDungeon.playTempMusicInstantly("ChildrenOfTheCity");
        Summon();
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        final int[] threshold = {0};
        switch (move.nextMove) {
            case PROTECTL:
                boolean buffedCorrectly = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof yanHand){
                        if(((yanHand) m).currentMode == yanHand.BEHAVIOUR.LEFT && !m.halfDead){
                            applyToTarget(m, m, new Protection(m, defendEnd));
                            buffedCorrectly = true;
                            break;
                        }
                    }
                }
                if(buffedCorrectly){ break; }
                else {
                    for(AbstractMonster m: monsterList()){
                        if(m instanceof yanHand){
                            if(((yanHand) m).currentMode == RIGHT && !m.halfDead){
                                applyToTarget(m, m, new Protection(m, defendEnd));
                                break;
                            }
                        }
                    }
                    break;
                }
            case PROTECTR:
                buffedCorrectly = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof yanHand){
                        if(((yanHand) m).currentMode == RIGHT && !m.halfDead){
                            applyToTarget(m, m, new Protection(m, defendEnd));
                            buffedCorrectly = true;
                            break;
                        }
                    }
                }
                if(buffedCorrectly){ break; }
                else {
                    for(AbstractMonster m: monsterList()){
                        if(m instanceof yanHand){
                            if(((yanHand) m).currentMode == yanHand.BEHAVIOUR.LEFT && !m.halfDead){
                                applyToTarget(m, m, new Protection(m, defendEnd));
                                break;
                            }
                        }
                    }
                    break;
                }
            case ATTACKL:
                buffedCorrectly = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof yanHand){
                        if(((yanHand) m).currentMode == LEFT && !m.halfDead){
                            System.out.println("applying str to left hand [attackL]");
                            applyToTarget(m, m, new StrengthPower(m, attackStr));
                            buffedCorrectly = true;
                            break;
                        }
                    }
                }
                System.out.println(buffedCorrectly);
                if(buffedCorrectly){ break; }
                else {
                    for(AbstractMonster m: monsterList()){
                        if(m instanceof yanHand){
                            if(((yanHand) m).currentMode == RIGHT && !m.halfDead){
                                System.out.println("applying str to right hand [attackL]");
                                applyToTarget(m, m, new StrengthPower(m, attackStr));
                                break;
                            }
                        }
                    }
                    break;
                }
            case ATTACKR:
                buffedCorrectly = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof yanHand){
                        if(((yanHand) m).currentMode == RIGHT && !m.halfDead){
                            System.out.println("applying str to right hand [attackR]");
                            applyToTarget(m, m, new StrengthPower(m, attackStr));
                            buffedCorrectly = true;
                            break;
                        }
                    }
                }
                System.out.println(buffedCorrectly);
                if(buffedCorrectly){ break; }
                else {
                    for(AbstractMonster m: monsterList()){
                        if(m instanceof yanHand){
                            if(((yanHand) m).currentMode == LEFT && !m.halfDead){
                                System.out.println("applying str to left hand [attackR]");
                                applyToTarget(m, m, new StrengthPower(m, attackStr));
                                break;
                            }
                        }
                    }
                    break;
                }
            case GIANT_FIST:
                specialAnimation("YanStab");
                dmg(adp(), info);
                applyToTarget(adp(), yanDistortion.this, new Paralysis(adp(), fistPara));
                resetIdle();
                break;
            case COMPRESS: {
                specialAnimation(null);
                block(this, compressBlock);
                resetIdle();
                break;
            }
            case FLURRY:
                for (int i = 0; i < multiplier; i++) {
                    specialAnimation("YanVert");
                    dmg(adp(), info);
                    resetIdle();
                }
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, flurryStr)));
                break;
            case BRAND:
                specialAnimation("YanBrand");
                dmg(adp(), info);
                applyToTarget(adp(), yanDistortion.this, new NextTurnPowerPower(adp(), new Erosion(adp(), brandErosion)));
                resetIdle();
                break;
            case LOCK:
                specialAnimation("YanLock");
                dmg(adp(), info);
                applyToTarget(adp(), yanDistortion.this, new DrawReductionPower(adp(), drawReduction));
                resetIdle();
                break;
            case DISTORTEDBLADE:
                specialAnimation("DistortedBladeStart");
                dmg(adp(), info);
                applyToTarget(adp(), yanDistortion.this, new Erosion(adp(), bladeErosion));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turn = 5;
                        isDone = true;
                    }
                });
                resetIdle();
                break;
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
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
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                turn -= 1;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractMonster m: monsterList()){
                    if(m instanceof yanHand){
                        if(!m.halfDead) {
                            m.rollMove();
                            m.createIntent();
                        }
                    }
                }
                isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(fulfillsMergeConditions()){
                    merge();
                    turn = 0;
                }
                this.isDone = true;
            }

        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(turn <= 0){ setMoveShortcut(DISTORTEDBLADE, MOVES[9], new CHRBOSS_DistortedBlade(this)); }
        else { createMoveFromCard(topDeckCardForMoveAction()); }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (turn != 0) { createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }
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
    protected void createDeck() {
        if(currentphase == PHASE.SPLIT){
            for(int i = 0; i < 3; i += 1){
                masterDeck.addToBottom(new CHRBOSS_yanAttack(this));
                masterDeck.addToBottom(new CHRBOSS_yanProtect(this));
            }
        }
        else {
            masterDeck.addToBottom(new CHRBOSS_FlurryOfFists(this));
            for(int i = 0; i < 2; i += 1){
                masterDeck.addToBottom(new CHRBOSS_GiantFist(this));
                masterDeck.addToBottom(new CHRBOSS_Compress(this));
            }
            if(leftKilledFirst){
                for(int i = 0; i < 2 ; i += 1){ masterDeck.addToBottom(new CHRBOSS_BalefulBrand(this)); }
                masterDeck.addToBottom(new CHRBOSS_Lock(this));
            }
            else {
                for(int i = 0; i < 2 ; i += 1){ masterDeck.addToBottom(new CHRBOSS_Lock(this)); }
                masterDeck.addToBottom(new CHRBOSS_BalefulBrand(this));
            }
        }
    }

    @Override
    protected void createMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {

    }

    protected void createMoveFromCard(AbstractCard c) {
        if (c.cardID.equals(CHRBOSS_yanProtect.ID)) { setMoveShortcut(PROTECTL, MOVES[0], c);
        } else if (c.cardID.equals(CHRBOSS_yanAttack.ID)) { setMoveShortcut(ATTACKL, MOVES[3], c);
        } else if (c.cardID.equals(CHRBOSS_GiantFist.ID)) { setMoveShortcut(GIANT_FIST, MOVES[4], c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) { setMoveShortcut(COMPRESS, MOVES[5], c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) { setMoveShortcut(FLURRY, MOVES[6], c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) { setMoveShortcut(BRAND, MOVES[7], c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) { setMoveShortcut(LOCK, MOVES[8], c);
        } else if (c.cardID.equals(CHRBOSS_DistortedBlade.ID)) { setMoveShortcut(DISTORTEDBLADE, MOVES[9], c);
        } else { setMoveShortcut(COMPRESS, MOVES[1], c); }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(CHRBOSS_yanProtect.ID)) { setAdditionalMoveShortcut(PROTECTR, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_yanAttack.ID)) { setAdditionalMoveShortcut(ATTACKR, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_GiantFist.ID)) { setAdditionalMoveShortcut(GIANT_FIST, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) { setAdditionalMoveShortcut(COMPRESS, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) { setAdditionalMoveShortcut(FLURRY, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) { setAdditionalMoveShortcut(BRAND, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) { setAdditionalMoveShortcut(LOCK, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_DistortedBlade.ID)) { setAdditionalMoveShortcut(DISTORTEDBLADE, moveHistory, c);
        } else { setAdditionalMoveShortcut(COMPRESS, moveHistory, c); }
    }


    public void merge(){
        currentphase = PHASE.MERGED;
        phase = 2;

        att(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                AbstractDungeon.getCurrRoom().cannotLose = false;
                isDone = true;
            }
        });
        att(new RemoveDebuffsAction(this));
        att(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractPower p: yanDistortion.this.powers){
                    if(p.ID.equals(POWER_POWER_ID)){ att(new RemoveSpecificPowerAction(yanDistortion.this, yanDistortion.this, p)); }
                }
                isDone = true;
            }
        });
        att(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractMonster m : monsterList()) {
                    if (!m.equals(yanDistortion.this)) {
                        m.loseBlock();
                        att(new SuicideAction(m));
                    }
                }
                this.isDone = true;
            }
        });
        att(new AbstractGameAction() {
            @Override
            public void update() {
                initializeDeck();
                isDone = true;
            }
        });
    }

    public void Summon() {
        float xPos_Middle_L = -450F;
        float xPos_Short_L = -150F;
        AbstractMonster hand1 = new yanHand(xPos_Middle_L, 0.0f, RIGHT,this);
        atb(new SpawnMonsterAction(hand1, true));
        atb(new UsePreBattleActionAction(hand1));
        hand1.rollMove();
        hand1.createIntent();
        AbstractMonster hand2 = new yanHand(xPos_Short_L, 0.0f, LEFT, this);
        atb(new SpawnMonsterAction(hand2, true));
        atb(new UsePreBattleActionAction(hand2));
        hand2.rollMove();
        hand2.createIntent();
    }

    public boolean fulfillsMergeConditions(){
        int deadC = 0;
        for(AbstractMonster m :monsterList()){
            if(m.halfDead){ deadC += 1; }
        }
        return (deadC == 2 || (currentHealth <= (maxHealth / 2))) && currentphase == PHASE.SPLIT;
    }

    private void specialAnimation(String sound) {
        animationAction("Special", sound, this);
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

}