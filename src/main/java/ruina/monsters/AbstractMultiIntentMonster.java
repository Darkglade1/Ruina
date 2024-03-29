package ruina.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.powers.Enchanted;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;
import java.util.Iterator;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public abstract class AbstractMultiIntentMonster extends AbstractRuinaMonster {
    public ArrayList<EnemyMoveInfo> additionalMoves = new ArrayList<>();
    protected ArrayList<ArrayList<Byte>> additionalMovesHistory = new ArrayList<>();
    public ArrayList<AdditionalIntent> additionalIntents = new ArrayList<>();
    protected int numAdditionalMoves = 0;
    protected int maxAdditionalMoves = 0;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {

    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    protected void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, String targetTexturePath, int whichMove) {
        if (!AbstractDungeon.actionManager.turnHasEnded && (additionalMove.nextMove == -1 || target.isDead || target.isDying)) {
            target = adp();
        }
        if (additionalMove.baseDamage > -1) {
            int dmg = additionalMove.baseDamage;
            DamageInfo info = new DamageInfo(this, dmg);
            info.applyPowers(this, target);

            dmg = info.output;

            if (this instanceof Argalia && additionalMove.nextMove == Argalia.SCYTHE && whichMove == Argalia.SCYTHE_INTENT_NUM) {
                dmg = (int)(dmg * Argalia.scytheDamageMultiplier);
            }
            if (target.hasPower(Enchanted.POWER_ID) && this.hasPower(BigBird.Salvation_POWER_ID)) {
                dmg = BigBird.INSTANT_KILL_NUM;
            }
            additionalIntent.updateDamage(dmg);
            if (target != adp()) {
                PowerTip intentTip = additionalIntent.intentTip;
                if (additionalIntent.numHits > 0) {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + additionalIntent.damage + TEXT[3] + additionalIntent.numHits + TEXT[4];
                } else {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + additionalIntent.damage + TEXT[2];
                }
                if (targetTexturePath != null) {
                    additionalIntent.setTargetTexture(targetTexturePath);
                }
            } else {
                additionalIntent.clearTargetTexture();
            }
        } else {
            if (additionalIntent.intent == Intent.DEBUFF || additionalIntent.intent == Intent.STRONG_DEBUFF || additionalIntent.intent == Intent.DEFEND_DEBUFF) {
                if (target != adp()) {
                    PowerTip intentTip = additionalIntent.intentTip;
                    intentTip.body = TEXT[5] + FontHelper.colorString(target.name, "y") + TEXT[6];
                    if (targetTexturePath != null) {
                        additionalIntent.setTargetTexture(targetTexturePath);
                    }
                } else {
                    additionalIntent.clearTargetTexture();
                }
            }
        }
    }

    protected void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, String targetTexturePath) {
        applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, targetTexturePath, -1);
    }

    @Override
    public void rollMove() {
        additionalIntents.clear();
        additionalMoves.clear();
        this.getMove(AbstractDungeon.aiRng.random(99));
        for (int i = 0; i < numAdditionalMoves; i++) {
            getAdditionalMoves(AbstractDungeon.aiRng.random(99), i);
        }
    }

    public void getAdditionalMoves(int num, int whichMove) {

    }

    public void setAdditionalMoveShortcut(byte next, ArrayList<Byte> moveHistory) {
        EnemyMoveInfo info = this.moves.get(next);
        AdditionalIntent additionalIntent = new AdditionalIntent(this, info);
        additionalIntents.add(additionalIntent);
        additionalMoves.add(info);
        moveHistory.add(next);
    }

    protected boolean lastMove(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 1) == move;
        }
    }

    protected boolean lastMoveBefore(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else if (moveHistory.size() < 2) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    protected boolean lastTwoMoves(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.size() < 2) {
            return false;
        } else {
            return (Byte)moveHistory.get(moveHistory.size() - 1) == move && (Byte)moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        int position = 1;
        for (AdditionalIntent additionalIntent : additionalIntents) {
            if (additionalIntent != null && !this.hasPower(StunMonsterPower.POWER_ID)) {
                additionalIntent.update(position);
                if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic(RunicDome.ID) && this.intent != AbstractMonster.Intent.NONE && !Settings.hideCombatElements) {
                    additionalIntent.render(sb, position);
                }
            }
            position++;
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        if (!adp().hasRelic(RunicDome.ID) && !this.hasPower(StunMonsterPower.POWER_ID)) {
            if (tips.size() > 0) {
                for (int i = 0; i < additionalIntents.size(); i++) {
                    AdditionalIntent additionalIntent = additionalIntents.get(i);
                    if (!additionalIntent.transparent) {
                        this.tips.add(i + 1, additionalIntent.intentTip);
                    }
                }
            }
        }
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentBaseDmg() {
        int original = super.getIntentBaseDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.baseDamage > maxDamage) {
                maxDamage = additionalIntent.baseDamage;
            }
        }
        return maxDamage;
    }

    public int getRealIntentBaseDmg() {
        return super.getIntentBaseDmg();
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentDmg() {
        int original = super.getIntentDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.damage > maxDamage) {
                maxDamage = additionalIntent.damage;
            }
        }
        if (maxDamage == BigBird.INSTANT_KILL_NUM && this instanceof BigBird) {
            //stop cards that deal damage based on intent from trivializing this fight
            return 16;
        }
        return maxDamage;
    }

    public int getRealIntentDmg() {
        return super.getIntentDmg();
    }

    @Override
    public void takeTurn() {
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            //that way they don't fade out after the monster takes its primary intent
            additionalIntent.usePrimaryIntentsColor = false;
        }
    }
}