package ruina.monsters.theHead;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act5.AnArbiter;
import ruina.powers.act5.PlayerBackAttack;
import ruina.powers.act5.SingularityJ;
import ruina.powers.act5.SingularityJInvisible;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class ZenaNoPreEvent extends Zena {

    public ZenaNoPreEvent(final float x, final float y) {
        super(x, y);
        currentPhase = PHASE.PHASE2;
        numAdditionalMoves++;
        halfDead = false;
    }

    @Override
    public void usePreBattleAction() {
        adp().drawX = playerX;
        adp().dialogX = (adp().drawX + 20.0F) * Settings.scale;
        adp().flipHorizontal = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Baral) {
                baral = (Baral) mo;
            }
            if (mo instanceof GeburaHead) {
                gebura = (GeburaHead) mo;
                target = (GeburaHead)mo;
            }
            if (mo instanceof BinahHead) {
                binah = (BinahHead) mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AnArbiter(this, POWER_DEBUFF));
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new SingularityJ(this, baral));
            applyToTarget(baral, baral, new SingularityJInvisible(baral, this));
        }
        applyToTarget(adp(), this, new PlayerBackAttack(adp(), BACK_ATTACK_AMT));
    }

    @Override
    protected void handlePreEvent() {
        // override this to do nothing
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() == 1) { // this gives zena shockwave on turn 2
            setMoveShortcut(SHOCKWAVE, MOVES[SHOCKWAVE], cardList.get(SHOCKWAVE).makeStatEquivalentCopy());
        } else {
            super.getMove(num);
        }
    }

}