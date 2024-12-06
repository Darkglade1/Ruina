package ruina.monsters.uninvitedGuests.normal.elena;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Chain;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Fairy;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Pillar;
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Binah extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Binah.class.getSimpleName());

    protected static final byte DEGRADED_PILLAR = 0;
    protected static final byte DEGRADED_CHAIN = 1;
    protected static final byte DEGRADED_FAIRY = 2;

    public final int DEBUFF = 1;
    public final int BLOCK = 14;
    public final int fairyHits = 2;
    public final int PILLAR_FAIRY = 1;

    public Elena elena;
    public VermilionCross vermilionCross;

    public static final String POWER_ID = makeID("Arbiter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Binah() {
        this(0.0f, 0.0f);
    }

    public Binah(final float x, final float y) {
        super(ID, ID, 120, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Binah/Spriter/Binah.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(120));

        addMove(DEGRADED_PILLAR, Intent.ATTACK_DEFEND, 24);
        addMove(DEGRADED_CHAIN, Intent.ATTACK_DEBUFF, 21);
        addMove(DEGRADED_FAIRY, Intent.ATTACK, 14, fairyHits);

        cardList.add(new Pillar(this));
        cardList.add(new Chain(this));
        cardList.add(new Fairy(this));

        this.icon = TexLoader.getTexture(makeUIPath("BinahIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Elena) {
                elena = (Elena)mo;
                target = elena;
            }
            if (mo instanceof VermilionCross) {
                vermilionCross = (VermilionCross)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                AbstractCreature enemy = target;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (!enemy.hasPower(StunMonsterPower.POWER_ID)) {
                            if (enemy instanceof AbstractMultiIntentMonster) {
                                ((AbstractMultiIntentMonster) enemy).additionalIntents.clear();
                                ((AbstractMultiIntentMonster) enemy).additionalMoves.clear();
                            }
                            if (enemy instanceof AbstractCardMonster) {
                                ArrayList<AbstractCard> cards = ((AbstractCardMonster) enemy).cardsToRender;
                                if (cards.size() > 1) {
                                    cards.remove(cards.size() - 1);
                                }
                            }
                        }
                        this.isDone = true;
                    }
                });
            }

            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                AbstractCreature newTarget = action.target;
                if (newTarget instanceof AbstractRuinaMonster && !newTarget.isDeadOrEscaped()) {
                    target = (AbstractRuinaMonster) action.target;
                    AbstractDungeon.onModifyPower();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        super.usePreBattleAction();
    }

    public void dialogue() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
    }

    @Override
    public void takeTurn() {
        dialogue();
        super.takeTurn();
        switch (this.nextMove) {
            case DEGRADED_PILLAR: {
                specialAnimation(target);
                atb(new AllyGainBlockAction(this, this, BLOCK));
                waitAnimation(0.25f);
                pillarEffect(target);
                dmg(target, info);
                if (cardList.get(DEGRADED_PILLAR).upgraded) {
                    applyToTarget(target, this, new ruina.powers.act5.Fairy(target, PILLAR_FAIRY));
                }
                resetIdle();
                break;
            }
            case DEGRADED_CHAIN: {
                bluntAnimation(target);
                dmg(target, info);
                chainsEffect(target);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, false));
                if (cardList.get(DEGRADED_CHAIN).upgraded) {
                    applyToTarget(target, this, new VulnerablePower(target, DEBUFF, false));
                }
                resetIdle();
                break;
            }
            case DEGRADED_FAIRY: {
                bluntAnimation(target);
                dmg(target, info);
                resetIdle(0.5f);
                slashAnimation(target);
                dmg(target, info);
                resetIdle(0.5f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        byte move;
        if (this.lastMove(DEGRADED_PILLAR)) {
            move = DEGRADED_CHAIN;
        } else if (this.lastMove(DEGRADED_CHAIN)) {
            move = DEGRADED_FAIRY;
        } else {
            move = DEGRADED_PILLAR;
        }
        setMoveShortcut(move, cardList.get(move));
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "BinahStoneReady", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BinahFairy", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BinahChain", enemy, this);
    }

    private void chainsEffect(AbstractCreature target) {
        String CHAINS = RuinaMod.makeMonsterPath("Binah/Spriter/fetter2.png");
        Texture CHAINS_TEXTURE = TexLoader.getTexture(CHAINS);
        float duration = 0.6f;
        AbstractGameEffect appear = new VfxBuilder(CHAINS_TEXTURE, target.hb.cX, target.hb.cY, duration)
                .fadeOut(duration)
                .build();
        atb(new VFXAction(appear, duration));
    }

    private void pillarEffect(AbstractCreature target) {
        String pillar = RuinaMod.makeMonsterPath("Binah/Spriter/stigma.png");
        Texture pillarTexture = TexLoader.getTexture(pillar);
        float duration = 0.8f;
        AbstractGameEffect appear = new VfxBuilder(pillarTexture, this.hb.cX, target.hb.cY, duration)
                .moveX(this.hb.cX, target.hb.cX + (200.0f * Settings.scale), VfxBuilder.Interpolations.EXP5)
                .playSoundAt(0.1f, makeID("BinahStoneFire"))
                .build();
        atb(new VFXAction(appear, duration));
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        renderTargetIcon(sb);
    }

}