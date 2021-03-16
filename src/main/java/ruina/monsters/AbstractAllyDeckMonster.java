package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.TransferBlockToAllyAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractDeckMonster;
import ruina.powers.InvisibleAllyBarricadePower;
import ruina.util.AllyMove;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.adp;

public abstract class AbstractAllyDeckMonster extends AbstractAllyMonster {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;
    public String allyIcon;

    //basically just for little Red who is an ally that can become an enemy
    public boolean isAlly = true;

    //essentially lets me have allies that the player can still target with cards
    public boolean isTargetableByPlayer = false;

    public ArrayList<AllyMove> allyMoves = new ArrayList<>();
    private static final int BLOCK_TRANSFER = 5;

    protected CardGroup masterDeck = new CardGroup(CardGroup.CardGroupType.HAND);
    protected CardGroup hand = new CardGroup(CardGroup.CardGroupType.HAND);
    protected CardGroup draw = new CardGroup(CardGroup.CardGroupType.HAND);
    protected CardGroup discard = new CardGroup(CardGroup.CardGroupType.HAND);
    protected CardGroup purge = new CardGroup(CardGroup.CardGroupType.HAND);

    public AbstractAllyDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.type = EnemyType.NORMAL;
    }

    public AbstractAllyDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        this.type = EnemyType.NORMAL;
    }

    public AbstractAllyDeckMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        this.type = EnemyType.NORMAL;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        AllyMove blockMove = new AllyMove(TEXT[11], this, new Texture(makeUIPath("defend.png")), TEXT[9] + BLOCK_TRANSFER + TEXT[10], () -> {
            atb(new TransferBlockToAllyAction(BLOCK_TRANSFER, this));
        });
        blockMove.setX(this.intentHb.x - ((30.0F + 32.0f) * Settings.scale));
        blockMove.setY(this.intentHb.cY - (32.0f * Settings.scale));
        allyMoves.add(blockMove);
        applyToTarget(this, this, new InvisibleAllyBarricadePower(this));
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

    public void initializeDeck(){
        masterDeck = new CardGroup(CardGroup.CardGroupType.HAND);
        draw = new CardGroup(CardGroup.CardGroupType.HAND);
        discard = new CardGroup(CardGroup.CardGroupType.HAND);
        purge = new CardGroup(CardGroup.CardGroupType.HAND);
        hand = new CardGroup(CardGroup.CardGroupType.HAND);
        createDeck();
        masterDeck.shuffle();
        for(AbstractCard c: masterDeck.group){
            draw.addToBottom(c);
        }
    }

    protected abstract void createDeck();

    public AbstractCard topDeckCardForMoveAction() {
        if (draw.isEmpty() && discard.isEmpty()) {
            System.out.println("oops");
            return new Madness();
        } else if (draw.isEmpty()) {
            shuffle();
        }
        AbstractCard c = draw.getTopCard();
        if (c != null) {
            moveToHand(c);
        }
//        System.out.println("Hand: " + hand);
//        System.out.println("Draw: " + draw);
//        System.out.println("Discard: " + discard);
        return c != null ? c : new Madness();
    }

    public void discardHand() {
        ArrayList<AbstractCard> cardsToExhaust = new ArrayList<>();
        ArrayList<AbstractCard> cardsToDiscard = new ArrayList<>();
        for (AbstractCard c : hand.group) {
            if (c != null) {
                if (c.exhaust) {
                    cardsToExhaust.add(c);
                } else {
                    cardsToDiscard.add(c);
                }
            }
        }
        for (AbstractCard card : cardsToExhaust) {
            moveToExhaust(card);
        }
        for (AbstractCard card : cardsToDiscard) {
            moveToDiscard(card);
        }
    }

    public void shuffle() {
        if (!discard.isEmpty()) {
            for (AbstractCard c : discard.group) {
                draw.addToBottom(c);
            }
            discard.clear();
            draw.shuffle();
        }
    }

    public void moveToDiscard(AbstractCard c){
        hand.removeCard(c);
        discard.addToBottom(c);
    }

    public void moveToExhaust(AbstractCard c){
        hand.removeCard(c);
        purge.addToBottom(c);
    }

    public void moveToHand(AbstractCard c) {
        draw.removeCard(c);
        hand.addToBottom(c);
    }

    @Override
    public void takeTurn() {
        if (isAlly && !isTargetableByPlayer) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = false;
                    this.isDone = true;
                }
            });
        }
        discardHand();
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    public void applyPowers(AbstractCreature target) {
        if (this.nextMove >= 0) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            if (target != adp()) {
                if(info.base > -1) {
                    if (this.intent == IntentEnums.MASS_ATTACK) {
                        info.applyPowers(this, adp());
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                        PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                        if (moves.get(this.nextMove).multiplier > 0) {
                            intentTip.body = TEXT[13] + info.output + TEXT[14] + " " + FontHelper.colorString(String.valueOf(moves.get(this.nextMove).multiplier), "b") + TEXT[16];
                        } else {
                            intentTip.body = TEXT[13] + info.output + TEXT[14] + TEXT[15];
                        }
                    } else {
                        Color color = new Color(0.0F, 1.0F, 0.0F, 0.5F);
                        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
                        info.applyPowers(this, target);
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

    @Override
    public void damage(DamageInfo info) {
        //failsafe to stop player from damaging allies
        if (isAlly && !isTargetableByPlayer && info.owner == adp()) {
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
        super.render(sb);
        if (isAlly && !isDead && !isDying) {
            for (AllyMove allyMove : allyMoves) {
                allyMove.render(sb);
            }
        }
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
        this.currentHealth = 0;
        this.loseBlock();
        this.isDead = true;
        this.isDying = true;
        this.healthBarUpdatedEvent();
    }

}
