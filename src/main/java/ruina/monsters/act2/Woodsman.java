package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleEnergyPower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Woodsman extends AbstractRuinaMonster
{
    public static final String ID = makeID(Woodsman.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte STRIKE = 0;
    private static final byte LUMBER = 1;
    private static final byte PULSE = 2;

    private static final int ENERGY_GAIN = 2;
    private final int STRENGTH = calcAscensionSpecial(1);
    private final int STATUS = calcAscensionSpecial(1);
    private final int DEBUFF = calcAscensionSpecial(2);

    public static final String HEART_POWER_ID = makeID("Heart");
    public static final PowerStrings HEARTPowerStrings = CardCrawlGame.languagePack.getPowerStrings(HEART_POWER_ID);
    public static final String HEART_POWER_NAME = HEARTPowerStrings.NAME;
    public static final String[] HEART_POWER_DESCRIPTIONS = HEARTPowerStrings.DESCRIPTIONS;

    public Woodsman() {
        this(0.0f, 0.0f);
    }

    public Woodsman(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 0, 230.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Woodsman/Spriter/Woodsman.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(140), calcAscensionTankiness(148));
        addMove(STRIKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(10), 2, true);
        addMove(LUMBER, Intent.ATTACK, calcAscensionDamage(4), 4, true);
        addMove(PULSE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(adp(), adp(), new InvisibleEnergyPower(adp(), ENERGY_GAIN));
        applyToTarget(this, this, new AbstractLambdaPower(HEART_POWER_NAME, HEART_POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
            @Override
            public void atEndOfRound() {
                int str = EnergyPanel.totalCount * amount;
                if (str > 0) {
                    applyToTarget(owner, owner, new StrengthPower(owner, str));
                }
            }

            public void onRemove() {
                atb(new RemoveSpecificPowerAction(adp(), adp(), InvisibleEnergyPower.POWER_ID));
            }

            @Override
            public void updateDescription() {
                description = HEART_POWER_DESCRIPTIONS[0];
                for (int i = 0; i < ENERGY_GAIN; i++) {
                    description += " [E]";
                }
                description += HEART_POWER_DESCRIPTIONS[1] + amount + HEART_POWER_DESCRIPTIONS[2];
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
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        switch (this.nextMove) {
            case STRIKE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        strikeAnimation(adp());
                    } else {
                        slashAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                intoDrawMo(new Wound(), STATUS, this);
                break;
            }
            case LUMBER: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == multiplier - 1) {
                        finishAnimation(adp());
                    } else if (i % 2 == 0) {
                        slashAnimation(adp());
                    } else {
                        strikeAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case PULSE: {
                strikeAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(STRIKE)) {
            possibilities.add(STRIKE);
        }
        if (!this.lastMove(LUMBER)) {
            possibilities.add(LUMBER);
        }
        if (!this.lastMove(PULSE) && !this.lastMoveBefore(PULSE)) {
            possibilities.add(PULSE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void strikeAnimation(AbstractCreature enemy) {
        animationAction("Strike", "WoodStrike", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "Slash", enemy, this);
    }

    private void finishAnimation(AbstractCreature enemy) {
        animationAction("Finish", "WoodFinish", enemy, this);
    }

}