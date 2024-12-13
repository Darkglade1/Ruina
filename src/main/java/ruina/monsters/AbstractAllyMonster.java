package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.TransferBlockToAllyAction;
import ruina.powers.InvisibleAllyBarricadePower;
import ruina.powers.MultiplayerAllyBuff;
import ruina.util.AllyMove;
import spireTogether.gamemodes.TiSGameModeSettings;
import spireTogether.networkcore.P2P.P2PManager;

import java.util.ArrayList;
import java.util.Optional;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public abstract class AbstractAllyMonster extends AbstractRuinaMonster {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;
    public boolean massAttackHitsPlayer = false;

    //basically just for little Red who is an ally that can become an enemy
    public boolean isAlly = true;

    //essentially lets me have allies that the player can still target with cards
    public boolean isTargetableByPlayer = false;

    public ArrayList<AllyMove> allyMoves = new ArrayList<>();
    private static final int BLOCK_TRANSFER = 5;

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        AllyMove blockMove = new AllyMove(TEXT[11], this, new Texture(makeUIPath("defend.png")), TEXT[9] + BLOCK_TRANSFER + TEXT[10], () -> {
            atb(new TransferBlockToAllyAction(BLOCK_TRANSFER, this));
        });
        blockMove.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        blockMove.setY(this.intentHb.cY - (32.0f * Settings.scale));
        allyMoves.add(blockMove);
        applyToTarget(this, this, new InvisibleAllyBarricadePower(this));
        if (RuinaMod.isMultiplayerConnected()) {
            applyToTarget(this, this, new MultiplayerAllyBuff(this));
        }
        if (isAlly && !isTargetableByPlayer) {
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
    public void takeTurn() {
        super.takeTurn();
        if (this.isDead) {
            return;
        }
        if (isAlly && !isTargetableByPlayer) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = false;
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || !isAlly) {
            super.applyPowers();
            return;
        }
        if (target != null) {
            applyPowers(target);
        }
    }

    public void applyPowers(AbstractCreature target) {
        float additionalMultiplier = getAdditionalMultiplier();
        if (this.nextMove >= 0) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            if (target != adp()) {
                if(info.base > -1) {
                    Color color = new Color(0.0F, 1.0F, 0.0F, 0.5F);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                    if (this.intent == IntentEnums.MASS_ATTACK) {
                        if (massAttackHitsPlayer) {
                            info.applyPowers(this, adp());
                            if (additionalMultiplier > 0) {
                                info.output = (int)(info.output * additionalMultiplier);
                            }
                            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                            PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                            if (moves.get(this.nextMove).multiplier > 0) {
                                intentTip.body = TEXT[13] + info.output + TEXT[14] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[16];
                            } else {
                                intentTip.body = TEXT[13] + info.output + TEXT[14] + TEXT[15];
                            }
                        } else {
                            info.applyPowers(this, target);
                            if (additionalMultiplier > 0) {
                                info.output = (int)(info.output * additionalMultiplier);
                            }
                            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                            PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                            if (moves.get(this.nextMove).multiplier > 0) {
                                intentTip.body = TEXT[13] + info.output + TEXT[17] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[16];
                            } else {
                                intentTip.body = TEXT[13] + info.output + TEXT[17] + TEXT[15];
                            }
                        }
                    } else {
                        info.applyPowers(this, target);
                        if (additionalMultiplier > 0) {
                            info.output = (int)(info.output * additionalMultiplier);
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                        PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                        Texture attackImg;
                        if (moves.get(this.nextMove).multiplier > 0) {
                            intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + moves.get(this.nextMove).multiplier + TEXT[4];
                            attackImg = getAttackIntent(info.output * moves.get(this.nextMove).multiplier);
                        } else {
                            intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                            attackImg = getAttackIntent(info.output);
                        }
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
                    }
                } else {
                    Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
                    ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                    PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                    if (this.intent == Intent.DEBUFF || this.intent == Intent.STRONG_DEBUFF) {
                        intentTip.body = TEXT[5] + FontHelper.colorString(target.name, "y") + TEXT[6];
                    }
                    if (this.intent == Intent.BUFF || this.intent == Intent.DEFEND_BUFF) {
                        intentTip.body = TEXT[7];
                    }
                    if (this.intent == Intent.DEFEND || this.intent == Intent.DEFEND_DEBUFF) {
                        intentTip.body = TEXT[8];
                    }
                }
            } else {
                Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                super.applyPowers();
            }
        }
    }

    protected float getAdditionalMultiplier() {
        return -1;
    }

    @Override
    public void damage(DamageInfo info) {
        //failsafe to stop player from damaging allies
        if (isAlly && !isTargetableByPlayer && (info.owner == adp() || info.owner == null)) {
            return;
        }
        super.damage(info);
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        if (!isAlly || isTargetableByPlayer) {
            super.renderReticle(sb);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (isAlly && !isDead && !isDying) {
            for (AllyMove allyMove : allyMoves) {
                allyMove.render(sb);
            }
        }
        super.render(sb);
    }

    public void update() {
        super.update();
        if (isAlly && !isDead && !isDying) {
            for (AllyMove allyMove : allyMoves) {
                allyMove.update();
            }
        }
    }

    public void disappear() {
        hideHealthBar();
        this.currentHealth = 0;
        this.loseBlock();
        this.isDead = true;
        this.isDying = true;
        this.healthBarUpdatedEvent();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(false);
    }

    public void setAnimationFlip(boolean horizontal, boolean vertical) {
        animation.setFlip(horizontal, vertical);
    }

    protected void renderTargetIcon(SpriteBatch sb) {
        if (target != null) {
            Color white = Color.WHITE.cpy();
            white.a = sb.getColor().a;
            sb.setColor(white);
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(target.icon, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }
}