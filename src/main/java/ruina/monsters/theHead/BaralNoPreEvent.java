package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act5.AClaw;
import ruina.powers.act5.PlayerBackAttack;
import ruina.powers.act5.SingularityT;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class BaralNoPreEvent extends Baral
{
    public BaralNoPreEvent(final float x, final float y) {
        super(x, y);
        currentPhase = PHASE.PHASE2;
        numAdditionalMoves++;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.lastCombatMetricKey = METRICS_ID;
        CustomDungeon.playTempMusicInstantly("TheHead");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Zena) {
                zena = (Zena)mo;
            }
            if (mo instanceof RolandHead) {
                roland = (RolandHead) mo;
                target = (RolandHead)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AClaw(this, KILL_THRESHOLD));
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new SingularityT(this));
        }
    }

    @Override
    protected void handlePreEvent() {
        // override this to do nothing
    }
}