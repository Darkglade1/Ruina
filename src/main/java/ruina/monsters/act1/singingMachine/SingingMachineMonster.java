package ruina.monsters.act1.singingMachine;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.cards.GrindingGears;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.BloodSplatter;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SingingMachineMonster extends AbstractRuinaMonster
{
    public static final String ID = makeID(SingingMachineMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BLOODY_TUNE = 0;
    private static final byte PERFORMANCE = 1;
    private static final byte CRUSHING_BEATS = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int SPECIAL_STATUS = 1;
    private final int WOUNDS = calcAscensionSpecial(1);
    private final int VULNERABLE = 1;

    private final int POWER_STRENGTH = calcAscensionSpecial(2);

    public ManicEmployee employee;
    public ArrayList<AbstractCard> machineCards = new ArrayList<>();
    public AbstractCard specialStatus = new GrindingGears(this);

    public static final String POWER_ID = makeID("Machine");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SingingMachineMonster() {
        this(100.0f, 0.0f);
    }

    public SingingMachineMonster(final float x, final float y) {
        super(NAME, ID, 150, 0.0F, 0, 250.0f, 210.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SingingMachine/Spriter/SingingMachine.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(BLOODY_TUNE, Intent.BUFF);
        addMove(PERFORMANCE, Intent.STRONG_DEBUFF);
        addMove(CRUSHING_BEATS, Intent.ATTACK, calcAscensionDamage(11));

        if (AbstractDungeon.ascensionLevel >= 19) {
            specialStatus.upgrade();
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela2");
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof ManicEmployee) {
                employee = (ManicEmployee) mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, POWER_STRENGTH) {

            @Override
            public void atEndOfRound() {
                for (AbstractMonster mo : monsterList()) {
                    if (machineCards.size() > 0) {
                        flash();
                        applyToTarget(mo, mo, new StrengthPower(mo, amount * machineCards.size()));
                        applyToTarget(mo, mo, new LoseStrengthPower(mo, amount * machineCards.size()));
                    }
                }
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

        if (firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case BLOODY_TUNE: {
                openAnimation();
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                intoDrawMo(specialStatus.makeStatEquivalentCopy(), SPECIAL_STATUS, this);
                intoDiscardMo(specialStatus.makeStatEquivalentCopy(), SPECIAL_STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case PERFORMANCE: {
                specialAnimation();
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                if (AbstractDungeon.ascensionLevel < 19) {
                    for (AbstractMonster mo : monsterList()) {
                        applyToTarget(mo, this, new VulnerablePower(mo, VULNERABLE, true));
                    }
                }
                intoDiscardMo(new Wound(), WOUNDS, this);
                resetIdle(1.0f);
                if (employee != null) {
                    employee.forcedAttack = true;
                    atb(new RollMoveAction(employee));
                }
                break;
            }
            case CRUSHING_BEATS: {
                specialAnimation();
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!firstMove && employee.isDeadOrEscaped()) {
            if (lastMove(CRUSHING_BEATS)) {
                setMoveShortcut(PERFORMANCE, MOVES[PERFORMANCE]);
            } else {
                setMoveShortcut(CRUSHING_BEATS, MOVES[CRUSHING_BEATS]);
            }
        } else {
            if (lastMove(BLOODY_TUNE)) {
                setMoveShortcut(PERFORMANCE, MOVES[PERFORMANCE]);
            } else if (lastMove(PERFORMANCE)) {
                setMoveShortcut(CRUSHING_BEATS, MOVES[CRUSHING_BEATS]);
            } else {
                setMoveShortcut(BLOODY_TUNE, MOVES[BLOODY_TUNE]);
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        float drawScale = 0.20F;
        float offsetX1 = 100.0F * Settings.scale;
        float offsetY = 100.0F * Settings.scale;
        int xCol = 0;
        int yRow = 0;
        for (int i = 0; i < machineCards.size(); i++) {
            AbstractCard card = machineCards.get(i);
            card.drawScale = drawScale;
            card.current_x = this.hb.x + offsetX1 * xCol;
            card.current_y = this.hb.y + offsetY * (yRow + 3);
            yRow++;
            if (i % 3 == 0 && i != 0) {
                xCol++;
                yRow = 0;
            }
            card.render(sb);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    private void openAnimation() {
        animationAction("Open", "SingingRhythm", this);
    }

    private void specialAnimation() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for (int j = 0; j < 6; j++)  {
                    AbstractDungeon.effectsQueue.add(new BloodSplatter(1.0F)); }
                isDone = true;
            }
        });
        animationAction("Special", "SingingEat", this);
    }

}