package ruina.monsters.act2.knight;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class KnightOfDespair extends AbstractRuinaMonster
{
    public static final String ID = makeID(KnightOfDespair.class.getSimpleName());

    public static final float MINION_X = -360.0F;

    private static final byte DESPAIR = 0;

    private static final int DESPAIR_LOSS = 40;
    private static final int MAX_STABS = 3;
    private final int STRENGTH = calcAscensionSpecial(5);
    private int stabCount = 0;

    private Sword sword;

    public static final String POWER_ID = makeID("Despair");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public KnightOfDespair() {
        this(0.0f, 0.0f);
    }

    public KnightOfDespair(final float x, final float y) {
        super(ID, ID, 160, -5.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Knight/Spriter/Knight.scml"));
        setHp(calcAscensionTankiness(160));
        addMove(DESPAIR, Intent.UNKNOWN);
    }

    @Override
    public void usePreBattleAction() {
        playSound("KnightChange");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Sword) {
                sword = (Sword) mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DESPAIR_LOSS) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case DESPAIR: {
                if (sword == null || sword.isDeadOrEscaped()) {
                    Summon();
                } else {
                    specialAnimation();
                    applyToTarget(sword, this, new StrengthPower(sword, STRENGTH));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(DESPAIR);
    }

    public void Summon() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                sword = new Sword(MINION_X, 0.0f, false);
                att(new UsePreBattleActionAction(sword));
                att(new SpawnMonsterAction(sword, true));
                this.isDone = true;
            }
        });
    }

    public void onSwordDeath() {
        atb(new LoseHPAction(this, this, DESPAIR_LOSS));
        stabCount++;
        if (stabCount > MAX_STABS) {
            stabCount = MAX_STABS;
        }
        animationAction("Idle" + stabCount, "KnightAttack", this);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Sword) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void specialAnimation() {
        animationAction("Idle" + stabCount, "KnightGaho", this);
    }

}