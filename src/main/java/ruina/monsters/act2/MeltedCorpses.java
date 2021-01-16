package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class MeltedCorpses extends AbstractRuinaMonster
{
    public static final String ID = makeID(MeltedCorpses.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(50);
    private final int ROT = calcAscensionSpecial(2);

    public static final String ROT_POWER_ID = makeID("Rot");
    public static final PowerStrings ROTPowerStrings = CardCrawlGame.languagePack.getPowerStrings(ROT_POWER_ID);
    public static final String ROT_POWER_NAME = ROTPowerStrings.NAME;
    public static final String[] ROT_POWER_DESCRIPTIONS = ROTPowerStrings.DESCRIPTIONS;

    public static final String CORPSE_POWER_ID = makeID("Corpse");
    public static final PowerStrings CORPSEPowerStrings = CardCrawlGame.languagePack.getPowerStrings(CORPSE_POWER_ID);
    public static final String CORPSE_POWER_NAME = CORPSEPowerStrings.NAME;
    public static final String[] CORPSE_POWER_DESCRIPTIONS = CORPSEPowerStrings.DESCRIPTIONS;

    public MeltedCorpses() {
        this(0.0f, 0.0f);
    }

    public MeltedCorpses(final float x, final float y) {
        super(NAME, ID, 30, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Corpse/Spriter/Corpse.scml"));
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 8) {
            setHp(30);
        } else {
            setHp(40);
        }
        addMove(NONE, Intent.NONE);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(CORPSE_POWER_NAME, CORPSE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = CORPSE_POWER_DESCRIPTIONS[0] + HEAL + CORPSE_POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new AbstractLambdaPower(ROT_POWER_NAME, ROT_POWER_ID, AbstractPower.PowerType.BUFF, false, this, ROT) {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner == adp()) {
                    this.flash();
                    att(new DamageAction(info.owner, new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON, true));
                }
                return damageAmount;
            }
            @Override
            public void updateDescription() {
                description = ROT_POWER_DESCRIPTIONS[0] + amount + ROT_POWER_DESCRIPTIONS[1];
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
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.isDying || this.currentHealth <= 0) {
            if (info.owner instanceof Mountain) {
                atb(new HealAction(info.owner, info.owner, HEAL));
            }
        }
        AbstractDungeon.onModifyPower();
    }
}