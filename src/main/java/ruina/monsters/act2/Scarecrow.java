package ruina.monsters.act2;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.ExhaustTopCardAction;
import ruina.actions.WisdomAction;
import ruina.cards.Wisdom;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Scarecrow extends AbstractRuinaMonster
{
    public static final String ID = makeID(Scarecrow.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte RAKE = 0;
    private static final byte HARVEST = 1;
    private static final byte STRUGGLE = 2;

    private static final int STRUGGLE_THRESHOLD = 2;
    private int struggleCounter;

    private final int WISDOM_AMT = calcAscensionSpecial(2);

    public static final String SEARCH_POWER_ID = makeID("Search");
    public static final PowerStrings SEARCHPowerStrings = CardCrawlGame.languagePack.getPowerStrings(SEARCH_POWER_ID);
    public static final String SEARCH_POWER_NAME = SEARCHPowerStrings.NAME;
    public static final String[] SEARCH_POWER_DESCRIPTIONS = SEARCHPowerStrings.DESCRIPTIONS;

    public Scarecrow() {
        this(0.0f, 0.0f, STRUGGLE_THRESHOLD);
    }

    public Scarecrow(final float x, final float y, int struggleCounter) {
        super(NAME, ID, 40, -5.0F, 0, 230.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Scarecrow/Spriter/Scarecrow.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(48), calcAscensionTankiness(54));
        addMove(RAKE, Intent.ATTACK, calcAscensionDamage(12));
        addMove(HARVEST, Intent.ATTACK, calcAscensionDamage(3), 3, true);
        addMove(STRUGGLE, Intent.DEBUFF);
        this.struggleCounter = struggleCounter;
    }

    @Override
    public void usePreBattleAction() {
        atb(new WisdomAction());
        applyToTarget(this, this, new AbstractLambdaPower(SEARCH_POWER_NAME, SEARCH_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                    atb(new ExhaustTopCardAction(owner));
                }
            }
            @Override
            public void updateDescription() {
                description = SEARCH_POWER_DESCRIPTIONS[0];
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
            case RAKE: {
                rakeAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case HARVEST: {
                for (int i = 0; i < multiplier; i++) {
                    harvestAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case STRUGGLE: {
                intoDiscardMo(new Wisdom(), WISDOM_AMT, this);
                struggleCounter = STRUGGLE_THRESHOLD + 1;
                break;
            }
        }
        struggleCounter--;
        //In case it gets stunned by Wisdom
        AbstractMonster mo = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                atb(new RollMoveAction(mo));
                this.isDone = true;
            }
        });

    }

    @Override
    protected void getMove(final int num) {
        if (this.hasPower(StunMonsterPower.POWER_ID)) {
            return;
        }
        if (struggleCounter <= 0) {
            setMoveShortcut(STRUGGLE);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(RAKE)) {
                possibilities.add(RAKE);
            }
            if (!this.lastMove(HARVEST)) {
                possibilities.add(HARVEST);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        playSound("ScarecrowDeath", 0.5f);
    }

    private void rakeAnimation(AbstractCreature enemy) {
        animationAction("Attack1", "Rake", enemy, this);
    }

    private void harvestAnimation(AbstractCreature enemy) {
        animationAction("Attack2", "Harvest", 0.5f, enemy, this);
    }

}