package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScaredyCat extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScaredyCat.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte RAWR = 0;
    private static final byte GROWL = 1;
    private static final byte COURAGE = 2;
    private static final byte FLEE = 3;

    private RoadHome road;

    private final int WOUND = 1;
    private final int STRENGTH = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("Courage");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ScaredyCat() {
        this(0.0f, 0.0f);
    }

    public ScaredyCat(final float x, final float y) {
        super(NAME, ID, 100, -5.0F, 0, 270.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScaredyCat/Spriter/ScaredyCat.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(RAWR, Intent.ATTACK, calcAscensionDamage(7), 2, true);
        addMove(GROWL, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(COURAGE, Intent.BUFF);
        addMove(FLEE, Intent.ESCAPE);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof RoadHome) {
                road = (RoadHome)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
            @Override
            public void onSpecificTrigger() {
                flash();
                applyToTarget(owner, owner, new StrengthPower(owner, STRENGTH));
                applyToTarget(owner, owner, new LoseStrengthPower(owner, STRENGTH));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(road.name, "y") + POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
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

        if (this.firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case RAWR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(adp());
                    } else {
                        attack2Animation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case GROWL: {
                poisonAnimation(adp());
                dmg(adp(), info);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    addToBot(new MakeTempCardInDiscardAndDeckAction(new Wound()));
                } else {
                    intoDiscardMo(new Wound(), WOUND, this);
                }
                resetIdle();
                break;
            }
            case FLEE: {
                animation.setFlip(true, false);
                atb(new EscapeAction(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (road != null && road.isDeadOrEscaped()) {
            setMoveShortcut(FLEE);
        } else {
            if (this.lastMove(RAWR)) {
                setMoveShortcut(GROWL, MOVES[GROWL]);
            } else {
                setMoveShortcut(RAWR, MOVES[RAWR]);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!road.isDeadOrEscaped()) {
            road.catDeath();
        }
    }

    public void roadDeath() {
        runAnim("Scared");
        playSound("LionChange");
        this.currentHealth = this.maxHealth = 1;
        healthBarUpdatedEvent();
        applyToTarget(this, this, new StrengthPower(this, -9999));
        makePowerRemovable(this, POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, POWER_ID));
        setMoveShortcut(FLEE);
        createIntent();
        atb(new SetMoveAction(this, FLEE, Intent.ESCAPE));
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "Claw", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "WoodStrike", enemy, this);
    }

    private void poisonAnimation(AbstractCreature enemy) {
        animationAction("Poison", "LionPoison", enemy, this);
    }

}