package ruina.monsters.act2.jester;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.applyToTarget;

public class Statue extends AbstractRuinaMonster
{
    public static final String ID = makeID(Statue.class.getSimpleName());

    protected int magicalGirl;
    protected JesterOfNihil jester;
    private static final byte NONE = 0;

    public static final String MAGICALGIRL_POWER_ID = RuinaMod.makeID("MagicalGirl");
    public static final PowerStrings MAGICALGIRLPowerStrings = CardCrawlGame.languagePack.getPowerStrings(MAGICALGIRL_POWER_ID);
    public static final String MAGICALGIRL_POWER_NAME = MAGICALGIRLPowerStrings.NAME;
    public static final String[] MAGICALGIRL_POWER_DESCRIPTIONS = MAGICALGIRLPowerStrings.DESCRIPTIONS;

    public Statue(final float x, final float y, int girl) {
        super(ID, ID, 5, -5.0F, 0, 150.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Statue/Spriter/Statue.scml"));
        setHp(5);
        addMove(NONE, Intent.NONE);
        this.magicalGirl = girl;
        if (girl == 0) {
            name = MOVES[0];
            runAnim("Hate");

        }
        if (girl == 1) {
            name = MOVES[1];
            runAnim("Wrath");
        }
    }
    
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof JesterOfNihil) {
                jester = (JesterOfNihil) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new AbstractLambdaPower(MAGICALGIRL_POWER_NAME, MAGICALGIRL_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = MAGICALGIRL_POWER_DESCRIPTIONS[0] ;
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
        jester.SummonGirl(magicalGirl);
    }
}