package ruina.monsters.uninvitedGuests.pluto.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.act3.seraphim.GuardianApostle;
import ruina.monsters.eventboss.redMist.cards.*;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ConfusingContract;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ContractOfLight;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ContractOfMight;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.NoContract;
import ruina.powers.Bleed;
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

public class Pluto extends AbstractCardMonster {
    public static final String ID = makeID(Pluto.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SAFEGUARD = 0;
    private static final byte MISSLE = 1;
    private static final byte ONSLAUGHT = 2;

    public final int magicSafeguardBlock = calcAscensionTankiness(12);
    public final int magicSafeguardStr = calcAscensionSpecial(2);

    public final int magicMissleDamage = calcAscensionDamage(6);
    public final int magicMissleHits = 3;

    public final int magicOnslaughtDamage = calcAscensionDamage(15);
    public final int magicOnslaughtPerTurnScaling = calcAscensionDamage(5);

    public enum PHASE{
        SHADES,
        NO_SHADES
    }

    public PHASE currentPhase = PHASE.SHADES;

    public Pluto() {
        this(150f, 0.0f);
    }
    public Pluto(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RedMist/Spriter/RedMist.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        maxAdditionalMoves = 0;
        numAdditionalMoves = 0;

        addMove(SAFEGUARD, Intent.DEFEND_BUFF);
        addMove(MISSLE, Intent.ATTACK, magicMissleDamage, magicMissleHits, true);
    }

    @Override
    public void usePreBattleAction()
    {
        Summon();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> contracts = new ArrayList<>();
                contracts.add(new ConfusingContract());
                contracts.add(new ContractOfLight());
                contracts.add(new ContractOfMight());
                contracts.add(new NoContract());
                att(new ChooseOneAction(contracts));
                isDone = true;
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case SAFEGUARD: {
                for(AbstractMonster m: monsterList()){
                    block(m, magicSafeguardBlock);
                    atb(new ApplyPowerAction(m, m, new StrengthPower(m, magicSafeguardStr)));
                }
                break;
            }
            case MISSLE:
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                break;
            case ONSLAUGHT:
                dmg(adp(), info);
                break;
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
            takeCustomTurn(additionalMove, adp());
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
                boolean shadeExists = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof Shade){ shadeExists = true; }
                }
                if(!shadeExists && currentPhase.equals(PHASE.SHADES)){ currentPhase = PHASE.NO_SHADES; }
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(currentPhase.equals(PHASE.NO_SHADES)){
            setMove(MOVES[ONSLAUGHT], ONSLAUGHT, Intent.ATTACK, magicOnslaughtDamage + (magicOnslaughtPerTurnScaling * GameActionManager.turn), new Madness());
        }
        else {
            if(firstMove){ setMoveShortcut(SAFEGUARD, MOVES[SAFEGUARD], new Madness()); }
            else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(SAFEGUARD)) { possibilities.add(SAFEGUARD); }
                if (!this.lastMove(MISSLE)) { possibilities.add(MISSLE); }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
            }
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


    protected AbstractCard getMoveCardFromByte(Byte move) {
        switch (move){
            default: return new Madness();
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -750.0F;
        float xPos_Middle_L = -450F;
        float xPos_Short_L = -150F;
        float xPos_Shortest_L = 0F;
        AbstractMonster shade1 = new Shade(xPos_Middle_L, 0.0f);
        atb(new SpawnMonsterAction(shade1, true));
        atb(new UsePreBattleActionAction(shade1));
        shade1.rollMove();
        shade1.createIntent();
        AbstractMonster shade2 = new Shade(xPos_Short_L, 0.0f);
        atb(new SpawnMonsterAction(shade2, true));
        atb(new UsePreBattleActionAction(shade2));
        shade2.rollMove();
        shade2.createIntent();
    }

}