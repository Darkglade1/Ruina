package ruina.monsters.uninvitedGuests.normal.bremen;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.cards.Melody;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.bremen.bremenCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.MelodyPower;
import ruina.powers.Paralysis;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Bremen extends AbstractCardMonster
{
    public static final String ID = makeID(Bremen.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte MELODY = 0;
    private static final byte NEIGH = 1;
    private static final byte BAWK = 2;
    private static final byte RARF = 3;
    private static final byte TENDON = 4;
    private static final byte TRIO = 5;

    public final int tendonHits = 2;
    public final int trioHits = 3;

    public final int STATUS = calcAscensionSpecial(3);
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int PARALYSIS = calcAscensionSpecial(2);
    public final int BLOCK = calcAscensionTankiness(22);
    public final int MELODY_LENGTH = 3;
    public final int MELODY_LENGTH_INCREASE = 1;
    private int currentMelodyLength = MELODY_LENGTH;
    public final int MELODY_BOSS_STR = calcAscensionSpecial(2);
    public final int MELODY_PLAYER_STR = 1;
    public final int TRIO_COOLDOWN = 2;
    private int cooldown = TRIO_COOLDOWN;
    public Netzach netzach;
    private boolean lastIntentTargetAlly = true;
    public Melody melodyCard = new Melody();
    private MelodyPower melodyPower = new MelodyPower(this, MELODY_LENGTH, MELODY_PLAYER_STR, MELODY_BOSS_STR, melodyCard, this);

    public static final String POWER_ID = makeID("Melody");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Bremen() {
        this(0.0f, 0.0f);
    }

    public Bremen(final float x, final float y) {
        super(NAME, ID, 850, -5.0F, 0, 200.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bremen/Spriter/Bremen.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(850));

        addMove(MELODY, Intent.ATTACK_BUFF, calcAscensionDamage(8));
        addMove(NEIGH, Intent.ATTACK, calcAscensionDamage(19));
        addMove(BAWK, Intent.DEBUFF);
        addMove(RARF, Intent.DEFEND_DEBUFF);
        addMove(TENDON, Intent.ATTACK, calcAscensionDamage(8), tendonHits, true);
        addMove(TRIO, Intent.ATTACK_BUFF, calcAscensionDamage(6), trioHits, true);
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
            if (mo instanceof Netzach) {
                netzach = (Netzach)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, melodyPower);
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new StrengthPower(this, 1)); //hacky solution again LOL
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case MELODY: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case NEIGH: {
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case BAWK: {
                debuffAnimation();
                intoDiscardMo(new Dazed(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case RARF: {
                pierceAnimation(target);
                block(this, BLOCK);
                applyToTarget(target, this, new Paralysis(target, PARALYSIS));
                resetIdle(1.0f);
                break;
            }
            case TENDON: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashAnimation(target);
                    } else {
                        pierceAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case TRIO: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        specialAttackAnimation(target);
                    } else {
                        specialAttackAnimation2(target);
                    }
                    dmg(target, info);
                    resetIdle(1.0f);
                }
                currentMelodyLength += MELODY_LENGTH_INCREASE;
                melodyPower.amount = currentMelodyLength;
                cooldown = TRIO_COOLDOWN + 1;
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BremenDog", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BremenHorse", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special1", "BremenStrong", enemy, this);
    }

    private void specialAttackAnimation2(AbstractCreature enemy) {
        animationAction("Special2", "BremenStrongFar", enemy, this);
    }

    private void debuffAnimation() {
        animationAction("Debuff", "BremenChicken", this);
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
                takeCustomTurn(additionalMove, netzach);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                cooldown--;
                lastIntentTargetAlly = !lastIntentTargetAlly;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (cooldown <= 0) {
            setMoveShortcut(TRIO, MOVES[TRIO], getMoveCardFromByte(TRIO));
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(RARF) && !this.lastMoveBefore(RARF)) {
                possibilities.add(RARF);
            }
            if (!this.lastMove(BAWK) && !this.lastMoveBefore(BAWK)) {
                possibilities.add(BAWK);
            }
            if (!this.lastMove(NEIGH) && !this.lastMoveBefore(NEIGH)) {
                possibilities.add(NEIGH);
            }
            if (!this.lastMove(TENDON) && !this.lastMoveBefore(TENDON)) {
                possibilities.add(TENDON);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (whichMove == 1 && !lastIntentTargetAlly) {
            if (!this.lastMove(BAWK, moveHistory) && !this.lastMoveBefore(BAWK, moveHistory)) {
                possibilities.add(BAWK);
            }
        }
        if (!this.lastMove(NEIGH, moveHistory) && !this.lastMoveBefore(NEIGH, moveHistory)) {
            possibilities.add(NEIGH);
        }
        if (!this.lastMove(TENDON, moveHistory) && !this.lastMoveBefore(TENDON, moveHistory)) {
            possibilities.add(TENDON);
        }
        if (whichMove == 1) {
            if (!this.lastMove(MELODY, moveHistory) && !this.lastMoveBefore(MELODY, moveHistory)) {
                possibilities.add(MELODY);
            }
        }
        if (possibilities.isEmpty()) {
            possibilities.add(NEIGH);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, getMoveCardFromByte(move));
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new EverlastingMelody(this));
        list.add(new Neigh(this));
        list.add(new Bawk(this));
        list.add(new Rarf(this));
        list.add(new Tendon(this));
        list.add(new Trio(this));
        return list.get(move);
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
                if (i == 1) {
                    if (lastIntentTargetAlly) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, netzach, netzach.icon);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, netzach, netzach.icon);
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        netzach.onBossDeath();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        float drawScale = 0.65f;
        float offsetX1 = 300.0F * Settings.scale;
        float offsetY = 100.0F * Settings.scale;
        AbstractCard card = melodyCard;
        card.drawScale = drawScale;
        card.current_x = this.hb.x + offsetX1;
        card.current_y = this.hb.y + offsetY;
        card.render(sb);
    }

}