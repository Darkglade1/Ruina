package ruina.monsters.act1.laetitia;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.BetterSpriterAnimation;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.LonelyIsSad;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Laetitia extends AbstractRuinaMonster {
    public static final String ID = makeID(Laetitia.class.getSimpleName());

    private static final byte GIFT = 0;
    private static final byte FUN = 1;

    private final int giftGifts = 1;

    private final int DAMAGE_INCREASE;

    private final AbstractCard gift = new Gift();

    public Laetitia() {
        this(140.0f, 0.0f);
    }

    public Laetitia(final float x, final float y) {
        super(ID, ID, 100, 0.0F, 0, 200.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Laetitia/Spriter/Laetitia.scml"));
        setHp(calcAscensionTankiness(100));
        addMove(GIFT, Intent.ATTACK_DEBUFF, calcAscensionDamage(4));
        addMove(FUN, Intent.ATTACK, calcAscensionDamage(2), 2);

        if (AbstractDungeon.ascensionLevel >= 18) {
            gift.upgrade();
            DAMAGE_INCREASE = 75;
        } else {
            DAMAGE_INCREASE = 100;
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        atb(new ApplyPowerAction(this, this, new LonelyIsSad(this, DAMAGE_INCREASE)));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case GIFT: {
                attackAnimation(adp());
                dmg(adp(), info);
                atb(new MakeTempCardInHandAction(gift.makeStatEquivalentCopy(), giftGifts));
                resetIdle();
                break;
            }
            case FUN: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastTwoMoves(GIFT)) {
            setMoveShortcut(FUN);
        } else {
            setMoveShortcut(GIFT);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GiftFriend || mo instanceof WitchFriend) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaAtk", enemy, this);
    }

}