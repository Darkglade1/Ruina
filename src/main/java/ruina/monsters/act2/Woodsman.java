package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.InvisibleEnergyPower;
import ruina.powers.act2.Heart;
import ruina.util.DetailedIntent;
import ruina.vfx.BloodSplatter;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Woodsman extends AbstractRuinaMonster
{
    public static final String ID = makeID(Woodsman.class.getSimpleName());

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
        super(ID, ID, 40, -5.0F, 0, 230.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Woodsman/Spriter/Woodsman.scml"));
        setHp(calcAscensionTankiness(140), calcAscensionTankiness(148));
        addMove(STRIKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(10), 2, true);
        addMove(LUMBER, Intent.ATTACK, calcAscensionDamage(4), 4, true);
        addMove(PULSE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(adp(), adp(), new InvisibleEnergyPower(adp(), ENERGY_GAIN));
        applyToTarget(this, this, new Heart(this, STRENGTH, ENERGY_GAIN));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
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
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (int j = 0; j < 6; j++)  {AbstractDungeon.effectsQueue.add(new BloodSplatter(0.5F)); }
                        isDone = true;
                    }
                });
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
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case STRIKE: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.WOUND_TEXTURE, DetailedIntent.TargetType.DRAW_PILE);
                detailsList.add(detail);
                break;
            }
            case PULSE: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void strikeAnimation(AbstractCreature enemy) {
        animationAction("Strike", "WoodFinish", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "WoodStrike", enemy, this);
    }

    private void finishAnimation(AbstractCreature enemy) {
        animationAction("Finish", "WoodFinish", enemy, this);
    }

}