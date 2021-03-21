package ruina.monsters.uninvitedGuests.argalia.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.act2.LittleRed;
import ruina.monsters.eventboss.redMist.cards.*;
import ruina.monsters.eventboss.yan.cards.*;
import ruina.monsters.uninvitedGuests.argalia.cards.*;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.RedMistPower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Argalia extends AbstractDeckMonster
{
    public static final String ID = makeID(Argalia.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte LARGO = 0;
    private static final byte ALLEGRO = 1;
    private static final byte SCYTHE = 2;
    private static final byte TRAILS = 3;
    private static final byte DANZA = 4;

    public final int largoBlock = calcAscensionTankiness(12);
    public final int largoDamage = calcAscensionDamage(15);

    public final int allegroDamage = calcAscensionDamage(6);
    public final int allegroHits = 3;
    public final int allegroVibration = calcAscensionSpecial(2);

    public final int scytheDamage = calcAscensionDamage(35);

    public final int trailsDamage = calcAscensionDamage(20);
    public final int trailsHits = 2;

    public final int tempestuousDamage = calcAscensionTankiness(5);
    public final int tempestuousHits = 5;
    public final int tempestuousVibrationInc = 7;

    private boolean queueDanza = false;
    private int additionalActions = 1;

    public Roland roland;

    public Argalia() { this(0.0f, 0.0f); }

    public Argalia(final float x, final float y) {
        super(NAME, ID, 1800, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Argalia/Spriter/Argalia.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        maxAdditionalMoves = 3;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = 3;

        addMove(LARGO, Intent.ATTACK_DEFEND, largoDamage);
        addMove(ALLEGRO, Intent.ATTACK_DEBUFF, allegroDamage, allegroHits, true);
        addMove(SCYTHE, Intent.ATTACK, scytheDamage);
        addMove(TRAILS, Intent.ATTACK, trailsDamage, trailsHits, true);
        addMove(DANZA, IntentEnums.MASS_ATTACK, tempestuousDamage, tempestuousHits, true);
        initializeDeck();
    }

    @Override
    public void usePreBattleAction()
    {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Roland) { roland = (Roland) mo; }
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        int[] damageArray;
        switch (move.nextMove) {
            case LARGO: {
                dmg(target, info);
                block(this, largoBlock);
                break;
            }
            case ALLEGRO: {
                for (int i = 0; i < multiplier; i++) { dmg(target, info); }
                break;
            }
            case SCYTHE: {
                dmg(target, info);
                break;
            }
            case TRAILS:
                for (int i = 0; i < multiplier; i++) { dmg(target, info); }
                break;
            case DANZA: {
                damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                for (int i = 0; i < multiplier; i++) {
                    atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                }
                queueDanza = false;
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
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (roland.isDead || roland.isDying) { takeCustomTurn(additionalMove, adp());
            } else { takeCustomTurn(additionalMove, roland); }
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
                calculateAllocatedMoves();
                danzaCheck();
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(queueDanza){ setMoveShortcut(DANZA, MOVES[DANZA], new CHRBOSS_TempestuousDanza(this)); }
        else { createMoveFromCard(topDeckCardForMoveAction()); }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) { createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }

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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, roland, roland.allyIcon);
            }
        }
    }

    @Override
    protected void createDeck() {
        for(int i = 0; i < 3; i += 1){
            masterDeck.addToBottom(new CHRBOSS_Largo(this));
            masterDeck.addToBottom(new CHRBOSS_Allegro(this));
            masterDeck.addToBottom(new CHRBOSS_TrailsOfBlue(this));
            masterDeck.addToBottom(new CHRBOSS_ResonantScythe(this));
        }
    }

    protected void createMoveFromCard(AbstractCard c) {
        if (c.cardID.equals(CHRBOSS_Largo.ID)) { setMoveShortcut(LARGO, MOVES[0], c);
        } else if (c.cardID.equals(CHRBOSS_Allegro.ID)) { setMoveShortcut(ALLEGRO, MOVES[1], c);
        } else if (c.cardID.equals(CHRBOSS_ResonantScythe.ID)) { setMoveShortcut(SCYTHE, MOVES[2], c);
        } else if (c.cardID.equals(CHRBOSS_TrailsOfBlue.ID)) { setMoveShortcut(TRAILS, MOVES[3], c);
        } else if (c.cardID.equals(CHRBOSS_TempestuousDanza.ID)) { setMoveShortcut(DANZA, MOVES[4], c);
        } else { setMoveShortcut(ALLEGRO, MOVES[1], c); }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(CHRBOSS_Largo.ID)) {
            setAdditionalMoveShortcut(LARGO, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Allegro.ID)) {
            setAdditionalMoveShortcut(ALLEGRO, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_ResonantScythe.ID)) {
            setAdditionalMoveShortcut(SCYTHE, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_TrailsOfBlue.ID)) {
            setAdditionalMoveShortcut(TRAILS, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_TempestuousDanza.ID)) {
            setAdditionalMoveShortcut(DANZA, moveHistory, c);
        } else {
            setAdditionalMoveShortcut(LARGO, moveHistory, c);
        }
    }

    public void calculateAllocatedMoves(){

    }
    public void danzaCheck(){

    }

}