package ruina.monsters.uninvitedGuests.puppeteer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.act2.HermitStaff;
import ruina.monsters.act2.ServantOfWrath;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Puppeteer extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Puppeteer.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PULLING_STRINGS_TAUT = 0;
    private static final byte TUGGING_STRINGS = 1;
    private static final byte ASSAILING_PULLS = 2;
    private static final byte THIN_STRINGS = 3;
    private static final byte PUPPETRY = 4;

    private final int BLOCK = calcAscensionTankiness(11);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    public Chesed chesed;
    public Puppet puppet;

    public Puppeteer() {
        this(100.0f, 0.0f);
    }

    public Puppeteer(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hermit/Spriter/Hermit.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(600));

        addMove(PULLING_STRINGS_TAUT, IntentEnums.MASS_ATTACK, calcAscensionDamage(36));
        addMove(TUGGING_STRINGS, Intent.ATTACK, calcAscensionDamage(11), 2, true);
        addMove(ASSAILING_PULLS, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(THIN_STRINGS, Intent.DEFEND_DEBUFF);
        addMove(PUPPETRY, Intent.BUFF);
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Chesed) {
                chesed = (Chesed)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        Summon();
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
            case PULLING_STRINGS_TAUT: {
                attack1Animation(target);
                dmg(target, info);
                resetIdle();
                if (target == adp()) {
                    applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                } else {
                    applyToTarget(target, this, new VulnerablePower(target, DEBUFF, true));
                }
                break;
            }
            case TUGGING_STRINGS: {
                attack2Animation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case ASSAILING_PULLS: {
                specialAnimation();
                buff();
                resetIdle(1.0f);
                break;
            }
            case THIN_STRINGS: {
                specialAnimation();
                if (staff == null) {
                    Summon();
                } else {
                    buff(); //in case someone forces this enemy to use this intent twice in a row :)
                }
                resetIdle(1.0f);
                break;
            }
            case PUPPETRY: {
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof Puppeteer || mo instanceof HermitStaff) {
                        block(mo, BLOCK);
                        applyToTargetNextTurn(mo, new StrengthPower(this, STRENGTH));
                    }
                }
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "HermitAtk", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "HermitStrongAtk", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "HermitWand", this);
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
            if (wrath.isDead || wrath.isDying) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, wrath);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (staff == null && !firstMove) {
            setMoveShortcut(HELLO);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(HOLD_STILL)) {
                possibilities.add(HOLD_STILL);
            }
            if (!this.lastMove(MAKE_WAY)) {
                possibilities.add(MAKE_WAY);
            }
            if (!this.lastMove(CRACKLE) && !this.lastMoveBefore(CRACKLE)) {
                possibilities.add(CRACKLE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(HOLD_STILL, moveHistory)) {
            possibilities.add(HOLD_STILL);
        }
        if (!this.lastTwoMoves(MAKE_WAY, moveHistory)) {
            possibilities.add(MAKE_WAY);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, wrath, wrath.allyIcon);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        wrath.onHermitDeath();
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof HermitStaff) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void Summon() {
        Puppeteer hermit = this;
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                float xPosition = -200.0F;
                staff = new HermitStaff(xPosition, 0.0f, hermit);
                att(new UsePreBattleActionAction(staff));
                att(new SpawnMonsterAction(staff, true));
                this.isDone = true;
            }
        });
    }

}