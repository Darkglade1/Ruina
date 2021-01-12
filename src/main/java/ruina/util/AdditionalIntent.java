package ruina.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.DebuffParticleEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.BuffParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.StunStarEffect;
import com.megacrit.cardcrawl.vfx.combat.UnknownParticleEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class AdditionalIntent {
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("AbstractMonster").TEXT;
    public AbstractMonster.Intent intent;

    public int damage;
    public boolean multihit;
    public int numHits;

    private AbstractMonster source;
    private BobEffect bobEffect;
    private float intentParticleTimer;
    private float intentAngle;
    private Texture intentImg;
    private Texture intentBg;
    public PowerTip intentTip;

    private ArrayList<AbstractGameEffect> intentVfx;

    private Color intentColor;

    float scaleWidth = 1.0F * Settings.scale;
    float scaleHeight = Settings.scale;

    public AdditionalIntent(AbstractMonster source, EnemyMoveInfo move) {
        this.source = source;
        intentColor = Color.WHITE.cpy();

        intent = move.intent;

        damage = move.baseDamage;
        multihit = move.isMultiDamage;
        numHits = move.multiplier;

        intentParticleTimer = 0.5f;
        this.bobEffect = new BobEffect();

        this.intentImg = this.getIntentImg();
        this.intentBg = null;

        intentVfx = new ArrayList<>();

        intentColor.a = 0.0f;
        intentTip = createAdditionalIntentTip(this);
    }

    public void updateDamage(int newDamage) {
        damage = newDamage;
        intentTip = createAdditionalIntentTip(this);
        this.intentImg = this.getIntentImg();
    }

    public void update() {
        this.bobEffect.update();
        if (intentColor.a != 1.0F) {
            intentColor.a += Gdx.graphics.getDeltaTime();
            if (intentColor.a > 1.0f) {
                intentColor.a = 1.0f;
            }
        }

        this.updateIntentVFX();

        Iterator<AbstractGameEffect> i = this.intentVfx.iterator();

        AbstractGameEffect e;
        while(i.hasNext()) {
            e = i.next();
            e.update();
            if (e.isDone) {
                i.remove();
            }
        }
    }

    public void render(SpriteBatch sb, int position) {
        this.renderIntentVfxBehind(sb);
        this.renderIntent(sb, position);
        this.renderIntentVfxAfter(sb);
        this.renderDamageRange(sb);
    }

    public void renderIntent(SpriteBatch sb, int position) {
        sb.setColor(intentColor);
        if (this.intentBg != null) {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, intentColor.a / 2.0F));
            sb.draw(this.intentBg, source.intentHb.cX - 64.0F + (86.0F * scaleWidth * position), source.intentHb.cY - 64.0F + this.bobEffect.y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
        }

        if (this.intentImg != null && this.intent != AbstractMonster.Intent.UNKNOWN && this.intent != AbstractMonster.Intent.STUN) {
            if (this.intent != AbstractMonster.Intent.DEBUFF && this.intent != AbstractMonster.Intent.STRONG_DEBUFF) {
                this.intentAngle = 0.0F;
            } else {
                this.intentAngle += Gdx.graphics.getDeltaTime() * 150.0F;
            }

            sb.setColor(this.intentColor);// 1079
            sb.draw(this.intentImg, source.intentHb.cX - 64.0F + (86.0F * scaleWidth), source.intentHb.cY - 64.0F + this.bobEffect.y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, this.intentAngle, 0, 0, 128, 128, false, false);
        }
    }

    private void renderDamageRange(SpriteBatch sb) {
        if (this.intent.name().contains("ATTACK")) {
            if (this.multihit) {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, this.damage + "x" + this.numHits, source.intentHb.cX + 50.0F * Settings.scale, source.intentHb.cY + this.bobEffect.y - 12.0F * Settings.scale, this.intentColor);
            } else {
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.damage), source.intentHb.cX + 50.0F * Settings.scale, source.intentHb.cY + this.bobEffect.y - 12.0F * Settings.scale, this.intentColor);
            }
        }
    }

    private void renderIntentVfxBehind(SpriteBatch sb) {
        for (AbstractGameEffect e : this.intentVfx) {
            if (e.renderBehind) {
                e.render(sb);
            }
        }
    }

    private void renderIntentVfxAfter(SpriteBatch sb) {
        for (AbstractGameEffect e : this.intentVfx) {
            if (!e.renderBehind) {
                e.render(sb);
            }
        }
    }

    private void updateIntentVFX() {
        if (intentColor.a > 0.0F) {
            if (this.intent != AbstractMonster.Intent.ATTACK_DEBUFF && this.intent != AbstractMonster.Intent.DEBUFF && this.intent != AbstractMonster.Intent.STRONG_DEBUFF && this.intent != AbstractMonster.Intent.DEFEND_DEBUFF) {
                if (this.intent != AbstractMonster.Intent.ATTACK_BUFF && this.intent != AbstractMonster.Intent.BUFF && this.intent != AbstractMonster.Intent.DEFEND_BUFF) {
                    if (this.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
                        this.intentParticleTimer -= Gdx.graphics.getDeltaTime();
                        if (this.intentParticleTimer < 0.0F) {
                            this.intentParticleTimer = 0.5F;
                            this.intentVfx.add(new ShieldParticleEffect(source.intentHb.cX + 72.0F * scaleWidth, source.intentHb.cY));
                        }
                    } else if (this.intent == AbstractMonster.Intent.UNKNOWN) {
                        this.intentParticleTimer -= Gdx.graphics.getDeltaTime();
                        if (this.intentParticleTimer < 0.0F) {
                            this.intentParticleTimer = 0.5F;
                            this.intentVfx.add(new UnknownParticleEffect(source.intentHb.cX + 72.0F * scaleWidth, source.intentHb.cY));
                        }
                    } else if (this.intent == AbstractMonster.Intent.STUN) {
                        this.intentParticleTimer -= Gdx.graphics.getDeltaTime();
                        if (this.intentParticleTimer < 0.0F) {
                            this.intentParticleTimer = 0.67F;
                            this.intentVfx.add(new StunStarEffect(source.intentHb.cX + 72.0F * scaleWidth, source.intentHb.cY));
                        }
                    }
                } else {
                    this.intentParticleTimer -= Gdx.graphics.getDeltaTime();
                    if (this.intentParticleTimer < 0.0F) {
                        this.intentParticleTimer = 0.1F;
                        this.intentVfx.add(new BuffParticleEffect(source.intentHb.cX + 72.0F * scaleWidth, source.intentHb.cY));
                    }
                }
            } else {
                this.intentParticleTimer -= Gdx.graphics.getDeltaTime();
                if (this.intentParticleTimer < 0.0F) {
                    this.intentParticleTimer = 1.0F;
                    this.intentVfx.add(new DebuffParticleEffect(source.intentHb.cX + 72.0F * scaleWidth, source.intentHb.cY));
                }
            }
        }
    }


    private Texture getIntentImg() {
        switch(this.intent) {
            case ATTACK:
            case ATTACK_BUFF:
            case ATTACK_DEBUFF:
            case ATTACK_DEFEND:
                return this.getAttackIntent();
            case BUFF:
                return ImageMaster.INTENT_BUFF_L;
            case DEBUFF:
                return ImageMaster.INTENT_DEBUFF_L;
            case STRONG_DEBUFF:
                return ImageMaster.INTENT_DEBUFF2_L;
            case DEFEND:
            case DEFEND_DEBUFF:
                return ImageMaster.INTENT_DEFEND_L;
            case DEFEND_BUFF:
                return ImageMaster.INTENT_DEFEND_BUFF_L;
            case ESCAPE:
                return ImageMaster.INTENT_ESCAPE_L;
            case MAGIC:
                return ImageMaster.INTENT_MAGIC_L;
            case SLEEP:
                return ImageMaster.INTENT_SLEEP_L;
            case STUN:
                return ImageMaster.INTENT_STUN;
            default:
                return ImageMaster.INTENT_UNKNOWN_L;
        }
    }

    protected Texture getAttackIntent() {
        int tmp;
        if (multihit) {
            tmp = this.damage * this.numHits;
        } else {
            tmp = this.damage;
        }

        if (tmp < 5) {
            return ImageMaster.INTENT_ATK_1;
        } else if (tmp < 10) {
            return ImageMaster.INTENT_ATK_2;
        } else if (tmp < 15) {
            return ImageMaster.INTENT_ATK_3;
        } else if (tmp < 20) {
            return ImageMaster.INTENT_ATK_4;
        } else if (tmp < 25) {
            return ImageMaster.INTENT_ATK_5;
        } else {
            return tmp < 30 ? ImageMaster.INTENT_ATK_6 : ImageMaster.INTENT_ATK_7;
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