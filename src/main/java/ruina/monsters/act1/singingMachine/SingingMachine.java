package ruina.monsters.act1.singingMachine;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
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
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.cards.GrindingGears;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.BloodSplatter;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SingingMachine extends AbstractRuinaMonster
{
    public static final String ID = makeID(SingingMachine.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BLOODY_TUNE = 0;
    private static final byte GRINDING_GEARS = 1;
    private static final byte PERFORMANCE = 2;
    private static final byte MANIC_PERFORMERS = 3;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int SPECIAL_STATUS = calcAscensionSpecial(3);
    private final int WOUNDS = calcAscensionSpecial(2);
    private final int VULNERABLE = 1;
    private final int BLOCK = calcAscensionTankiness(10);

    private final int POWER_STRENGTH = calcAscensionSpecial(1);
    private final int POWER_METALLICIZE = calcAscensionSpecial(2);

    private byte nextMoveByte = 0;

    public ManicEmployee[] minions = new ManicEmployee[2];
    public ArrayList<AbstractCard> machineCards = new ArrayList<>();

    public static final String POWER_ID = makeID("Machine");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SingingMachine() {
        this(100.0f, 0.0f);
    }

    public SingingMachine(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SingingMachine/Spriter/SingingMachine.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(BLOODY_TUNE, Intent.BUFF);
        addMove(GRINDING_GEARS, Intent.DEBUFF);
        addMove(PERFORMANCE, Intent.STRONG_DEBUFF);
        addMove(MANIC_PERFORMERS, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela2");
        Summon();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + POWER_STRENGTH + POWER_DESCRIPTIONS[1] + POWER_METALLICIZE + POWER_DESCRIPTIONS[2];
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
                int strMultiplier = 1;
                if (minions[0] == null || minions[1] == null) {
                    strMultiplier = 2;
                }
                boolean gaveStr = false;
                openAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (mo != this) {
                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH * strMultiplier));
                        gaveStr = true;
                    }
                }
                if (!gaveStr) {
                    block(this, BLOCK);
                }
                nextMoveByte = GRINDING_GEARS;
                resetIdle(1.0f);
                break;
            }
            case GRINDING_GEARS: {
                openAnimation();
                intoDiscardMo(new GrindingGears(this), SPECIAL_STATUS, this);
                resetIdle(1.0f);
                nextMoveByte = PERFORMANCE;
                break;
            }
            case PERFORMANCE: {
                specialAnimation();
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new VulnerablePower(mo, VULNERABLE, true));
                }
                intoDiscardMo(new Wound(), WOUNDS, this);
                resetIdle(1.0f);
                nextMoveByte = BLOODY_TUNE;
                for (ManicEmployee mo : minions) {
                    if (mo != null) {
                        mo.forcedAttack = true;
                        atb(new RollMoveAction(mo));
                    }
                }
                break;
            }
            case MANIC_PERFORMERS: {
                specialAnimation();
                Summon();
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (minions[0] == null && minions[1] == null && !firstMove) {
            setMoveShortcut(MANIC_PERFORMERS, MOVES[MANIC_PERFORMERS]);
        } else {
            if (nextMoveByte == BLOODY_TUNE) {
                setMoveShortcut(BLOODY_TUNE, MOVES[BLOODY_TUNE]);
            } else if (nextMoveByte == GRINDING_GEARS) {
                setMoveShortcut(GRINDING_GEARS, MOVES[GRINDING_GEARS]);
            } else if (nextMoveByte == PERFORMANCE) {
                setMoveShortcut(PERFORMANCE, MOVES[PERFORMANCE]);
            }
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -450.0F;
        float xPos_Middle_L = -200F;
        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                ManicEmployee minion;
                if (i == 0) {
                    minion = new ManicEmployee(xPos_Farthest_L, 0.0f, this, 0, (byte) 0);
                } else {
                    minion = new ManicEmployee(xPos_Middle_L, 0.0f, this, 1, (byte) 1);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                int attackCount = 0;
                int skillCount = 0;
                for (AbstractCard card : machineCards) {
                    if (card.type == AbstractCard.CardType.ATTACK) {
                        attackCount++;
                    }
                    if (card.type == AbstractCard.CardType.SKILL) {
                        skillCount++;
                    }
                }
                if (attackCount > 0) {
                    applyToTarget(minion, this, new StrengthPower(minion, attackCount * POWER_STRENGTH));
                }
                if (skillCount > 0) {
                    applyToTarget(minion, this, new MetallicizePower(minion, skillCount * POWER_METALLICIZE));
                }
                minions[i] = minion;
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
            if (i % 4 == 0 && i != 0) {
                xCol++;
                yRow = 0;
            }
            card.render(sb);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof ManicEmployee) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
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