package ruina.monsters.act1.laetitia;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.powers.SurprisePresent;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class GiftFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(GiftFriend.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private final AbstractCard card = new Gift();

    public float storedX;

    private static final byte TAKE_IT = 0;
    private static final byte UNKNOWN = 1;

    private final int takeItDamage = calcAscensionDamage(6);

    public static final String POWER_ID = makeID("LonelyIsSad");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Laetitia parent;

    public GiftFriend(final float x, final float y, Laetitia elite) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alriune/Spriter/Alriune.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(30));
        addMove(TAKE_IT, Intent.ATTACK, takeItDamage);
        addMove(UNKNOWN, Intent.UNKNOWN);
        parent = elite;
        storedX = x;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new SurprisePresent(this, 3, calcAscensionDamage(10))));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (this.nextMove) {
            case TAKE_IT: {
                dmg(adp(), info);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        AbstractPower p = this.getPower("kaboom");
        if(p != null && p.amount == 1){
            setMoveShortcut(UNKNOWN, MOVES[UNKNOWN]);
        }
        else { setMoveShortcut(TAKE_IT, MOVES[TAKE_IT]); }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.isDying || this.currentHealth <= 0) {
            AbstractMonster giftFriend1 = new WitchFriend(storedX, 0.0f, parent);
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
}