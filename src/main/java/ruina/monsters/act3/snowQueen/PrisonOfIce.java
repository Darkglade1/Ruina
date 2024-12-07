package ruina.monsters.act3.snowQueen;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.FrozenMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PrisonOfIce extends AbstractRuinaMonster
{
    public static final String ID = makeID(PrisonOfIce.class.getSimpleName());

    private static final byte NONE = 0;

    public static final String POWER_ID = makeID("KaiAndGerda");
    public static final PowerStrings PowerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = PowerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = PowerStrings.DESCRIPTIONS;

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
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
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
        queen.canBlizzard = true;
        if (!queen.isDeadOrEscaped()) {
            playSound("SnowPrisonBreak");
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