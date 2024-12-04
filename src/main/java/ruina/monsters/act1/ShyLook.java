package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ShyLook extends AbstractRuinaMonster
{
    public static final String ID = makeID(ShyLook.class.getSimpleName());

    private static final byte DEFEND = 1;
    private static final byte BUFF = 2;
    private static final byte DEBUFF_ATTACK = 3;
    private static final byte DEFEND_ATTACK = 4;
    private static final byte ATTACK = 5;

    private final int BLOCK = calcAscensionTankiness(13);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int ATTACK_BLOCK = calcAscensionTankiness(6);

    private final int CARD_THRESHOLD = 3;
    private int face = 1;

    public static final String POWER_ID = makeID("Expression");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ShyLook() {
        this(0.0f, 0.0f);
    }

    public ShyLook(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ShyLook/Spriter/ShyLook.scml"));
        setHp(calcAscensionTankiness(67), calcAscensionTankiness(72));
        addMove(DEFEND, Intent.DEFEND);
        addMove(BUFF, Intent.BUFF);
        addMove(DEBUFF_ATTACK, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(DEFEND_ATTACK, Intent.ATTACK_DEFEND, calcAscensionDamage(12));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(16));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= CARD_THRESHOLD) {
                    flash();
                    this.amount = 0;
                    rollMove();
                    createIntent();
                } else {
                    flashWithoutSound();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CARD_THRESHOLD + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case DEFEND: {
                blockAnimation();
                block(this, BLOCK);
                resetIdle();
                break;
            }
            case BUFF: {
                blockAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case DEBUFF_ATTACK: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case DEFEND_ATTACK: {
                block(this, ATTACK_BLOCK);
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case ATTACK: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(DEFEND)) {
            possibilities.add(DEFEND);
        }
        if (!this.lastMove(BUFF)) {
            possibilities.add(BUFF);
        }
        if (!this.lastMove(DEBUFF_ATTACK)) {
            possibilities.add(DEBUFF_ATTACK);
        }
        if (!this.lastMove(DEFEND_ATTACK)) {
            possibilities.add(DEFEND_ATTACK);
        }
        if (!this.lastMove(ATTACK)) {
            possibilities.add(ATTACK);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[0]);
        face = move;
        runAnim("Idle" + face);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + face);
                this.isDone = true;
            }
        });
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack" + face, "ShyAtk", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block" + face, null, this);
    }

}