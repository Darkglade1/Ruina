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

import java.util.ArrayList;

import static ruina.util.AdditionalIntent.ENEMY_CARD_HOVERED_HEIGHT;
import static ruina.util.AdditionalIntent.ENEMY_CARD_HOVERED_WIDTH;

public abstract class AbstractAllyCardMonster extends AbstractAllyMonster {
    protected AbstractCard allyCard = null;
    public static AbstractCard hoveredCard = null;
    public ArrayList<AbstractCard> cardList = new ArrayList<>();

    public AbstractAllyCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractAllyCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractAllyCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void update() {
        super.update();
        if (allyCard != null && !this.isDead && !this.isDying && !hasPower(StunMonsterPower.POWER_ID)) {
            allyCard.hb.update();
            if (Settings.FAST_MODE) {
                allyCard.current_x = MathHelper.cardLerpSnap(allyCard.current_x, allyCard.target_x);
                allyCard.current_y = MathHelper.cardLerpSnap(allyCard.current_y, allyCard.target_y);
            }

            allyCard.current_x = MathHelper.cardLerpSnap(allyCard.current_x, allyCard.target_x);
            allyCard.current_y = MathHelper.cardLerpSnap(allyCard.current_y, allyCard.target_y);
            allyCard.hb.move(allyCard.current_x, allyCard.current_y);
            allyCard.hb.resize(ENEMY_CARD_HOVERED_WIDTH * allyCard.drawScale, ENEMY_CARD_HOVERED_HEIGHT * allyCard.drawScale);
            if (allyCard.hb.hovered) {
                if (hoveredCard == null) {
                    hoveredCard = allyCard;
                }
            } else {
                if (hoveredCard == allyCard) {
                    hoveredCard = null;
                }
            }
            if (hoveredCard == allyCard) {
                allyCard.drawScale = MathHelper.cardScaleLerpSnap(allyCard.drawScale, allyCard.targetDrawScale * 3.0F);
                allyCard.drawScale = MathHelper.cardScaleLerpSnap(allyCard.drawScale, allyCard.targetDrawScale * 3.0F);
            } else {
                allyCard.drawScale = MathHelper.cardScaleLerpSnap(allyCard.drawScale, allyCard.targetDrawScale);
            }
        }
    }

    public void renderCard(SpriteBatch sb) {
        Color color = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentColor");
        if (color.a > 0 && !hasPower(StunMonsterPower.POWER_ID)) {
            sb.setColor(color);
            if (allyCard != hoveredCard) {
                allyCard.render(sb);
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

    public void setAllyCard(AbstractCard card, int baseDamage) {
        allyCard = card;
        if (allyCard != null) {
            allyCard.drawScale = 0.25f;
            allyCard.targetDrawScale = 0.25f;
            allyCard.baseDamage = baseDamage;
            allyCard.current_x = this.intentHb.cX;
            allyCard.target_x = this.intentHb.cX;
            allyCard.current_y = this.intentHb.cY + (100.0f * Settings.scale);
            allyCard.target_y = this.intentHb.cY + (100.0f * Settings.scale);
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        updateAllyCard();
    }

    private void updateAllyCard() {
        if (allyCard != null) {
            allyCard.damage = this.getIntentDmg();
            if (allyCard.damage != allyCard.baseDamage) {
                allyCard.isDamageModified = true;
            } else {
                allyCard.isDamageModified = false;
            }
        }
    }

    @Override
    public void createIntent() {
        setCards();
        super.createIntent();
    }

    private void setCards() {
        allyCard = null;
        AbstractAllyCardMonster.hoveredCard = null; //in case the player was hovering one of them while it was getting yeeted

        EnemyMoveInfo info = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        byte next = info.nextMove;
        AbstractCard card = getCardFromMove(next);
        if (card != null) {
            moveName = card.name;
            setAllyCard(card, info.baseDamage);
        }
    }

    private AbstractCard getCardFromMove(byte next) {
        if (next >= 0 && next < cardList.size()) {
            return cardList.get(next);
        }
        return null;
    }


}