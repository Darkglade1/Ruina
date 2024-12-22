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
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.TintEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Shade extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Shade.class.getSimpleName());

    private static final byte MIMIC_ATK = 0;
    private static final byte MIMIC_ATK_2 = 1;
    private static final byte MIMIC_DEFENSE = 2;
    private static final byte MIMIC_BUFF = 3;

    private final int BLOCK = calcAscensionTankiness(25);
    private final int STR = calcAscensionSpecial(2);

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

        addMove(MIMIC_ATK, Intent.ATTACK, calcAscensionDamage(5), 2);
        addMove(MIMIC_ATK_2, Intent.ATTACK, calcAscensionDamage(13));
        addMove(MIMIC_DEFENSE, Intent.DEFEND);
        addMove(MIMIC_BUFF, Intent.BUFF);
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
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case MIMIC_ATK: {
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
                break;
            }
            case MIMIC_ATK_2:
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            case MIMIC_DEFENSE:
                block(this, BLOCK);
                break;
            case MIMIC_BUFF:
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
        }
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
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(MIMIC_ATK) && !this.lastMoveBefore(MIMIC_ATK)) {
            possibilities.add(MIMIC_ATK);
        }
        if (!this.lastMove(MIMIC_ATK_2) && !this.lastMoveBefore(MIMIC_ATK_2)) {
            possibilities.add(MIMIC_ATK_2);
        }
        if (!this.lastMove(MIMIC_DEFENSE) && !this.lastMoveBefore(MIMIC_DEFENSE)) {
            possibilities.add(MIMIC_DEFENSE);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(MIMIC_ATK, moveHistory) && !this.lastMoveBefore(MIMIC_ATK, moveHistory)) {
            possibilities.add(MIMIC_ATK);
        }
        if (!this.lastMove(MIMIC_ATK_2, moveHistory) && !this.lastMoveBefore(MIMIC_ATK_2, moveHistory)) {
            possibilities.add(MIMIC_ATK_2);
        }
        if (!this.lastMove(MIMIC_BUFF, moveHistory) && !this.lastMoveBefore(MIMIC_BUFF, moveHistory)) {
            possibilities.add(MIMIC_BUFF);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
    }

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