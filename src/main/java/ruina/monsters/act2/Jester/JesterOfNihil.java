package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class JesterOfNihil extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(JesterOfNihil.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CRUEL_CLAWS = 0;
    private static final byte FEROCIOUS_FANGS = 1;
    private static final byte BLOODSTAINED_HUNT = 2;
    private static final byte HOWL = 3;

    private final int BLOCK = calcAscensionTankiness(20);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int HEAL = calcAscensionSpecial(100);
    private final int BLEED = calcAscensionSpecial(2);
    private static final float GIRL_1_X_POSITION = -500.0f;
    private static final float GIRL_2_X_POSITION = -400.0f;
    private static final float STATUE_1_X_POSITION = -200.0f;
    private static final float STATUE_2_X_POSITION = -300.0f;
    private boolean girl1Spawned = false;
    private boolean girl2Spawned = false;
    private AbstractMagicalGirl girl1;
    private AbstractMagicalGirl girl2;
    private InvisibleBarricadePower power = new InvisibleBarricadePower(this);

    public static final String POWER_ID = makeID("BloodstainedClaws");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JesterOfNihil() {
        this(0.0f, 0.0f);
    }

    public JesterOfNihil(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jester/Spriter/Jester.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(this.maxHealth));

        ArrayList<Integer> possibleGirls = new ArrayList<>();
        possibleGirls.add(0);
        possibleGirls.add(1);
        Collections.shuffle(possibleGirls, AbstractDungeon.monsterRng.random);
        int first = possibleGirls.remove(0);
        int second = possibleGirls.remove(0);
        //If Queen of Love is selected always put her in the first position
        if (second == 0) {
            int temp = second;
            second = first;
            first = temp;
        }
        girl1 = returnGirl(first, GIRL_1_X_POSITION);
        girl2 = returnGirl(second, GIRL_2_X_POSITION);

        addMove(CRUEL_CLAWS, Intent.ATTACK_DEFEND, calcAscensionDamage(9));
        addMove(FEROCIOUS_FANGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(8), 2, true);
        addMove(BLOODSTAINED_HUNT, Intent.ATTACK, calcAscensionDamage(7), 3, true);
        addMove(HOWL, Intent.BUFF);
    }

    public AbstractMagicalGirl returnGirl(int num, float xPosition) {
        AbstractMagicalGirl girl = null;
        if (num == 0) {
            girl = new QueenOfLove(xPosition, 0.0f);
        }
        if (num == 1) {
            girl = new ServantOfCourage(xPosition, 0.0f);
        }
        return girl;
    }

    @Override
    public void usePreBattleAction() {
        Statue statue1 = new Statue(STATUE_1_X_POSITION, 0.0f, this, girl1);
        Statue statue2 = new Statue(STATUE_2_X_POSITION, 0.0f, this, girl2);
        atb(new SpawnMonsterAction(statue1, true));
        atb(new UsePreBattleActionAction(statue1));
        atb(new SpawnMonsterAction(statue2, true));
        atb(new UsePreBattleActionAction(statue2));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, HEAL) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, power);
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case CRUEL_CLAWS: {
                block(this, BLOCK);
                clawAnimation(target);
                dmg(target, info);
                resetIdle();
                power.justGainedBlock = true; //hack to make the block fall off at a different time LOL
                break;
            }
            case FEROCIOUS_FANGS: {
                for (int i = 0; i < multiplier; i++) {
                    biteAnimation(target);
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(target, this, new Bleed(target, BLEED));
                break;
            }
            case BLOODSTAINED_HUNT: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        clawAnimation(target);
                    } else {
                        biteAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case HOWL: {
                howlAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void clawAnimation(AbstractCreature enemy) {
        animationAction("Claw", "Claw", enemy, this);
    }

    private void biteAnimation(AbstractCreature enemy) {
        animationAction("Bite", "Bite", enemy, this);
    }

    private void howlAnimation() {
        animationAction("Howl", "Howl", this);
    }

    @Override
    public void takeTurn() {
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (EnemyMoveInfo additionalMove : additionalMoves) {
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new IntentFlashAction(this));
            if (red.isDead || red.isDying) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, red);
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CRUEL_CLAWS)) {
            setMoveShortcut(BLOODSTAINED_HUNT, MOVES[BLOODSTAINED_HUNT]);
        } else if (this.lastMove(FEROCIOUS_FANGS)) {
            setMoveShortcut(CRUEL_CLAWS, MOVES[CRUEL_CLAWS]);
        } else {
            setMoveShortcut(FEROCIOUS_FANGS, MOVES[FEROCIOUS_FANGS]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (lastMove(BLOODSTAINED_HUNT, moveHistory)) {
            setAdditionalMoveShortcut(HOWL, moveHistory);
        } else if (lastMove(HOWL, moveHistory)) {
            setAdditionalMoveShortcut(FEROCIOUS_FANGS, moveHistory);
        } else {
            setAdditionalMoveShortcut(BLOODSTAINED_HUNT, moveHistory);
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, red, makeUIPath("RedIcon.png"));
            }
        }
    }

    public void SummonGirl(AbstractMagicalGirl girl) {
        if (girl == girl1) {
            girl1Spawned = true;
        }
        if (girl == girl2) {
            girl2Spawned = true;
        }
        atb(new SpawnMonsterAction(girl, false));
        atb(new UsePreBattleActionAction(girl));
    }

}