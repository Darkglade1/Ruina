package ruina.monsters.eventboss.clawVsKali;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.RuinaMod;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.eventboss.clawVsKali.clawCards.PlayerSerumR;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_Spear;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.theHead.Baral;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.PlayerClaw;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class ClawKali extends RedMist {
    public static final String ID = RuinaMod.makeID(ClawKali.class.getSimpleName());
    protected static final byte ONRUSH = 6;

    public final int focusSpiritBlock = calcAscensionTankiness(30);
    public final int focusSpiritStr = calcAscensionSpecial(3);

    public final int UPSTANDING_SLASH_DEBUFF = 1;

    public final int upstanding_damage = calcAscensionDamage(7);
    public final int upstanding_threshold = upstanding_damage;
    public final int level_damage = calcAscensionDamage(6);
    public final int level_threshold = level_damage;
    public final int level_strength = calcAscensionSpecial(2);

    public final int onrushBlock = calcAscensionTankiness(20);

    private final ArrayList<Byte> movepool = new ArrayList<>();
    private byte previewIntent = -1;

    private Baral claw;

    public int playerMaxHp;
    public int playerCurrentHp;
    public ArrayList<AbstractRelic> playerRelics = new ArrayList<>();
    public ArrayList<AbstractPotion> playerPotions = new ArrayList<>();
    public int playerEnergy;
    public int playerCardDraw;

    public static final String POWER_ID = makeID("Relentless");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ClawKali() {
        this(0.0f, 0.0f);
    }

    public ClawKali(final float x, final float y) {
        super(x, y);

        this.setHp(calcAscensionTankiness(300));
        numAdditionalMoves = 1;
        moves.clear();

        addMove(FOCUS_SPIRIT, Intent.DEFEND_BUFF);
        addMove(UPSTANDING_SLASH, Intent.ATTACK_DEBUFF, upstanding_damage, 2, true);
        addMove(LEVEL_SLASH, Intent.ATTACK_BUFF, level_damage, 2, true);
        addMove(SPEAR, Intent.ATTACK, calcAscensionDamage(5), 3, true);

        addMove(GSH, Intent.ATTACK_DEBUFF, calcAscensionDamage(30));
        addMove(ONRUSH, Intent.ATTACK_DEFEND, calcAscensionDamage(20));

        cardList.add(new ClawKali_FocusSpirit(this));
        ClawKali_UpstandingSlash card = new ClawKali_UpstandingSlash(this);
        if (AbstractDungeon.ascensionLevel >= 18) {
            card.uDesc();
        }
        cardList.add(card);
        cardList.add(new ClawKali_LevelSlash(this));
        cardList.add(new CHRBOSS_Spear());
        cardList.add(new CHRBOSS_Spear()); //filler card so the indices work out
        cardList.add(new ClawKali_GreaterSplitHorizontal(this));

        populateMovepool();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        adp().powers.clear();
                        applyToTargetTop(adp(), adp(), new PlayerClaw(adp()));
                        this.isDone = true;
                    }
                });
                this.isDone = true;
            }
        });

        claw = new Baral(-1100.0F, 0.0f);
        claw.drawX = AbstractDungeon.player.drawX;
        claw.setFlipAnimation(true, this);

        playerMaxHp = adp().maxHealth;
        playerCurrentHp = adp().currentHealth;
        adp().maxHealth = 100;
        adp().currentHealth = adp().maxHealth;
        adp().healthBarUpdatedEvent();
        playerRelics.addAll(adp().relics);
        adp().relics.clear();
        playerPotions.addAll(adp().potions);
        adp().potions.clear();
        playerEnergy = adp().energy.energy;
        playerCardDraw = adp().gameHandSize;
        adp().energy.energy = 5;
        EnergyPanel.totalCount = 5 - playerEnergy; //that way when the initial energy is added it adds up to 5
        adp().gameHandSize = 5;

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.drawPile.group.clear();
                AbstractDungeon.player.discardPile.group.clear();
                AbstractDungeon.player.exhaustPile.group.clear();
                AbstractDungeon.player.hand.group.clear();
                this.isDone = true;
            }
        });
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.drawPile.group.add(new PlayerSerumR(claw));
                AbstractDungeon.player.drawPile.group.add(new PlayerSerumR(claw));
                AbstractDungeon.player.drawPile.group.add(new PlayerSerumR(claw));
                AbstractDungeon.player.drawPile.group.add(new PlayerSerumR(claw));
                AbstractDungeon.player.drawPile.group.add(new PlayerSerumR(claw));
                AbstractDungeon.player.drawPile.shuffle();
                this.isDone = true;
            }
        });

        playSound("RedMistChange");
        phase = EGO_PHASE;
        runAnim("Idle" + phase);
        CustomDungeon.playTempMusicInstantly("RedMistBGM");

        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= 1) {
                    flashWithoutSound();
                    this.amount = 0;
                    if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                        takeTurn();
                    }
                } else {
                    flashWithoutSound();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        applyToTarget(this, this, new AbstractLambdaPower(BlackSilence1.KILLING_POWER_NAME, BlackSilence1.KILLING_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = BlackSilence1.KILLING_POWER_DESCRIPTIONS[0];
            }
        });
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
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        final int[] threshold = {0};
        switch (nextMove) {
            case FOCUS_SPIRIT: {
                blockAnimation();
                block(this, focusSpiritBlock);
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, focusSpiritStr)));
                applyToTarget(this, this, new NextTurnPowerPower(this, new LoseStrengthPower(this, focusSpiritStr)));
                resetIdle();
                break;
            }
            case UPSTANDING_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        upstandingAnimation(adp());
                    } else {
                        levelAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (threshold[0] >= upstanding_threshold) {
                            if (AbstractDungeon.ascensionLevel >= 18) {
                                applyToTargetTop(adp(), ClawKali.this, new VulnerablePower(adp(), UPSTANDING_SLASH_DEBUFF, false));
                            } else {
                                applyToTargetTop(adp(), ClawKali.this, new FrailPower(adp(), UPSTANDING_SLASH_DEBUFF, false));
                            }
                        }
                        isDone = true;
                    }
                });

                break;
            }
            case LEVEL_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        levelAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (threshold[0] >= level_threshold) {
                            applyToTargetTop(ClawKali.this, ClawKali.this, new StrengthPower(ClawKali.this, level_strength));
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case SPEAR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        spearAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case GSH: {
                horizontalSplitVfx();
                horizontalAnimation(adp());
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (adp().lastDamageTaken > 0) {
                            applyToTargetTop(adp(), ClawKali.this, new Bleed(adp(), adp().lastDamageTaken));
                        }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                break;
            }
            case ONRUSH: {
                levelAnimation(adp());
                dmg(adp(), info);
                block(this, onrushBlock);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                createIntent();
                System.out.println(enemyCard);
                for (AdditionalIntent intent : additionalIntents) {
                    System.out.println(intent.enemyCard);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        boolean rollAgain = false;
        if (previewIntent >= 0) {
            setMoveShortcut(previewIntent, MOVES[previewIntent], cardList.get(previewIntent).makeStatEquivalentCopy());
        } else {
            rollAgain = true;
        }
        if (movepool.isEmpty()) {
            populateMovepool();
        } else {
            previewIntent = movepool.remove(0);
        }
        setAdditionalMoveShortcut(previewIntent, moveHistory, cardList.get(previewIntent).makeStatEquivalentCopy());
        for (AdditionalIntent additionalIntent : additionalIntents) {
            additionalIntent.transparent = true;
            additionalIntent.usePrimaryIntentsColor = false;
        }
        //if previewIntent wasn't a valid intent, roll again (should only happen at the start of combat)
        if (rollAgain) {
            rollMove();
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
    }


    private void populateMovepool() {
        movepool.add(FOCUS_SPIRIT);
        movepool.add(UPSTANDING_SLASH);
        movepool.add(LEVEL_SLASH);
        movepool.add(SPEAR);
        movepool.add(GSH);
        Collections.shuffle(movepool, AbstractDungeon.monsterRng.random);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (claw != null) {
            claw.render(sb);
        }
    }

}