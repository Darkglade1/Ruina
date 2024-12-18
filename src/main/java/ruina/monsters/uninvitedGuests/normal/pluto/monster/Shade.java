package ruina.monsters.uninvitedGuests.normal.pluto.monster;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Point;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.vfx.TintEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractDeckMonster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Shade extends AbstractDeckMonster
{
    public static final String ID = makeID(Shade.class.getSimpleName());

    public final int MAX_DAMAGE = calcAscensionSpecial(50);
    protected Map<Byte, Integer> blockMoveValues = new HashMap<>();

    public Shade() {
        this(0.0f, 0.0f);
    }

    public Shade(final float x, final float y) {
        super(ID, ID, 300, AbstractDungeon.player.hb_x / Settings.scale, AbstractDungeon.player.hb_y / Settings.scale, AbstractDungeon.player.hb_w / Settings.scale, AbstractDungeon.player.hb_h / Settings.scale, null, x, y);
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(300));
        // you removed my yan code by accident do not remove this or it will NPE and everything will go kaboom boom die and it will be ur fault
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Shade/Spriter/Shade.scml"));
        // double warning ^ do not touch this or i will be big mad
        this.dialogX = -(AbstractDungeon.player.dialogX - AbstractDungeon.player.drawX);
        this.dialogY =  (AbstractDungeon.player.dialogY - AbstractDungeon.player.drawY);
        setNumAdditionalMoves(1);
        AbstractCard fillerCard = new Madness(); //in case deck somehow has no cards
        addMove((byte) fillerCard.cardID.hashCode(), Intent.ATTACK, 12);
        initializeDeck();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Hokma) {
                target = (Hokma)mo;
            }
        }
        applyToTarget(this, this, new BarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        int blockValue = getBlockForMove(move.nextMove);
        if (info.base > -1) {
            if (blockValue > 0) {
                block(this, blockValue);
            }
            dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        } else {
            if (blockValue > 0) {
                block(this, blockValue);
            }
        }
        resetIdle();
    }


    @Override
    public void takeTurn() {
        AbstractCreature hokma = target;
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                hokma.halfDead = false;
                this.isDone = true;
            }
        });
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        AbstractCard c = topDeckCardForMoveAction();
        setMoveShortcut((byte) c.cardID.hashCode(), c.name, c);
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove));
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        super.applyPowers();
    }

    @Override
    protected void createDeck() {
        CardStrings damageString = CardCrawlGame.languagePack.getCardStrings(Strike_Red.ID);
        CardStrings blockString = CardCrawlGame.languagePack.getCardStrings(Defend_Red.ID);
        for (AbstractCard card : adp().masterDeck.group) {
            if ((card.baseDamage > 0 || card.baseBlock > 0) && !(card.rarity == AbstractCard.CardRarity.BASIC) && card.baseDamage <= MAX_DAMAGE) {
                AbstractCard cardCopy = card.makeStatEquivalentCopy();
                Intent intent;
                if (card.baseDamage > 0 && card.baseBlock > 0) {
                    intent = Intent.ATTACK_DEFEND;
                    cardCopy.rawDescription = blockString.DESCRIPTION + " NL " + damageString.DESCRIPTION;
                } else if (card.baseBlock > 0) {
                    intent = Intent.DEFEND;
                    cardCopy.rawDescription = blockString.DESCRIPTION;
                } else {
                    intent = Intent.ATTACK;
                    cardCopy.rawDescription = damageString.DESCRIPTION;
                }
                cardCopy.name += "?";
                cardCopy.initializeDescription();
                masterDeck.addToBottom(cardCopy);
                addMove((byte) card.cardID.hashCode(), intent, card.baseDamage);
                if (card.baseBlock > 0) {
                    blockMoveValues.put((byte) card.cardID.hashCode(), card.baseBlock);
                }
            }
        }
    }

    private int getBlockForMove(byte move) {
        if (blockMoveValues.containsKey(move)) {
            return blockMoveValues.get(move);
        }
        return -1;
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) { setAdditionalMoveShortcut((byte) c.cardID.hashCode(), moveHistory, c); }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractDungeon.onModifyPower();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDead && !this.escaped) {
            // Draw the Player's image
            // ... store player's values
            float playerDrawX = AbstractDungeon.player.drawX;
            float playerDrawY = AbstractDungeon.player.drawY;
            float playerAnimX = AbstractDungeon.player.animX;
            float playerAnimY = AbstractDungeon.player.animY;
            TintEffect playerTint = AbstractDungeon.player.tint;
            boolean playerFlipHorizontal = AbstractDungeon.player.flipHorizontal;
            float deltaTime = Gdx.graphics.getDeltaTime();
            int spriterSpeed = setSpriterAnimationSpeed(0);
            // ... set our values
            setDeltaTime(0);
            AbstractDungeon.player.drawX = this.drawX;
            AbstractDungeon.player.drawY = this.drawY;
            AbstractDungeon.player.animX = this.animX;
            AbstractDungeon.player.animY = this.animY + 10;
            AbstractDungeon.player.tint = this.tint;
            AbstractDungeon.player.flipHorizontal = !this.flipHorizontal;
            applyFlipToSkeleton();
            updateSpriterAnimationPosition();
            // ... draw
            AbstractDungeon.player.renderPlayerImage(sb);
            // ... restore player's values
            AbstractDungeon.player.drawX = playerDrawX;
            AbstractDungeon.player.drawY = playerDrawY;
            AbstractDungeon.player.animX = playerAnimX;
            AbstractDungeon.player.animY = playerAnimY;
            AbstractDungeon.player.tint = playerTint;
            AbstractDungeon.player.flipHorizontal = playerFlipHorizontal;
            applyFlipToSkeleton();
            updateSpriterAnimationPosition();
            setDeltaTime(deltaTime);
            setSpriterAnimationSpeed(spriterSpeed);
        }
        super.render(sb);
    }

    private void setDeltaTime(float deltaTime) {
        // When we call AbstractPlayer.renderPlayerImage, this updates animations based on Gdx.graphics.getDeltaTime().
        // So it would update the animation twice, resulting in a sped up animation.
        // As a fix, we set deltaTime to 0
        if (Gdx.graphics instanceof LwjglGraphics) {
            ReflectionHacks.setPrivate(Gdx.graphics, LwjglGraphics.class, "deltaTime", deltaTime);
        }
    }

    private void applyFlipToSkeleton() {
        // For some reason the world transform gets updated *after* setting the flip values. So we fix that here
        if (ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "skeleton") != null) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "skeleton");
            boolean flipVertical = (boolean)ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractCreature.class, "flipVertical");
            skeleton.setFlip(AbstractDungeon.player.flipHorizontal, flipVertical);
        }
    }

    private void updateSpriterAnimationPosition() {
        // Just like the spine skeletons, the SpriterAnimation does update before setting position
        if (AbstractDungeon.player instanceof CustomPlayer) {
            CustomPlayer p = (CustomPlayer)AbstractDungeon.player;
            AbstractAnimation anim = (AbstractAnimation)ReflectionHacks.getPrivate(p, CustomPlayer.class, "animation");
            if (anim instanceof SpriterAnimation) {
                Player myPlayer = (Player)ReflectionHacks.getPrivate(anim, SpriterAnimation.class, "myPlayer");
                Point pos = new Point();
                pos.x = p.drawX + p.animX;
                pos.y = p.drawY + p.animY + AbstractDungeon.sceneOffsetY;
                myPlayer.setPosition(pos);
            }
        }
    }

    private int setSpriterAnimationSpeed(int speed) {
        // Spriter animations don't use Gdx.deltaTime, rather they have a hardcoded speed
        if (AbstractDungeon.player instanceof CustomPlayer) {
            CustomPlayer p = (CustomPlayer)AbstractDungeon.player;
            AbstractAnimation anim = (AbstractAnimation)ReflectionHacks.getPrivate(p, CustomPlayer.class, "animation");
            if (anim instanceof SpriterAnimation) {
                Player myPlayer = (Player)ReflectionHacks.getPrivate(anim, SpriterAnimation.class, "myPlayer");
                int oldSpeed = myPlayer.speed;
                myPlayer.speed = speed;
                return oldSpeed;
            }
        }
        return 0;
    }
}