package ruina.monsters.uninvitedGuests.eileen;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Eileen extends AbstractCardMonster
{
    public static final String ID = makeID(Eileen.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PREACH = 0;
    private static final byte ACCELERATE = 1;
    private static final byte PROPAGATE = 2;
    private static final byte BRAINWASH = 3;

    public final int BLOCK = calcAscensionTankiness(22);
    public final int STRENGTH = calcAscensionSpecial(4);
    public final int VULNERABLE = calcAscensionSpecial(1);
    public final int HP_LOSS = 100;

    public Yesod yesod;

    public AbstractMonster[] minions = new AbstractMonster[2];

    public static final String POWER_ID = makeID("Gears");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Eileen() {
        this(0.0f, 0.0f);
    }

    public Eileen(final float x, final float y) {
        super(NAME, ID, 1000, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Eileen/Spriter/Eileen.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(PREACH, Intent.BUFF);
        addMove(ACCELERATE, Intent.DEFEND);
        addMove(PROPAGATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(BRAINWASH, Intent.ATTACK, calcAscensionDamage(16));

//        cardList.add(new Eventide(this));
//        cardList.add(new Emotions(this));
//        cardList.add(new Stigmatize(this));
//        cardList.add(new Searing(this));
//        cardList.add(new Sorrow(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Yesod) {
                yesod = (Yesod)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, HP_LOSS) {

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case PREACH: {
                buffAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        applyToTargetNextTurn(mo, new StrengthPower(this, STRENGTH));
                    }
                }
                resetIdle();
                break;
            }
            case ACCELERATE: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        block(mo, BLOCK);
                    }
                }
                resetIdle();
                break;
            }
            case PROPAGATE: {
                rangeAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                break;
            }
            case BRAINWASH: {
                strongAttackAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
    }

    private void strongAttackAnimation(AbstractCreature enemy) {
        animationAction("Attack2", "GearStrongAtk", enemy, this);
    }

    private void rangeAnimation(AbstractCreature enemy) {
        animationAction("Attack1", "GearFar", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Buff", "GearStrongStart", this);
    }

    private void blockAnimation() {
        animationAction("Block", "GearStrongStart", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, yesod);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        Summon();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(PREACH)) {
            setMoveShortcut(ACCELERATE, MOVES[ACCELERATE], cardList.get(ACCELERATE).makeStatEquivalentCopy());
        } else {
            setMoveShortcut(PREACH, MOVES[PREACH], cardList.get(PREACH).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(PROPAGATE, moveHistory)) {
            setAdditionalMoveShortcut(BRAINWASH, moveHistory, cardList.get(BRAINWASH).makeStatEquivalentCopy());
        } else {
            setAdditionalMoveShortcut(PROPAGATE, moveHistory, cardList.get(PROPAGATE).makeStatEquivalentCopy());
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, yesod, yesod.allyIcon);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GearsWorshipper) {
                atb(new SuicideAction(mo));
            }
        }
        yesod.onBossDeath();
    }

    public void Summon() {
        //float xPos_Farthest_L = -450.0f;
        float xPos_Middle_L = -175.0f;
        float xPos_Short_L = 0F;
        float y = 125.0f;

        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new GearsWorshipper(xPos_Middle_L, y, this);
                } else {
                    minion = new GearsWorshipper(xPos_Short_L, y, this);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                minions[i] = minion;
            }
        }
    }

    public void onMinionDeath() {
        atb(new LoseHPAction(this, this, HP_LOSS));
    }

}