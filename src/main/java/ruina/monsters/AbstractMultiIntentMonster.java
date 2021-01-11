package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

    private void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent) {
        if (additionalMove.baseDamage > -1) {
            AbstractPlayer target = AbstractDungeon.player;
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
                this.tips.add(createAdditionalIntentTip(additionalIntent));
            }
        }
        for (AbstractPower p : this.powers) {
            if (p.region48 != null) {
                this.tips.add(new PowerTip(p.name, p.description, p.region48));
                continue;
            }
            this.tips.add(new PowerTip(p.name, p.description, p.img));
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

    public PowerTip createAdditionalIntentTip(AdditionalIntent additionalIntent) {
        PowerTip additionalIntentTip = new PowerTip();
        switch(additionalIntent.intent) {
            case ATTACK:
                additionalIntentTip.header = TEXT[0];
                if (additionalIntent.multihit) {
                    additionalIntentTip.body = TEXT[1] + additionalIntent.damage + TEXT[2] + additionalIntent.numHits + TEXT[3];
                } else {
                    additionalIntentTip.body = TEXT[4] + additionalIntent.damage + TEXT[5];
                }

                additionalIntentTip.img = this.getAttackIntentTip(additionalIntent);
                break;
            case ATTACK_BUFF:
                additionalIntentTip.header = TEXT[6];
                if (additionalIntent.multihit) {
                    additionalIntentTip.body = TEXT[7] + additionalIntent.damage + TEXT[2] + additionalIntent.numHits + TEXT[8];
                } else {
                    additionalIntentTip.body = TEXT[9] + additionalIntent.damage + TEXT[5];
                }

                additionalIntentTip.img = ImageMaster.INTENT_ATTACK_BUFF;
                break;
            case ATTACK_DEBUFF:
                additionalIntentTip.header = TEXT[10];
                if (additionalIntent.multihit) {
                    additionalIntentTip.body = TEXT[11] + additionalIntent.damage + TEXT[2] + additionalIntent.numHits + TEXT[3];
                } else {
                    additionalIntentTip.body = TEXT[11] + additionalIntent.damage + TEXT[5];
                }
                additionalIntentTip.img = ImageMaster.INTENT_ATTACK_DEBUFF;
                break;
            case ATTACK_DEFEND:
                additionalIntentTip.header = TEXT[0];
                if (additionalIntent.multihit) {
                    additionalIntentTip.body = TEXT[12] + additionalIntent.damage + TEXT[2] + additionalIntent.numHits + TEXT[3];
                } else {
                    additionalIntentTip.body = TEXT[12] + additionalIntent.damage + TEXT[5];
                }

                additionalIntentTip.img = ImageMaster.INTENT_ATTACK_DEFEND;
                break;
            case BUFF:
                additionalIntentTip.header = TEXT[10];
                additionalIntentTip.body = TEXT[19];
                additionalIntentTip.img = ImageMaster.INTENT_BUFF;
                break;
            case DEBUFF:
                additionalIntentTip.header = TEXT[10];
                additionalIntentTip.body = TEXT[20];
                additionalIntentTip.img = ImageMaster.INTENT_DEBUFF;
                break;
            case STRONG_DEBUFF:
                additionalIntentTip.header = TEXT[10];
                additionalIntentTip.body = TEXT[21];
                additionalIntentTip.img = ImageMaster.INTENT_DEBUFF2;
                break;
            case DEFEND:
                additionalIntentTip.header = TEXT[13];
                additionalIntentTip.body = TEXT[22];
                additionalIntentTip.img = ImageMaster.INTENT_DEFEND;
                break;
            case DEFEND_DEBUFF:
                additionalIntentTip.header = TEXT[13];
                additionalIntentTip.body = TEXT[23];
                additionalIntentTip.img = ImageMaster.INTENT_DEFEND;
                break;
            case DEFEND_BUFF:
                additionalIntentTip.header = TEXT[13];
                additionalIntentTip.body = TEXT[24];
                additionalIntentTip.img = ImageMaster.INTENT_DEFEND_BUFF;
                break;
            case ESCAPE:
                additionalIntentTip.header = TEXT[14];
                additionalIntentTip.body = TEXT[25];
                additionalIntentTip.img = ImageMaster.INTENT_ESCAPE;
                break;
            case MAGIC:
                additionalIntentTip.header = TEXT[15];
                additionalIntentTip.body = TEXT[26];
                additionalIntentTip.img = ImageMaster.INTENT_MAGIC;
                break;
            case SLEEP:
                additionalIntentTip.header = TEXT[16];
                additionalIntentTip.body = TEXT[27];
                additionalIntentTip.img = ImageMaster.INTENT_SLEEP;
                break;
            case STUN:
                additionalIntentTip.header = TEXT[17];
                additionalIntentTip.body = TEXT[28];
                additionalIntentTip.img = ImageMaster.INTENT_STUN;
                break;
            case UNKNOWN:
                additionalIntentTip.header = TEXT[18];
                additionalIntentTip.body = TEXT[29];
                additionalIntentTip.img = ImageMaster.INTENT_UNKNOWN;
                break;
            case NONE:
                additionalIntentTip.header = "";
                additionalIntentTip.body = "";
                additionalIntentTip.img = ImageMaster.INTENT_UNKNOWN;
                break;
            default:
                additionalIntentTip.header = "NOT SET";
                additionalIntentTip.body = "NOT SET";
                additionalIntentTip.img = ImageMaster.INTENT_UNKNOWN;
        }
        return additionalIntentTip;
    }

    private Texture getAttackIntentTip(AdditionalIntent additionalIntent) {
        int tmp;
        if (additionalIntent.multihit) {
            tmp = additionalIntent.damage * additionalIntent.numHits;
        } else {
            tmp = additionalIntent.damage;
        }

        if (tmp < 5) {
            return ImageMaster.INTENT_ATK_TIP_1;
        } else if (tmp < 10) {
            return ImageMaster.INTENT_ATK_TIP_2;
        } else if (tmp < 15) {
            return ImageMaster.INTENT_ATK_TIP_3;
        } else if (tmp < 20) {
            return ImageMaster.INTENT_ATK_TIP_4;
        } else if (tmp < 25) {
            return ImageMaster.INTENT_ATK_TIP_5;
        } else {
            return tmp < 30 ? ImageMaster.INTENT_ATK_TIP_6 : ImageMaster.INTENT_ATK_TIP_7;
        }
    }

}