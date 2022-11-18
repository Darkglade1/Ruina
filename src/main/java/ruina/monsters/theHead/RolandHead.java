package ruina.monsters.theHead;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.BlackSilence;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class RolandHead extends Roland {

    private boolean usedPreBattleAction = false;
    private int CARDS_PER_TURN = 6;

    public static final String POWER_ID = makeID("Orlando");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RolandHead() {
        this(0.0f, 0.0f);
    }

    public RolandHead(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
            applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
                @Override
                public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                    this.amount++;
                    if (this.amount >= CARDS_PER_TURN) {
                        this.amount = 0;
                        takeTurn();
                        AbstractPower power = owner.getPower(BlackSilence.POWER_ID);
                        if (power != null) {
                            power.atEndOfTurn(false);
                        }
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                createIntent();
                                this.isDone = true;
                            }
                        });
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                halfDead = true;
                                this.isDone = true;
                            }
                        });
                    }
                }

                @Override
                public void updateDescription() {
                    description = POWER_DESCRIPTIONS[0] + CARDS_PER_TURN + POWER_DESCRIPTIONS[1];
                }
            });
            super.usePreBattleAction();
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Baral) { enemyBoss = mo; }
            }
        }
    }

    public void dialogue() {
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[2]));
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

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        Texture targetTexture = null;
        if (enemyBoss instanceof Baral) {
            targetTexture = Baral.targetTexture;
        } else if (enemyBoss instanceof Zena) {
            targetTexture = Zena.targetTexture;
        }
        if (targetTexture != null) {
            sb.setColor(Color.WHITE.cpy());
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

}
