package ruina.monsters;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.RunicDome;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.multiplayer.NetworkRuinaMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;
import spireTogether.networkcore.P2P.P2PManager;
import spireTogether.networkcore.objects.entities.NetworkMonster;
import spireTogether.other.RoomDataManager;
import spireTogether.util.SpireHelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public abstract class AbstractRuinaMonster extends CustomMonster {

    public String NAME;
    public String[] MOVES;
    public String[] DIALOG;

    protected Map<Byte, EnemyMoveInfo> moves;
    public boolean firstMove = true;
    protected DamageInfo info;
    protected int multiplier;
    private static final float ASCENSION_DAMAGE_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_TANK_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_SPECIAL_BUFF_PERCENT = 1.5f;
    private static final float ASCENSION_TANK_NERF_PERCENT = 0.85f;
    public AbstractRuinaMonster target;
    public Texture icon;
    public boolean justDiedThisTurn = false;

    // for stance particle effects
    protected float particleTimer;
    protected float particleTimer2;

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        setUpMisc();
        setUpStrings(id);
    }

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        setUpMisc();
        setUpStrings(id);
    }

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        setUpMisc();
        setUpStrings(id);
    }


    protected void setUpMisc() {
        moves = new HashMap<>();
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
    }

    protected void setUpStrings(String ID) {
        MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        this.name = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }

    protected void addMove(byte moveCode, Intent intent) {
        this.addMove(moveCode, intent, -1);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage) {
        this.addMove(moveCode, intent, baseDamage, 0, false);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier) {
        this.addMove(moveCode, intent, baseDamage, multiplier, true);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        this.moves.put(moveCode, new EnemyMoveInfo(moveCode, intent, baseDamage, multiplier, isMultiDamage));
    }

    public void setMoveShortcut(byte next) {
        String moveName = null;
        if (next < MOVES.length) {
            moveName = MOVES[next];
        }
        EnemyMoveInfo info = this.moves.get(next);
        this.setMove(moveName, next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }

    @Override
    public void takeTurn() {
        this.info = getInfoFromMove(this.nextMove);
        this.multiplier = getMultiplierFromMove(this.nextMove);
        if (firstMove) {
            firstMove = false;
            if (RuinaMod.isMultiplayerConnected()) {
                P2PManager.SendData(NetworkRuinaMonster.request_monsterUpdateFirstMove, firstMove, SpireHelp.Gameplay.CreatureToUID(this), SpireHelp.Gameplay.GetMapLocation());
                NetworkMonster m = RoomDataManager.GetMonsterForCurrentRoom(this);
                if (m instanceof NetworkRuinaMonster) {
                    ((NetworkRuinaMonster)m).firstMove = this.firstMove;
                }
            }
        }
        if(info.base > -1) {
            if (target != null) {
                info.applyPowers(this, target);
            } else {
                info.applyPowers(this, adp());
            }
        }
        for (AbstractPower power : this.powers) {
            if (power instanceof InvisibleBarricadePower) {
                atb(new RemoveAllBlockAction(this, this));
            }
        }
    }

    protected DamageInfo getInfoFromMove(byte move) {
        if(moves.containsKey(move)) {
            EnemyMoveInfo emi = moves.get(move);
            return new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
        } else {
            return new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
    }

    protected int getMultiplierFromMove(byte move) {
        int multiplier = 0;
        if(moves.containsKey(move)) {
            EnemyMoveInfo emi = moves.get(move);
            multiplier = emi.multiplier;
        }
        return multiplier;
    }

    protected int calcAscensionDamage(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 4) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 3) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 2) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionTankiness(float base) {
        if (this instanceof AbstractAllyMonster) {
            switch (this.type) {
                case BOSS:
                    if(AbstractDungeon.ascensionLevel >= 9) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
                case ELITE:
                    if(AbstractDungeon.ascensionLevel >= 8) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
                case NORMAL:
                    if(AbstractDungeon.ascensionLevel >= 7) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
            }
        } else {
            switch (this.type) {
                case BOSS:
                    if(AbstractDungeon.ascensionLevel >= 9) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
                case ELITE:
                    if(AbstractDungeon.ascensionLevel >= 8) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
                case NORMAL:
                    if(AbstractDungeon.ascensionLevel >= 7) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
            }
        }
        return Math.round(base);
    }

    protected int calcAscensionSpecial(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 19) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 18) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 17) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    @Override
    public void die(boolean triggerRelics) {
        this.justDiedThisTurn = true;
        this.useShakeAnimation(5.0F);
        if (this.animation instanceof BetterSpriterAnimation) {
            ((BetterSpriterAnimation)this.animation).startDying();
        }
        super.die(triggerRelics);
    }

    public int[] calcMassAttack(DamageInfo info) {
        int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
        info.applyPowers(this, adp());
        damageArray[damageArray.length - 1] = info.output;
        for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            info.applyPowers(this, mo);
            damageArray[i] = info.output;
        }
        return damageArray;
    }

    public int[] calcMassAttackNoHitPlayer(DamageInfo info) {
        int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size()];
        for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            info.applyPowers(this, mo);
            damageArray[i] = info.output;
        }
        return damageArray;
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    protected void animationAction(String animation, String sound, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound, volume);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound, volume);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, AbstractCreature owner) {
        animationAction(animation, sound, null, owner);
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature owner) {
        animationAction(animation, sound, volume, null, owner);
    }

    public static void playSound(String sound, float volume) {
        if (sound != null) {
            CardCrawlGame.sound.playV(makeID(sound), volume);
        }
    }

    public static void playSound(String sound) {
        playSound(sound, 1.0f);
    }

    public static void playSoundAnimation(String sound, float volume) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound(sound, volume);
                this.isDone = true;
            }
        });
    }

    public static void playSoundAnimation(String sound) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound(sound);
                this.isDone = true;
            }
        });
    }

    public void resetIdle() {
        resetIdle(0.5f);
    }

    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle");
                this.isDone = true;
            }
        });
    }

    public void waitAnimation() {
        waitAnimation(0.5f, null);
    }

    public void waitAnimation(float duration) {
        waitAnimation(duration, null);
    }

    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.5f, enemy);
    }

    public void waitAnimation(float time, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractRuinaMonster.this.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    att(new VFXActionButItCanFizzle(AbstractRuinaMonster.this, new WaitEffect(), time));
                } else if (!enemy.isDeadOrEscaped()) {
                    att(new VFXActionButItCanFizzle(AbstractRuinaMonster.this, new WaitEffect(), time));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        Map<Integer, ArrayList<DetailedIntent>> detailsMap = DetailedIntent.intents.get(this);
        if (detailsMap != null && !this.isDead && !this.isDying && !AbstractDungeon.isScreenUp) {
            for (int intentNum : detailsMap.keySet()) {
                ArrayList<DetailedIntent> detailList = detailsMap.get(intentNum);
                for (int i = 0; i < detailList.size(); i++) {
                    DetailedIntent detail = detailList.get(i);
                    detail.renderDetails(sb, i + 1, intentNum + 1);
                }
            }
        }
        super.render(sb);
    }

    protected void postGetMove() {
        setDetailedIntents();
    }

    protected void setDetailedIntents() {
        if (!RuinaMod.disableDetailedIntentsConfig && !adp().hasRelic(RunicDome.ID)) {
            DetailedIntent.intents.put(this, getDetailedIntents());
        }
    }

    protected Map<Integer, ArrayList<DetailedIntent>> getDetailedIntents() {
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        Map<Integer, ArrayList<DetailedIntent>> detailsMap = new HashMap<>();
        ArrayList<DetailedIntent> details = getDetails(move, -1);
        if (details != null) {
            detailsMap.put(-1, details);
        }
        return detailsMap;
    }

    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        return null;
    }

    @Override
    public void rollMove() {
        super.rollMove();
        postGetMove();
    }

    protected boolean lastMoveBeforeBefore(byte move) {
        if (moveHistory.isEmpty()) {
            return false;
        } else if (moveHistory.size() < 3) {
            return false;
        } else {
            return moveHistory.get(moveHistory.size() - 3) == move;
        }
    }

    protected boolean threeTurnCooldownHasPassedForMove(byte move) {
        return moveHistory.size() >= 3 && !this.lastMove(move) && !this.lastMoveBefore(move) && !this.lastMoveBeforeBefore(move);
    }

}