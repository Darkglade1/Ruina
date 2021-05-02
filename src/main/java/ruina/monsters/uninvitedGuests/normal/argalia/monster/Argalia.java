package ruina.monsters.uninvitedGuests.normal.argalia.monster;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.uninvitedGuests.normal.argalia.cards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.BlueReverberation;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.NextTurnPowerPower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Argalia extends AbstractDeckMonster
{
    public static final String ID = makeID(Argalia.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final byte LARGO = 0;
    public static final byte ALLEGRO = 1;
    public static final byte SCYTHE = 2;
    public static final byte TRAILS = 3;
    public static final byte DANZA = 4;

    public final int largoBlock = calcAscensionTankiness(30);
    public final int largoDamage = calcAscensionDamage(15);

    public final int allegroDamage = calcAscensionDamage(10);
    public final int allegroHits = 2;
    public final int allegroStrength = calcAscensionSpecial(3);

    public final int scytheDamage = calcAscensionDamage(30);
    public static final float scytheDamageMultiplier = 3.0f;
    public static final int SCYTHE_INTENT_NUM = 0;

    public final int trailsDamage = calcAscensionDamage(26);
    public final int trailsStrengthLoss = calcAscensionSpecial(3);

    public final int tempestuousDamage = calcAscensionDamage(6);
    public final int tempestuousHits = 5;

    private static final int DANZA_COOLDOWN = 9;
    private boolean queueDanza = false;
    private int danzaTimer = 0;

    private static final int INTENT_SHIFT_AMT = 1;

    public Roland roland;
    private InvisibleBarricadePower power = new InvisibleBarricadePower(this);

    public static final String POWER_ID = makeID("Vibration");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String RESONANCE_POWER_ID = makeID("Resonance");
    public static final PowerStrings ResonancepowerStrings = CardCrawlGame.languagePack.getPowerStrings(RESONANCE_POWER_ID);
    public static final String RESONANCE_POWER_NAME = ResonancepowerStrings.NAME;
    public static final String[] RESONANCE_POWER_DESCRIPTIONS = ResonancepowerStrings.DESCRIPTIONS;

    public Argalia() { this(0.0f, 0.0f); }

    public Argalia(final float x, final float y) {
        super(NAME, ID, 1500, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Argalia/Spriter/Argalia.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        maxAdditionalMoves = 2;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = 2;

        addMove(LARGO, Intent.ATTACK_DEFEND, largoDamage);
        addMove(ALLEGRO, Intent.ATTACK_BUFF, allegroDamage, allegroHits, true);
        addMove(SCYTHE, Intent.ATTACK, scytheDamage);
        addMove(TRAILS, Intent.ATTACK_DEBUFF, trailsDamage);
        addMove(DANZA, IntentEnums.MASS_ATTACK, tempestuousDamage, tempestuousHits, true);
        initializeDeck();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction()
    {
        applyToTarget(this, this, power);
        applyToTarget(this, this, new AbstractLambdaPower(RESONANCE_POWER_NAME, RESONANCE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, INTENT_SHIFT_AMT) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                shiftIntents(amount);
            }

            @Override
            public void updateDescription() {
                description = RESONANCE_POWER_DESCRIPTIONS[0] + amount + RESONANCE_POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new BlueReverberation(this, 2));
        applyToTarget(this, this, new StrengthPower(this, 1)); //this 1 strength is a hack to make the powers line up properly LOL
        CustomDungeon.playTempMusicInstantly("EnsembleArgalia");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Roland) { roland = (Roland) mo; }
        }

    }

    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature enemy, int whichMove) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, enemy); }
        int[] damageArray;
        switch (move.nextMove) {
            case LARGO: {
                slashLeftAnimation(enemy);
                block(this, largoBlock);
                dmg(enemy, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPower vibration = enemy.getPower(POWER_ID);
                        if (vibration != null) {
                            vibration.flash();
                            att(new RemoveSpecificPowerAction(enemy, Argalia.this, POWER_ID));
                            att(new GainBlockAction(Argalia.this, largoBlock));
                        } else {
                            applyVibration(enemy);
                        }
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            }
            case ALLEGRO: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashUpAnimation(enemy);
                    } else {
                        slashDownAnimation(enemy);
                    }
                    dmg(enemy, info);
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPower vibration = enemy.getPower(POWER_ID);
                        if (vibration != null) {
                            vibration.flash();
                            att(new RemoveSpecificPowerAction(enemy, Argalia.this, POWER_ID));
                            att(new ApplyPowerAction(Argalia.this, Argalia.this, new NextTurnPowerPower(Argalia.this, new StrengthPower(Argalia.this, allegroStrength))));
                        } else {
                            applyVibration(enemy);
                        }
                        this.isDone = true;
                    }
                });
                break;
            }
            case SCYTHE: {
                slamAnimation(enemy);
                if (whichMove == SCYTHE_INTENT_NUM) {
                    info.output = (int)(info.output * scytheDamageMultiplier);
                }
                dmg(enemy, info);
                resetIdle(1.0f);
                break;
            }
            case TRAILS:
                slashUpAnimation2(enemy);
                dmg(enemy, info);
                resetIdle(1.0f);
                applyToTarget(enemy, this, new StrengthPower(enemy, -trailsStrengthLoss));
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
                    if (i == 0) {
                        slashLeftAnimation(adp());
                    } else if (i == 1) {
                        slashRightAnimation(adp());
                    } else if (i == 2) {
                        slashDownAnimation(adp());
                    } else if (i == 3) {
                        slashUpAnimation(adp());
                    } else {
                        slamAnimation(adp());
                    }
                    atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    resetIdle(1.0f);
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        queueDanza = false;
                        isDone = true;
                    }
                });
                break;
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (firstMove) {
                    att(new TalkAction(Argalia.this, DIALOG[0]));
                    firstMove = false;
                }
                isDone = true;
            }
        });
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp(), -1);
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (roland.isDead || roland.isDying) { takeCustomTurn(additionalMove, adp(), i);
            } else { takeCustomTurn(additionalMove, roland, i); }
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
    public void getAdditionalMoves(int num, int whichMove) {
        if(!queueDanza){ createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, roland, roland.allyIcon, i);
            }
        }
    }

    @Override
    protected void createDeck() {
        masterDeck.addToBottom(new CHRBOSS_Largo(this));
        masterDeck.addToBottom(new CHRBOSS_Largo(this));
        masterDeck.addToBottom(new CHRBOSS_Allegro(this));
        masterDeck.addToBottom(new CHRBOSS_Allegro(this));
        masterDeck.addToBottom(new CHRBOSS_TrailsOfBlue(this));
        masterDeck.addToBottom(new CHRBOSS_ResonantScythe(this));
    }

    protected void createMoveFromCard(AbstractCard c) {
        if (c.cardID.equals(CHRBOSS_Largo.ID)) { setMoveShortcut(LARGO, MOVES[LARGO], c);
        } else if (c.cardID.equals(CHRBOSS_Allegro.ID)) { setMoveShortcut(ALLEGRO, MOVES[ALLEGRO], c);
        } else if (c.cardID.equals(CHRBOSS_ResonantScythe.ID)) { setMoveShortcut(SCYTHE, MOVES[SCYTHE], c);
        } else if (c.cardID.equals(CHRBOSS_TrailsOfBlue.ID)) { setMoveShortcut(TRAILS, MOVES[TRAILS], c);
        } else if (c.cardID.equals(CHRBOSS_TempestuousDanza.ID)) { setMoveShortcut(DANZA, MOVES[DANZA], c);
        } else { setMoveShortcut(ALLEGRO, MOVES[ALLEGRO], c); }
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

    public void danzaCheck(){
        att(new AbstractGameAction() {
            @Override
            public void update() {
                danzaTimer += 1;
                if(danzaTimer % DANZA_COOLDOWN == 0){ queueDanza = true; }
                isDone = true;
            }
        });
    }

    private void shiftIntents(int times) {
        if (this.hasPower(StunMonsterPower.POWER_ID)) {
            return;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<Byte> moves = new ArrayList<>();
                ArrayList<AbstractCard> cards = new ArrayList<>();
                moves.add(nextMove);
                cards.add(enemyCard);
                for (EnemyMoveInfo additionalMove : additionalMoves) {
                    moves.add(additionalMove.nextMove);
                }
                for (AdditionalIntent additionalIntent : additionalIntents) {
                    cards.add(additionalIntent.enemyCard);
                }
                if (moves.size() > 1) {
                    for (int i = 0; i < times; i++) {
                        Byte move = moves.remove(moves.size() - 1);
                        moves.add(0, move);
                        AbstractCard card = cards.remove(cards.size() - 1);
                        cards.add(0, card);
                    }
                    additionalMoves.clear();
                    additionalIntents.clear();
                    setMoveShortcut(moves.get(0), MOVES[moves.get(0)], cards.get(0));
                    for (int i = 1; i < moves.size(); i++) {
                        setAdditionalMoveShortcut(moves.get(i), additionalMovesHistory.get(i - 1), cards.get(i));
                    }
                    createIntent();
                    AbstractDungeon.onModifyPower();
                }
                this.isDone = true;
            }
        });
    }

    private void applyVibration(AbstractCreature target) {
        applyToTargetTop(target, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.DEBUFF, false, target, 1) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
            reverbClear = true;
            saveConfig();
            roland.onArgaliaDeath();
            this.onBossVictoryLogic();
            this.onFinalBossVictoryLogic();
        }
    }

    private void slashLeftAnimation(AbstractCreature enemy) {
        animationAction("SlashLeft", "ArgaliaAtk", enemy, this);
    }

    private void slashRightAnimation(AbstractCreature enemy) {
        animationAction("SlashRight", "ArgaliaAtk", enemy, this);
    }

    private void slashUpAnimation(AbstractCreature enemy) {
        animationAction("SlashUp", "ArgaliaStrongAtk1", enemy, this);
    }

    private void slashDownAnimation(AbstractCreature enemy) {
        animationAction("SlashDown", "ArgaliaStrongAtk2", enemy, this);
    }

    private void slamAnimation(AbstractCreature enemy) {
        animationAction("Slam", "ArgaliaFarAtk1", enemy, this);
    }

    private void slashUpAnimation2(AbstractCreature enemy) {
        animationAction("SlashUp", "ArgaliaFarAtk2", enemy, this);
    }

}