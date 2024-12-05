package ruina.monsters.act2.knight;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Sword extends AbstractRuinaMonster
{
    public static final String ID = makeID(Sword.class.getSimpleName());

    private static final byte TEAR_HEART = 0;
    private final int BLOCK = calcAscensionSpecial(8);
    private KnightOfDespair knight;
    boolean gainInitialBlock;

    public static final String POWER_ID = makeID("Worthless");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Sword(final float x, final float y, boolean gainInitialBlock) {
        super(ID, ID, 40, -5.0F, 0, 150.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Sword/Spriter/Sword.scml"));
        this.gainInitialBlock = gainInitialBlock;
        setHp(calcAscensionTankiness(40));
        addMove(TEAR_HEART, Intent.ATTACK, calcAscensionDamage(18));
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof KnightOfDespair) {
                knight = (KnightOfDespair) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.owner == owner && damageAmount == 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    att(new SuicideAction((AbstractMonster) owner));
                }
            }
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        applyToTarget(this, this, new MetallicizePower(this, BLOCK));
        if (gainInitialBlock) {
            block(this, BLOCK);
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TEAR_HEART: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        knight.onSwordDeath();
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(TEAR_HEART);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Vertical", "KnightVertGaho", enemy, this);
    }

}