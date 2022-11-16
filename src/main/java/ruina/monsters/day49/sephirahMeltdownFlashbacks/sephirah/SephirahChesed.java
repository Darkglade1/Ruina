package ruina.monsters.day49.sephirahMeltdownFlashbacks.sephirah;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AbnormalityQueenOfHatred;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SephirahChesed extends AbstractRuinaMonster
{
    public static final String ID = makeID(SephirahChesed.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PALE_HANDS = 0;
    private static final byte DEPRESSION = 1;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(3);

    public SephirahChesed() {
        this(-450.0f, 0.0f);
    }

    public SephirahChesed(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 320.0f, null, x, y);
        loadAnimation(makeMonsterPath("Day49/SephirahMeltdownFlashbacks/Chesed/chesed.atlas"), makeMonsterPath("Day49/SephirahMeltdownFlashbacks/Chesed/chesed.json"), 0.65f);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Phase1", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Phase1", "Phase2", 1f);
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(88), calcAscensionTankiness(94));
        addMove(PALE_HANDS, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(DEPRESSION, Intent.DEFEND_DEBUFF);


    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case PALE_HANDS: {
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                break;
            }
            case DEPRESSION: {
                block(this, BLOCK);
                applyToTarget(adp(), this, new StrengthPower(adp(), -DEBUFF));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEBUFF));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void usePreBattleAction() {
        atb(new SpawnMonsterAction(new AbnormalityQueenOfHatred(), false));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(PALE_HANDS)) {
            setMoveShortcut(DEPRESSION);
        } else {
            setMoveShortcut(PALE_HANDS);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BloodAttack", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "BloodSpecial", this);
    }

}