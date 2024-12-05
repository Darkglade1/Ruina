package ruina.monsters.act3.punishingBird;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.ForestKeeperLock;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Keeper extends AbstractRuinaMonster {
    public static final String ID = makeID(Keeper.class.getSimpleName());

    private static final byte CUCKOO = 0;
    private static final byte RING = 1;
    private static final byte SMACK = 2;

    public static final String lock_pid = makeID("Lock");
    public static final PowerStrings str_lock = CardCrawlGame.languagePack.getPowerStrings(lock_pid);
    public static final String lock_pname = str_lock.NAME;
    public static final String[] lock_desc = str_lock.DESCRIPTIONS;

    public static final AbstractCard card = new ForestKeeperLock();

    public Keeper(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 200.0f, 220.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Keeper/Spriter/Keeper.scml"));
        setHp(calcAscensionTankiness(40), calcAscensionTankiness(48));
        addMove(CUCKOO, Intent.DEFEND);
        addMove(RING, Intent.ATTACK, calcAscensionDamage(6), 2);
        addMove(SMACK, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (nextMove) {
            case CUCKOO:
                specialAnimation();
                block(this, calcAscensionTankiness(12));
                resetIdle(1.0f);
                break;
            case RING:
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            case SMACK:
                attackAnimation(adp());
                dmg(adp(), info);
                atb(new ApplyPowerAction(adp(), this, new Paralysis(adp(), calcAscensionSpecial(1))));
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(CUCKOO)) {
            if (num <= 45) {
                setMoveShortcut(RING);
            } else {
                setMoveShortcut(SMACK);
            }
        } else if (lastMove(RING)) {
            if (num <= 45) {
                setMoveShortcut(CUCKOO);
            } else {
                setMoveShortcut(SMACK);
            }
        } else if (lastMove(SMACK)) {
            if (num <= 45) {
                setMoveShortcut(CUCKOO);
            } else {
                setMoveShortcut(RING);
            }
        } else {
            if (num <= 33) {
                setMoveShortcut(CUCKOO);
            } else if (num <= 66) {
                setMoveShortcut(RING);
            } else {
                setMoveShortcut(SMACK);
            }
        }
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        applyToTarget(this, this, new AbstractLambdaPower(lock_pname, lock_pid, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = lock_desc[0];
            }
        });
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        atb(new MakeTempCardInHandAction(card.makeStatEquivalentCopy(), 1));
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(card));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Smack", "BluntBlow", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Ring", "BossBirdSpecial", this);
    }
}