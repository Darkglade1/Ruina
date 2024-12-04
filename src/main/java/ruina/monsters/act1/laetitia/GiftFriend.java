package ruina.monsters.act1.laetitia;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.SurprisePresent;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GiftFriend extends AbstractRuinaMonster {
    public static final String ID = makeID(GiftFriend.class.getSimpleName());

    private final AbstractCard card = new Gift();

    public float storedX;

    private static final byte TAKE_IT = 0;
    private static final byte UNKNOWN = 1;

    public GiftFriend(final float x, final float y) {
        super(ID, ID, 20, 0.0F, 0, 200.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Friend/Spriter/Friend.scml"));
        setHp(calcAscensionTankiness(17), calcAscensionTankiness(19));
        addMove(TAKE_IT, Intent.ATTACK, calcAscensionDamage(6));
        addMove(UNKNOWN, Intent.UNKNOWN);
        storedX = x;
        if (AbstractDungeon.ascensionLevel >= 18) {
            card.upgrade();
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        atb(new ApplyPowerAction(this, this, new SurprisePresent(this, 3, calcAscensionSpecial(10), card)));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TAKE_IT: {
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
        AbstractPower p = this.getPower(SurprisePresent.POWER_ID);
        if (p != null && p.amount == 1) {
            setMoveShortcut(UNKNOWN);
        } else {
            setMoveShortcut(TAKE_IT);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.isDying || this.currentHealth <= 0) {
            AbstractMonster giftFriend1 = new WitchFriend(storedX, 0.0f);
            atb(new SpawnMonsterAction(giftFriend1, true));
            atb(new UsePreBattleActionAction(giftFriend1));
        }
        AbstractDungeon.onModifyPower();
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(card.makeStatEquivalentCopy()));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }
}