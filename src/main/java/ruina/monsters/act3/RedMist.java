package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.cards.Dazzled;
import ruina.cards.EGO.act2.Mimicry;
import ruina.monsters.AbstractCardMonster;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RedMist extends AbstractCardMonster
{
    public static final String ID = makeID(RedMist.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SALVATION = 0;
    private static final byte DAZZLE_ENEMY = 1;
    private static final byte DAZZLE_PLAYER = 2;
    private static final byte ILLUMINATE = 3;

    private final int STATUS = calcAscensionSpecial(3);
    private final int DEBUFF = calcAscensionSpecial(1);

    public static final String Salvation_POWER_ID = makeID("Salvation");
    public static final PowerStrings SalvationPowerStrings = CardCrawlGame.languagePack.getPowerStrings(Salvation_POWER_ID);
    public static final String Salvation_POWER_NAME = SalvationPowerStrings.NAME;
    public static final String[] Salvation_POWER_DESCRIPTIONS = SalvationPowerStrings.DESCRIPTIONS;

    public RedMist() {
        this(100.0f, 0.0f);
    }

    public RedMist(final float x, final float y) {
        super(NAME, ID, 400, -5.0F, 0, 300.0f, 355.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BigBird/Spriter/BigBird.scml"));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(SALVATION, Intent.ATTACK, calcAscensionDamage(15));
        addMove(DAZZLE_ENEMY, Intent.STRONG_DEBUFF);
        addMove(DAZZLE_PLAYER, Intent.DEBUFF);
        addMove(ILLUMINATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
    }

    @Override
    public void usePreBattleAction() {

    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case SALVATION: {
                dazzleAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case DAZZLE_ENEMY: {

                break;
            }
            case DAZZLE_PLAYER: {
                specialAnimation(target);
                intoDrawMo(new Dazzled(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case ILLUMINATE: {
                dazzleAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void salvation1Animation(AbstractCreature enemy) {
        animationAction("Salvation1", "BigBirdOpen", enemy, this);
    }

    private void salvation2Animation(AbstractCreature enemy) {
        animationAction("Salvation2", "BigBirdCrunch", enemy, this);
    }

    private void dazzleAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdLamp", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdEyes", enemy, this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SALVATION)) {
            possibilities.add(SALVATION);
        }
        if (!this.lastMove(DAZZLE_PLAYER)) {
            possibilities.add(DAZZLE_PLAYER);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], new Mimicry());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            setAdditionalMoveShortcut(SALVATION, moveHistory, new Mimicry());
        }
        if (whichMove == 1) {
            setAdditionalMoveShortcut(SALVATION, moveHistory, new Mimicry());
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

}