package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;

public abstract class AbstractCardMonster extends AbstractMultiIntentMonster {
    protected AbstractCard enemyCard = null;
    public ArrayList<AbstractCard> cardsToRender = new ArrayList<>();
    public static AbstractCard hoveredCard = null;

    public AbstractCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void update() {
        super.update();
        if (enemyCard != null && !this.isDeadOrEscaped() && !hasPower(StunMonsterPower.POWER_ID)) {
            enemyCard.hb.update();
            if (Settings.FAST_MODE) {
                enemyCard.current_x = MathHelper.cardLerpSnap(enemyCard.current_x, enemyCard.target_x);
                enemyCard.current_y = MathHelper.cardLerpSnap(enemyCard.current_y, enemyCard.target_y);
            }

            enemyCard.current_x = MathHelper.cardLerpSnap(enemyCard.current_x, enemyCard.target_x);
            enemyCard.current_y = MathHelper.cardLerpSnap(enemyCard.current_y, enemyCard.target_y);
            enemyCard.hb.move(enemyCard.current_x, enemyCard.current_y);
            enemyCard.hb.resize(512 * enemyCard.drawScale, 512 * enemyCard.drawScale);
            if (enemyCard.hb.hovered) {
                if (AbstractCardMonster.hoveredCard == null) {
                    AbstractCardMonster.hoveredCard = enemyCard;
                }
            } else {
                if (AbstractCardMonster.hoveredCard == enemyCard) {
                    AbstractCardMonster.hoveredCard = null;
                }
            }
            if (AbstractCardMonster.hoveredCard == enemyCard) {
                enemyCard.drawScale = MathHelper.cardScaleLerpSnap(enemyCard.drawScale, enemyCard.targetDrawScale * 3.0F);
                enemyCard.drawScale = MathHelper.cardScaleLerpSnap(enemyCard.drawScale, enemyCard.targetDrawScale * 3.0F);
            } else {
                enemyCard.drawScale = MathHelper.cardScaleLerpSnap(enemyCard.drawScale, enemyCard.targetDrawScale);
            }
        }
    }

    public void renderCard(SpriteBatch sb) {
        Color color = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentColor");
        if (color.a > 0 && !hasPower(StunMonsterPower.POWER_ID)) {
            sb.setColor(color);
            for (AbstractCard card : cardsToRender) {
                if (card != hoveredCard) {
                    card.render(sb);
                }
            }
            if (hoveredCard != null) {
                hoveredCard.render(sb);
            }
        }
    }

    @Override
    protected void renderDamageRange(SpriteBatch sb) {
        super.renderDamageRange(sb);
        renderCard(sb);
    }

    public void setEnemyCard(AbstractCard card, int baseDamage) {
        enemyCard = card;
        if (enemyCard != null) {
            enemyCard.drawScale = 0.25f;
            enemyCard.targetDrawScale = 0.25f;
            enemyCard.baseDamage = baseDamage;
            enemyCard.current_x = this.intentHb.cX;
            enemyCard.target_x = this.intentHb.cX;
            enemyCard.current_y = this.intentHb.cY + (100.0f * Settings.scale);
            enemyCard.target_y = this.intentHb.cY + (100.0f * Settings.scale);
        }
    }

    @Override
    public void rollMove() {
        cardsToRender.clear();
        super.rollMove();
        AbstractCardMonster.hoveredCard = null; //in case the player was hovering one of them while it was getting yeeted
    }

    public void setMoveShortcut(byte next, String text, AbstractCard enemyCard) {
        EnemyMoveInfo info = this.moves.get(next);
        this.setMove(text, next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
        setEnemyCard(enemyCard, info.baseDamage);
        cardsToRender.add(enemyCard);
    }

    public void setAdditionalMoveShortcut(byte next, ArrayList<Byte> moveHistory, AbstractCard enemyCard) {
        EnemyMoveInfo info = this.moves.get(next);
        AdditionalIntent additionalIntent = new AdditionalIntent(this, info, enemyCard);
        additionalIntents.add(additionalIntent);
        additionalMoves.add(info);
        moveHistory.add(next);
        cardsToRender.add(enemyCard);
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (enemyCard != null) {
            enemyCard.damage = this.getIntentDmg();
            if (enemyCard.damage != enemyCard.baseDamage) {
                enemyCard.isDamageModified = true;
            } else {
                enemyCard.isDamageModified = false;
            }
        }
    }
}