package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.DaggerSpray;
import com.megacrit.cardcrawl.cards.green.Terror;
import com.megacrit.cardcrawl.cards.red.Bludgeon;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import com.megacrit.cardcrawl.vfx.combat.BloodShotEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartBuffEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.ViceCrushEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AspirationEffectAction;
import ruina.actions.Day49PhaseTransition1Action;
import ruina.actions.Day49PhaseTransition2Action;
import ruina.actions.MakeTempCardInDrawPileActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.day49.angelaCards.aspiration.*;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.AspirationHeartEffect;
import ruina.vfx.ThirstEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.adp;

public class Act2Angela extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Act2Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte INCLINATION = 0;
    private static final byte ASPIRATION_PNEUMONIA = 1;
    private static final byte TURBULENT_BEATS = 2;
    private static final byte PULSATION = 3;
    private static final byte PHASE_TRANSITION = 4;

    public final int palpitationBeatOfDeath = 2;

    public final int pneumoniaCardLimit = 18;

    public final int inclinationVuln = 2;
    public final int inclinationWeak = 2;
    public final int inclinationFrail = 2;
    public final int inclinationCards = 5;

    public int pulsationBuffCount = 0;
    public final int pulsationStrength = 2;

    public final int pulsationPneumoniaDecrease = 1;

    public final int pulsationFirstBuffPneumonia = 17;
    public final int pulsationSecondBuffPneumonia = 16;
    public final int pulsationThirdBuffPneumonia = 15;
    public final int pulsationFourthBuffStrength = 10;
    public final int pulsationFifthBuffStrength = 50;

    public final int aspirationPneumoniaDamage = 75;

    public final int turbulentBeatsDamage = 5;
    public final int turbulentBeatsHits = 15;

    private static final int HP = 3500;

    private final ArrayList<Byte> movepool = new ArrayList<>();
    private byte previewIntent = -1;

    public Act2Angela() {
        this(0.0f, 0.0f);
    }

    public Act2Angela(final float x, final float y) {
        super(NAME, ID, 1000, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence1/Spriter/BlackSilence1.scml"));
        animation.setFlip(true, false);
        this.setHp(HP);
        this.type = EnemyType.BOSS;
        firstMove = true;
        addMove(INCLINATION, Intent.STRONG_DEBUFF);
        addMove(PULSATION, Intent.BUFF);
        addMove(ASPIRATION_PNEUMONIA, Intent.ATTACK, aspirationPneumoniaDamage);
        addMove(TURBULENT_BEATS, Intent.ATTACK, turbulentBeatsDamage, turbulentBeatsHits, true);
        addMove(PHASE_TRANSITION, Intent.UNKNOWN);
        populateCards();
        populateMovepool();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
        CustomDungeon.playTempMusicInstantly("Story5");
        PlayerAngela playerAngela = new PlayerAngela(adp());
        AbstractDungeon.player.powers.add(playerAngela);
        AbstractDungeon.player.powers.add(new Memoir(adp()));
        AbstractDungeon.player.powers.add(new InvisibleBarricadePower(adp()));
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        atb(new AspirationEffectAction());
        playerAngela.atStartOfTurnPostDraw();
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));
        atb(new ApplyPowerAction(this, this, new Palpitation(this, palpitationBeatOfDeath)));
        atb(new ApplyPowerAction(this, this, new Pneumonia(this, pneumoniaCardLimit)));
        atb(new ApplyPowerAction(this, this, new DamageReductionInvincible(this, HP / 5)));
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        AbstractCreature target = adp();
        if(info.base > -1) { info.applyPowers(this, adp()); }
        switch (this.nextMove) {
            case INCLINATION:
                // change this
                atb(new VFXAction(new HeartMegaDebuffEffect()));
                atb(new ApplyPowerAction(adp(), this, new WeakPower(adp(), inclinationWeak, true)));
                atb(new ApplyPowerAction(adp(), this, new VulnerablePower(adp(), inclinationVuln, true)));
                atb(new ApplyPowerAction(adp(), this, new FrailPower(adp(), inclinationFrail, true)));
                float renderDistance = 0.2f;
                for(int i = 0; i < inclinationCards; i += 1){
                    atb(new MakeTempCardInDrawPileActionButItCanFizzle(new Heartbeat(), 1, true, false, false, Settings.WIDTH * renderDistance, Settings.HEIGHT / 2.0F, this));
                    renderDistance += 0.15f;
                }
                break;
            case PULSATION: {
                // change this
                atb(new VFXAction(new BorderFlashEffect(new Color(0.8f, 0.5f, 1.0f, 1.0f))));
                atb(new VFXAction(new HeartBuffEffect(this.hb.cX, this.hb.cY)));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        int minusStrength = 0;
                        AbstractPower strength = Act2Angela.this.getPower(StrengthPower.POWER_ID);
                        if(strength != null && strength.amount < 0){
                            minusStrength = -strength.amount;
                        }
                        att(new ApplyPowerAction(Act2Angela.this, Act2Angela.this, new StrengthPower(Act2Angela.this, pulsationStrength + minusStrength)));
                        isDone = true;
                    }
                });
                switch (pulsationBuffCount){
                    case 0:
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractPower pneumonia = Act2Angela.this.getPower(Pneumonia.POWER_ID);
                                if(pneumonia != null){ ((Pneumonia) pneumonia).changeThreshold(pulsationFirstBuffPneumonia); }
                                isDone = true;
                            }
                        });
                        break;
                    case 1:
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractPower pneumonia = Act2Angela.this.getPower(Pneumonia.POWER_ID);
                                if(pneumonia != null){ ((Pneumonia) pneumonia).changeThreshold(pulsationSecondBuffPneumonia); }
                                isDone = true;
                            }
                        });
                        break;
                    case 2:
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractPower pneumonia = Act2Angela.this.getPower(Pneumonia.POWER_ID);
                                if(pneumonia != null){ ((Pneumonia) pneumonia).changeThreshold(pulsationThirdBuffPneumonia); }
                                isDone = true;
                            }
                        });
                        break;
                    case 3:
                        atb(new ApplyPowerAction(this, this, new StrengthPower(Act2Angela.this, pulsationFourthBuffStrength)));
                        break;
                    default:
                        atb(new ApplyPowerAction(this, this, new StrengthPower(Act2Angela.this, pulsationFifthBuffStrength)));
                        break;
                }
                pulsationBuffCount += 1;
                break;
            }
            case TURBULENT_BEATS: {
                atb(new VFXAction(new BloodShotEffect(hb.cX, hb.cY, adp().hb.cX, adp().hb.cY, multiplier), 0.6F));
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                break;
            }
            case ASPIRATION_PNEUMONIA: {
                atb(new AspirationEffectAction());
                ViceCrushEffect ViceCrush = new ViceCrushEffect(adp().hb.cX, adp().hb.cY);
                ReflectionHacks.setPrivate(ViceCrush, ViceCrushEffect.class, "color", Color.RED.cpy());
                atb(new VFXAction(ViceCrush, 0.5F));
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case PHASE_TRANSITION:
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.player.onVictory();
                        isDone = true;
                    }
                });
                atb(new Day49PhaseTransition2Action(0, 1));
                break;
        }
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                createIntent();
                this.isDone = true;
            }
        });
    }

    @Override
    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.25f, enemy);
    }

    private void moveAnimation(float x, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (enemy == null || !enemy.isDeadOrEscaped()) {
                    drawX = x;
                }
                this.isDone = true;
            }
        });
    }

    private void setFlipAnimation(boolean flipHorizontal, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (enemy == null || !enemy.isDeadOrEscaped()) {
                    animation.setFlip(flipHorizontal, false);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if(halfDead){ setMoveShortcut(PHASE_TRANSITION); }
        else {
            boolean rollAgain = false;
            if (previewIntent >= 0) {
                if(previewIntent == PULSATION){
                    AbstractCard c = new Pulsation(this);
                    setMoveShortcut(previewIntent, MOVES[previewIntent], c);
                }
                else { setMoveShortcut(previewIntent, MOVES[previewIntent], cardList.get(previewIntent)); }
            } else {
                rollAgain = true;
            }
            if (movepool.isEmpty()) {
                previewIntent = PULSATION;
                populateMovepool();
            } else {
                previewIntent = movepool.remove(0);
            }
            if(previewIntent == PULSATION){
                AbstractCard c = new Pulsation(this);
                setAdditionalMoveShortcut(previewIntent, moveHistory, c);
            }
            else { setAdditionalMoveShortcut(previewIntent, moveHistory, cardList.get(previewIntent)); }
            for (AdditionalIntent additionalIntent : additionalIntents) {
                additionalIntent.transparent = true;
                additionalIntent.usePrimaryIntentsColor = false;
            }
            //if previewIntent wasn't a valid intent, roll again (should only happen at the start of combat)
            if (rollAgain) {
                rollMove();
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

    private void populateMovepool() {
        if(firstMove){
            movepool.add(INCLINATION);
            firstMove = false;
        }
        ArrayList<Byte> shuffleMovePool = new ArrayList<>();
        shuffleMovePool.add(TURBULENT_BEATS);
        shuffleMovePool.add(ASPIRATION_PNEUMONIA);
        Collections.shuffle(shuffleMovePool, AbstractDungeon.monsterRng.random);
        for(Byte move : shuffleMovePool){ movepool.add(move); }
    }

    private void populateCards() {
        cardList.add(new Inclination(this));
        cardList.add(new AspirationPneumonia(this));
        cardList.add(new TurbulentBeats(this));
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            cardsToRender.clear();
            AbstractCardMonster.hoveredCard = null;
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            att(new ClearCardQueueAction());
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof Refracting)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            cardsToRender.clear();
            setMove(PHASE_TRANSITION, Intent.UNKNOWN);
            additionalIntents.clear();
            additionalMoves.clear();
            ArrayList<AbstractCard> cards = cardsToRender;
            if (cards.size() > 1) {
                cards.remove(cards.size() - 1);
            }
            createIntent();
        }
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    public void dieBypass() {
        super.die(false);
    }
}
