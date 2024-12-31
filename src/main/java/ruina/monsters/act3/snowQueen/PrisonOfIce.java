package ruina.monsters.act3.snowQueen;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.FrozenMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act3.KaiAndGerda;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PrisonOfIce extends AbstractRuinaMonster
{
    public static final String ID = makeID(PrisonOfIce.class.getSimpleName());
    private static final byte NONE = 0;

    private SnowQueen queen;

    public PrisonOfIce(final float x, final float y) {
        super(ID, ID, 70, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("PrisonOfIce/Spriter/PrisonOfIce.scml"));
        setHp(calcAscensionTankiness(70));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof SnowQueen) {
                this.queen = (SnowQueen) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new KaiAndGerda(this));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!queen.isDeadOrEscaped()) {
            playSound("SnowPrisonBreak", 0.5f);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard card : adp().hand.group) {
                        if (CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                            CardModifierManager.removeModifiersById(card, FrozenMod.ID, false);
                        }
                    }
                    this.isDone = true;
                }
            });
        }
    }
}