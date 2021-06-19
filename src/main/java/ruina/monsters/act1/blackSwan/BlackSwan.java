package ruina.monsters.act1.blackSwan;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act1.fairyFestival.FairyMass;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlackSwan extends AbstractRuinaMonster
{
    public static final String ID = makeID(BlackSwan.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte WRITHE = 0;
    private static final byte PARASOL = 1;
    private static final byte REALITY = 2;
    private static final byte SHRIEK = 3;

    private final int BLOCK = calcAscensionTankiness(9);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int EROSION = calcAscensionSpecial(5);
    private static final int DEAD_BROTHERS_THRESHOLD = 4;
    public int numDeadBrothers = 0;

    public static final String POWER_ID = makeID("Dream");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BlackSwan() {
        this(0.0f, 0.0f);
    }

    public BlackSwan(final float x, final float y) {
        super(NAME, ID, 160, 0.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSwan/Spriter/BlackSwan.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(WRITHE, Intent.ATTACK, calcAscensionDamage(11));
        addMove(PARASOL, Intent.DEFEND);
        addMove(REALITY, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(SHRIEK, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela1");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {

            @Override
            public void atEndOfRound() {
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (mo instanceof Brother && mo.halfDead) {
                        ((Brother) mo).revive();
                    }
                }
                if (numDeadBrothers >= DEAD_BROTHERS_THRESHOLD) {
                    rollMove();
                    createIntent();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + DEAD_BROTHERS_THRESHOLD + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case WRITHE: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case PARASOL: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                resetIdle();
                break;
            }
            case REALITY: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case SHRIEK: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Erosion(adp(), EROSION));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (numDeadBrothers >= DEAD_BROTHERS_THRESHOLD) {
            setMoveShortcut(SHRIEK, MOVES[SHRIEK]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(WRITHE)) {
                possibilities.add(WRITHE);
            }
            if (!this.lastMove(PARASOL)) {
                possibilities.add(PARASOL);
            }
            if (!this.lastMove(REALITY)) {
                possibilities.add(REALITY);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    public void onBrotherDeath() {
        numDeadBrothers++;
        AbstractPower power = getPower(POWER_ID);
        if (power != null) {
            power.amount++;
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Brother && !mo.isDead) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "TeddyAtk", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "TeddyBlock", this);
    }

}