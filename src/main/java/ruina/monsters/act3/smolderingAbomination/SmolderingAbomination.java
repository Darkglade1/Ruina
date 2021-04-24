package ruina.monsters.act3.smolderingAbomination;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ShiftingPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.normal.eileen.GearsWorshipper;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Accelerate;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Preach;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Propagate;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.ThoughtGearBrainwash;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Paralysis;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class SmolderingAbomination extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(SmolderingAbomination.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SPEW_SMOKE = 0;
    private static final byte AMALGAMATED_SINEWS = 1;
    private static final byte TAIL_SWIPE = 2;
    private static final byte UNKNOWN = 3;

    private int SMOKE_DAMAGE = calcAscensionDamage(55);
    private int SMOKE_HAZE = calcAscensionSpecial(3);

    private int SINEWS_DAMAGE = calcAscensionDamage(35);
    private int SINEWS_DAMAGE_INCREASE = calcAscensionDamage(5);
    private int SINEWS_HITS = 3;

    private int TAIL_SWIPE_DAMAGE = calcAscensionDamage(40);
    private int TAIL_SWIPE_PARALYSIS = calcAscensionSpecial(2);

    public AbstractMonster[] minions = new AbstractMonster[2];

    public static final String POWER_ID = makeID("HatchingDead");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SmolderingAbomination() {
        this(200.0f, 0.0f);
    }

    public SmolderingAbomination(final float x, final float y) {
        super(NAME, ID, 1500, -5.0F, 0, 160.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Eileen/Spriter/Eileen.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(SPEW_SMOKE, Intent.ATTACK_BUFF, SMOKE_DAMAGE);
        addMove(AMALGAMATED_SINEWS, Intent.ATTACK, SINEWS_DAMAGE, SINEWS_HITS, true);
        addMove(TAIL_SWIPE, Intent.ATTACK_DEBUFF, TAIL_SWIPE_DAMAGE);
        addMove(UNKNOWN, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new ShiftingPower(this));
        Summon();
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case SPEW_SMOKE: {
                dmg(target, info);
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof SmolderingByproduct) { applyToTargetNextTurn(mo, new StrengthPower(this, SMOKE_HAZE)); }
                }
                break;
            }
            case AMALGAMATED_SINEWS: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                }
                SINEWS_DAMAGE += SINEWS_DAMAGE_INCREASE;
                break;
            }
            case TAIL_SWIPE: {
                dmg(target, info);
                applyToTarget(target, this, new Paralysis(target, TAIL_SWIPE_PARALYSIS));
                break;
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        Summon();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) { setMoveShortcut(UNKNOWN); }
        else if (lastMove(UNKNOWN)){ setMoveShortcut(SPEW_SMOKE, MOVES[SPEW_SMOKE]); }
        else if (lastMove(AMALGAMATED_SINEWS)) { setMove(MOVES[AMALGAMATED_SINEWS], AMALGAMATED_SINEWS, Intent.ATTACK, SINEWS_DAMAGE, SINEWS_HITS, true); }
        else { setMoveShortcut(TAIL_SWIPE, MOVES[TAIL_SWIPE]); }
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GearsWorshipper) {
                atb(new SuicideAction(mo));
            }
        }
    }

    public void Summon() {
        //float xPos_Farthest_L = -450.0f;
        float xPos_Middle_L = -175.0f;
        float xPos_Short_L = 0F;
        float y = 0.0f;

        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new SmolderingByproduct(xPos_Middle_L, y, this);
                } else {
                    minion = new SmolderingByproduct(xPos_Short_L, y, this);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                minions[i] = minion;
            }
        }
    }


}