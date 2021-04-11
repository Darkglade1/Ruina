package ruina.monsters.uninvitedGuests.normal.elena;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.HeatUp;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.HeatedWeapon;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.Obstruct;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.Rampage;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.Shockwave;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class VermilionCross extends AbstractCardMonster
{
    public static final String ID = makeID(VermilionCross.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte OBSTRUCT = 0;
    private static final byte SHOCKWAVE = 1;
    private static final byte HEATED_WEAPON = 2;
    private static final byte RAMPAGE = 3;
    private static final byte HEAT_UP = 4;

    public final int heatedWeaponHits = 2;

    public final int OBSTRUCT_BLOCK = calcAscensionTankiness(50);
    public final int HEAT_UP_BLOCK = calcAscensionTankiness(10);
    public final int STRENGTH = calcAscensionSpecial(5);
    public final int BURNS = calcAscensionSpecial(1);
    public final int allyIntangible = calcAscensionSpecial(1);
    public Binah binah;
    public Elena elena;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("VermilionIcon.png"));

    public VermilionCross() {
        this(0.0f, 0.0f);
    }

    public VermilionCross(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Vermilion/Spriter/Vermilion.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(OBSTRUCT, Intent.DEFEND);
        addMove(SHOCKWAVE, Intent.ATTACK_BUFF, calcAscensionDamage(16));
        addMove(HEATED_WEAPON, Intent.ATTACK_DEBUFF, calcAscensionDamage(7), heatedWeaponHits, true);
        addMove(RAMPAGE, Intent.ATTACK, calcAscensionDamage(45));
        addMove(HEAT_UP, Intent.DEFEND_BUFF);

        cardList.add(new Obstruct(this));
        cardList.add(new Shockwave(this));
        cardList.add(new HeatedWeapon(this));
        cardList.add(new Rampage(this));
        cardList.add(new HeatUp(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Binah) {
                binah = (Binah)mo;
            }
            if (mo instanceof Elena) {
                elena = (Elena)mo;
            }
        }
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
            case OBSTRUCT: {
                blockAnimation();
                block(this, OBSTRUCT_BLOCK);
                resetIdle();
                break;
            }
            case SHOCKWAVE: {
                slashAnimation(target);
                dmg(target, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (currentBlock > 0) {
                            AbstractCreature powerTarget = elena;
                            if (elena.isDeadOrEscaped()) {
                                powerTarget = VermilionCross.this;
                            }
                            applyToTargetTop(powerTarget, VermilionCross.this, new IntangiblePlayerPower(powerTarget, allyIntangible + 1));
                        }
                        this.isDone = true;
                    }
                });
                atb(new RemoveAllBlockAction(this, this));
                resetIdle();
                break;
            }
            case HEATED_WEAPON: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                intoDrawMo(new Burn(), BURNS, this);
                break;
            }
            case RAMPAGE: {
                strongAttackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case HEAT_UP: {
                blockAnimation();
                block(this, HEAT_UP_BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "FireVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "FireGuard", this);
    }

    private void strongAttackAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireStrong", enemy, this);
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
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, binah);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(OBSTRUCT)) {
            setMoveShortcut(SHOCKWAVE, MOVES[SHOCKWAVE], cardList.get(SHOCKWAVE).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (moveHistory.size() >= 3) {
                moveHistory.clear(); //resets the cooldowns after all moves have been used once
            }
            if (!this.lastMove(OBSTRUCT) && !this.lastMoveBefore(OBSTRUCT)) {
                possibilities.add(OBSTRUCT);
            }
            if (!this.lastMove(HEATED_WEAPON) && !this.lastMoveBefore(HEATED_WEAPON)) {
                possibilities.add(HEATED_WEAPON);
            }
            if (possibilities.isEmpty()) {
                possibilities.add(HEATED_WEAPON);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(RAMPAGE, moveHistory) && !this.lastMoveBefore(RAMPAGE, moveHistory)) {
            possibilities.add(RAMPAGE);
        }
        if (!this.lastMove(HEATED_WEAPON, moveHistory) && !this.lastMoveBefore(HEATED_WEAPON, moveHistory)) {
            possibilities.add(HEATED_WEAPON);
        }
        if (!this.lastMove(HEAT_UP, moveHistory) && !this.lastMoveBefore(HEAT_UP, moveHistory)) {
            possibilities.add(HEAT_UP);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, binah, binah.allyIcon);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        elena.onVermilionDeath();
        binah.targetEnemy = elena;
        if (elena.isDeadOrEscaped()) {
            binah.onBossDeath();
        }
    }

}