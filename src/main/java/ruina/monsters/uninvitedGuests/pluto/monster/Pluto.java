package ruina.monsters.uninvitedGuests.pluto.monster;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ConfusingContract;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ContractOfLight;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.ContractOfMight;
import ruina.monsters.uninvitedGuests.pluto.cards.contracts.NoContract;
import ruina.monsters.uninvitedGuests.pluto.plutoCards.BindingTerms;
import ruina.monsters.uninvitedGuests.pluto.plutoCards.Contract;
import ruina.monsters.uninvitedGuests.pluto.plutoCards.Missile;
import ruina.monsters.uninvitedGuests.pluto.plutoCards.Onslaught;
import ruina.monsters.uninvitedGuests.pluto.plutoCards.Safeguard;
import ruina.monsters.uninvitedGuests.puppeteer.Puppet;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

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
    private static final byte CONTRACT = 3;
    private static final byte BINDING_TERMS = 4;

    public final int magicSafeguardBlock = calcAscensionTankiness(18);
    public final int magicSafeguardStr = calcAscensionSpecial(2);

    public final int magicMissleDamage = calcAscensionDamage(6);
    public final int magicMissleHits = 3;

    public final int magicOnslaughtDamage = calcAscensionDamage(15);
    public final int magicOnslaughtPerUseScaling = calcAscensionDamage(5);

    public final int STATUS = calcAscensionSpecial(2);
    private int onslaughtTimesUsed;

    private Hokma hokma;
    public Shade shade;

    public enum PHASE{
        SHADES,
        NO_SHADES
    }

    public PHASE currentPhase = PHASE.SHADES;

    public Pluto() {
        this(150f, 0.0f);
    }
    public Pluto(final float x, final float y) {
        super(NAME, ID, 800, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pluto/Spriter/Pluto.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        addMove(SAFEGUARD, Intent.DEFEND_BUFF);
        addMove(MISSLE, Intent.ATTACK, magicMissleDamage, magicMissleHits, true);
        addMove(ONSLAUGHT, Intent.ATTACK, magicOnslaughtDamage);
        addMove(CONTRACT, Intent.UNKNOWN);
        addMove(BINDING_TERMS, Intent.DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction()
    {
        CustomDungeon.playTempMusicInstantly("Ensemble3");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Hokma) {
                hokma = (Hokma)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        Summon();
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case SAFEGUARD: {
                buffAnimation();
                for (AbstractMonster m : monsterList()) {
                    if (!(m instanceof AbstractAllyMonster)) {
                        block(m, magicSafeguardBlock);
                        atb(new ApplyPowerAction(m, m, new StrengthPower(m, magicSafeguardStr)));
                    }
                }
                resetIdle();
                break;
            }
            case MISSLE:
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            case ONSLAUGHT:
                bluntAnimation(target);
                dmg(target, info);
                resetIdle();
                onslaughtTimesUsed++;
                break;
            case CONTRACT:
                contractAnimation(target);
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
                resetIdle();
                break;
            case BINDING_TERMS:
                contractAnimation(target);
                intoDiscardMo(new VoidCard(), STATUS, this);
                resetIdle();
                break;
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                hokma.halfDead = false;
                this.isDone = true;
            }
        });
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, hokma);
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
                boolean shadeExists = false;
                for(AbstractMonster m: monsterList()){
                    if(m instanceof Shade){ shadeExists = true; }
                }
                if(!shadeExists && currentPhase.equals(PHASE.SHADES)){ currentPhase = PHASE.NO_SHADES; }
                isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int newDamage = moves.get(ONSLAUGHT).baseDamage += magicOnslaughtPerUseScaling * onslaughtTimesUsed;
                onslaughtTimesUsed = 0;
                addMove(ONSLAUGHT, Intent.ATTACK, newDamage);
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (currentPhase.equals(PHASE.NO_SHADES)) {
            setMoveShortcut(ONSLAUGHT, MOVES[ONSLAUGHT], getMoveCardFromByte(ONSLAUGHT));
        } else {
            if (firstMove) {
                setMoveShortcut(CONTRACT, MOVES[CONTRACT], getMoveCardFromByte(CONTRACT));
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(ONSLAUGHT) && !this.lastMoveBefore(ONSLAUGHT)) {
                    possibilities.add(ONSLAUGHT);
                }
                if (!this.lastMove(MISSLE)) {
                    possibilities.add(MISSLE);
                }
                if (!this.lastMove(BINDING_TERMS) && !this.lastMoveBefore(BINDING_TERMS)) {
                    possibilities.add(BINDING_TERMS);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SAFEGUARD, moveHistory) && !this.lastMoveBefore(SAFEGUARD, moveHistory)) {
            possibilities.add(SAFEGUARD);
        }
        if (!this.lastMove(ONSLAUGHT, moveHistory)) {
            possibilities.add(ONSLAUGHT);
        }
        if (!this.lastMove(MISSLE, moveHistory)) {
            possibilities.add(MISSLE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, getMoveCardFromByte(move));
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, hokma, hokma.allyIcon);
            }
        }
    }


    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new Safeguard(this));
        list.add(new Missile(this));
        list.add(new Onslaught(this));
        list.add(new Contract(this));
        list.add(new BindingTerms(this));
        return list.get(move);
    }

    public void Summon() {
        float xPos_Farthest_L = -750.0F;
        float xPos_Middle_L = -450F;
        float xPos_Short_L = -150F;
        float xPos_Shortest_L = 0F;
        Shade shade = new Shade(xPos_Short_L, 0.0f);
        this.shade = shade;
        atb(new SpawnMonsterAction(shade, true));
        atb(new UsePreBattleActionAction(shade));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Shade) {
                atb(new SuicideAction(mo));
            }
        }
        hokma.onBossDeath();
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Hit", "PlutoHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "PlutoVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "PlutoStab", enemy, this);
    }

    private void contractAnimation(AbstractCreature enemy) {
        animationAction("Contract", "PlutoContract", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "PlutoStrongStart", this);
    }

    private void buffAnimation() {
        animationAction("Block", "PlutoGuard", this);
    }

}