package ruina.monsters.act1.laetitia;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class WitchFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(WitchFriend.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private final AbstractCard card = new Gift();

    private static final byte SPRINGS_GENESIS = 0;
    private static final byte FULL_BLOOM = 1;
    private static final byte MAGNIFICENT_END = 2;

    private static final byte GLITCH = 0;

    private final int glitchDamage = calcAscensionDamage(15);

    public static final String POWER_ID = makeID("LonelyIsSad");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Laetitia parent;

    public WitchFriend(final float x, final float y, Laetitia elite) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alriune/Spriter/Alriune.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(15));
        addMove(GLITCH, Intent.ATTACK, glitchDamage);
        parent = elite;
        firstMove = true;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {

    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        parent.onMonsterDeath();
    }

    @Override
    public void takeTurn() {
        if(firstMove){
            firstMove = false;
        }
        else {
            DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            int multiplier = this.moves.get(nextMove).multiplier;
            if (info.base > -1) {
                info.applyPowers(this, adp());
            }
            switch (this.nextMove) {
                case GLITCH: {
                    dmg(adp(), info);
                    break;
                }
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(GLITCH, MOVES[GLITCH]);
    }

}