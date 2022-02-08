package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.BurrowingHeavenEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BurrowingHeaven extends AbstractRuinaMonster
{
    public static final String ID = makeID(BurrowingHeaven.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte YOUR_OWN_HEAVEN = 0;
    private static final byte BLOODY_WINGS = 1;
    private static final byte GAZE_OF_OTHERS = 2;

    private final int FRAIL = calcAscensionSpecial(1);
    private final int VULNERABLE = calcAscensionSpecial(2);
    private final int STR_DOWN = calcAscensionSpecial(2);
    private final float DAMAGE_REDUCTION = 0.5f;

    public static final String POWER_ID = makeID("Unnerving");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BurrowingHeaven() {
        this(0.0f, 0.0f);
    }

    public BurrowingHeaven(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 280.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BurrowingHeaven/Spriter/BurrowingHeaven.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(140));
        addMove(YOUR_OWN_HEAVEN, Intent.ATTACK, calcAscensionDamage(21));
        addMove(BLOODY_WINGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(GAZE_OF_OTHERS, Intent.STRONG_DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, (int)(DAMAGE_REDUCTION * 100)) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL) {
                    return damage * (1.0f - DAMAGE_REDUCTION);
                } else {
                    return damage;
                }
            }

            @Override
            public void onGainedBlock(float blockAmount) {
                atb(new LoseHPAction(owner, owner, (int)blockAmount));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
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
            case YOUR_OWN_HEAVEN: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case BLOODY_WINGS: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                resetIdle();
                break;
            }
            case GAZE_OF_OTHERS: {
                specialAnimation(adp());
                final AbstractGameEffect[] vfx = {null};
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(vfx[0] == null){
                            vfx[0] = new BurrowingHeavenEffect();
                            AbstractDungeon.effectsQueue.add(vfx[0]);
                        }
                        else {
                            isDone = vfx[0].isDone;
                        }
                    }
                });
                applyToTarget(adp(), this, new StrengthPower(adp(), -STR_DOWN));
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(YOUR_OWN_HEAVEN)) {
            possibilities.add(YOUR_OWN_HEAVEN);
        }
        if (!this.lastMove(BLOODY_WINGS)) {
            possibilities.add(BLOODY_WINGS);
        }
        if (!this.lastMove(GAZE_OF_OTHERS) && !this.lastMoveBefore(GAZE_OF_OTHERS)) {
            possibilities.add(GAZE_OF_OTHERS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WoodFinish", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Debuff", null, enemy, this);
    }

}