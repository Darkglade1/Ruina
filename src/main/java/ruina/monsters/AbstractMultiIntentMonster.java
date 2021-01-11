package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.RunicDome;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractMultiIntentMonster extends AbstractRuinaMonster {
    protected ArrayList<EnemyMoveInfo> additionalMoves = new ArrayList<>();
    protected ArrayList<ArrayList<Byte>> additionalMovesHistory = new ArrayList<>();
    protected ArrayList<AdditionalIntent> additionalIntents = new ArrayList<>();
    protected int numAdditionalMoves = 0;

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    public void takeCustomTurn(EnemyMoveInfo move) {

    }

    protected void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target) {
        if (additionalMove.baseDamage > -1) {
            int dmg = additionalMove.baseDamage;
            float tmp = (float)dmg;
            if (Settings.isEndless && AbstractDungeon.player.hasBlight("DeadlyEnemies")) {
                float mod = AbstractDungeon.player.getBlight("DeadlyEnemies").effectFloat();
                tmp *= mod;
            }

            AbstractPower p;
            Iterator var6;
            for(var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageGive(tmp, DamageInfo.DamageType.NORMAL)) {
                p = (AbstractPower)var6.next();
            }

            for(var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL)) {
                p = (AbstractPower)var6.next();
            }

            tmp = AbstractDungeon.player.stance.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);

            for(var6 = this.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL)) {
                p = (AbstractPower)var6.next();
            }

            for(var6 = target.powers.iterator(); var6.hasNext(); tmp = p.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL)) {
                p = (AbstractPower)var6.next();
            }

            dmg = MathUtils.floor(tmp);
            if (dmg < 0) {
                dmg = 0;
            }
            additionalIntent.updateDamage(dmg);
        }
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

    public void setAdditionalMoveShortcut(byte next, String text, ArrayList<Byte> moveHistory) {
        EnemyMoveInfo info = this.moves.get(next);
        AdditionalIntent additionalIntent = new AdditionalIntent(this, info);
        additionalIntents.add(additionalIntent);
        additionalMoves.add(info);
        moveHistory.add(next);
    }
    public void setAdditionalMoveShortcut(byte next, ArrayList<Byte> moveHistory) {
        this.setAdditionalMoveShortcut(next, null, moveHistory);
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
        for (AdditionalIntent additionalIntent : additionalIntents) {
            if (additionalIntent != null && !this.hasPower(StunMonsterPower.POWER_ID)) {
                additionalIntent.update();
                additionalIntent.render(sb);
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        this.tips.clear();
        if (!AbstractDungeon.player.hasRelic(RunicDome.ID)) {
            PowerTip intentTip = (PowerTip) ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
            this.tips.add(intentTip);
            for (AdditionalIntent additionalIntent : additionalIntents) {
                this.tips.add(additionalIntent.intentTip);
            }
        }
        for (AbstractPower p : this.powers) {
            if (p.region48 != null && !(p instanceof InvisiblePower)) {
                this.tips.add(new PowerTip(p.name, p.description, p.region48));
                continue;
            }
            if (!(p instanceof InvisiblePower)) {
                this.tips.add(new PowerTip(p.name, p.description, p.img));
            }
        }
        if (!this.tips.isEmpty()) {
            if (this.hb.cX + this.hb.width / 2.0F < TIP_X_THRESHOLD) {
                TipHelper.queuePowerTips(this.hb.cX + this.hb.width / 2.0F + TIP_OFFSET_R_X, this.hb.cY +

                        TipHelper.calculateAdditionalOffset(this.tips, this.hb.cY), this.tips);
            } else {
                TipHelper.queuePowerTips(this.hb.cX - this.hb.width / 2.0F + TIP_OFFSET_L_X, this.hb.cY +

                        TipHelper.calculateAdditionalOffset(this.tips, this.hb.cY), this.tips);
            }
        }
    }

}