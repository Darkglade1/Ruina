package ruina.monsters.angela.Abnormalities;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.angela.AbnormalityContainer;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.makeInHand;

public class Angela_BloodBath extends AbnormalityContainer
{
    public static final String ID = makeID("Angela_BloodBath");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);

    public Angela_BloodBath(){
        this(0, 350f);
    }

    public Angela_BloodBath(final float x, final float y) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FreshMeat/Spriter/FreshMeat.scml"));
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(60);
        } else {
            setHp(80);
        }
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        Bloodbath bloodbath = new Bloodbath();
        atb(new SpawnMonsterAction(bloodbath, true));
        atb(new UsePreBattleActionAction(bloodbath));
        bloodbath.rollMove();
        bloodbath.createIntent();

    }

    @Override
    public void takeTurn() {
        getAbnormality(timesBreached++);

    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}