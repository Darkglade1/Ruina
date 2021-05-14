package ruina.monsters.theHead.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.ui.DialogWord.AppearEffect;
import com.megacrit.cardcrawl.ui.DialogWord.WordColor;
import com.megacrit.cardcrawl.ui.DialogWord.WordEffect;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Dialog {
    private Color color = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    private Color targetColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    private static final Color PANEL_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.5F);
    private static final float COLOR_FADE_SPEED = 8.0F;
    private boolean isMoving = false;
    private float animateTimer = 0.0F;
    private static final float ANIM_SPEED = 0.5F;
    private boolean show = false;
    private float curLineWidth = 0.0F;
    private int curLine = 0;
    private AppearEffect a_effect;
    private Scanner s;
    private GlyphLayout gl = new GlyphLayout();
    private ArrayList<DialogWord> words = new ArrayList();
    private boolean textDone = true;
    private float wordTimer = 0.0F;
    private static final float WORD_TIME = 0.02F;
    private static final float CHAR_SPACING;
    private static final float LINE_SPACING;
    public static final float DIALOG_MSG_X;
    public static final float DIALOG_MSG_Y;
    public static final float DIALOG_MSG_W;
    public static ArrayList<LargeDialogOptionButton> optionList;
    public static int selectedOption;
    public static boolean waitForInput;

    public Dialog() {
    }// 39

    public void update() {
        if (this.isMoving) {// 53
            this.animateTimer -= Gdx.graphics.getDeltaTime();// 54
            if (this.animateTimer < 0.0F) {// 55
                this.animateTimer = 0.0F;// 56
                this.isMoving = false;// 57
            }
        }

        this.color = this.color.lerp(this.targetColor, Gdx.graphics.getDeltaTime() * 8.0F);// 61
        if (this.show) {// 63
            for(int i = 0; i < optionList.size(); ++i) {// 64
                ((LargeDialogOptionButton)optionList.get(i)).update(optionList.size());// 65
                if (((LargeDialogOptionButton)optionList.get(i)).pressed && waitForInput) {// 66
                    selectedOption = i;// 67
                    ((LargeDialogOptionButton)optionList.get(i)).pressed = false;// 68
                    waitForInput = false;// 69
                }
            }
        }

        if (Settings.lineBreakViaCharacter) {// 74
            this.bodyTextEffectCN();// 75
        } else {
            this.bodyTextEffect();// 77
        }

        Iterator var3 = this.words.iterator();// 79

        while(var3.hasNext()) {
            DialogWord w = (DialogWord)var3.next();
            w.update();// 80
        }

    }// 82

    public int getSelectedOption() {
        waitForInput = true;// 85
        return selectedOption;// 86
    }

    public void clear() {
        optionList.clear();// 90
        this.words.clear();// 91
        waitForInput = true;// 92
    }// 93

    public void show() {
        this.targetColor = PANEL_COLOR;// 96
        if (Settings.FAST_MODE) {// 97
            this.animateTimer = 0.125F;// 98
        } else {
            this.animateTimer = 0.5F;// 100
        }

        this.show = true;// 102
        this.isMoving = true;// 103
    }// 104

    public void show(String text) {
        this.updateBodyText(text);// 107
        this.show();// 108
    }// 109

    public void hide() {
        this.targetColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);// 112
        if (Settings.FAST_MODE) {// 113
            this.animateTimer = 0.125F;// 114
        } else {
            this.animateTimer = 0.5F;// 116
        }

        this.show = false;// 118
        this.isMoving = true;// 119
        Iterator var1 = this.words.iterator();

        while(var1.hasNext()) {
            DialogWord w = (DialogWord)var1.next();// 120
            w.dialogFadeOut();// 121
        }

        optionList.clear();// 123
    }// 124

    public void removeDialogOption(int slot) {
        optionList.remove(slot);// 127
    }// 128

    public void addDialogOption(String text) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text));// 131
    }// 132

    public void addDialogOption(String text, AbstractCard previewCard) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, previewCard));// 135
    }// 136

    public void addDialogOption(String text, AbstractRelic previewRelic) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, previewRelic));// 139
    }// 140

    public void addDialogOption(String text, AbstractCard previewCard, AbstractRelic previewRelic) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, previewCard, previewRelic));// 143
    }// 144

    public void addDialogOption(String text, boolean isDisabled) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, isDisabled));// 147
    }// 148

    public void addDialogOption(String text, boolean isDisabled, AbstractCard previewCard) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, isDisabled, previewCard));// 151
    }// 152

    public void addDialogOption(String text, boolean isDisabled, AbstractRelic previewRelic) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, isDisabled, previewRelic));// 155
    }// 156

    public void addDialogOption(String text, boolean isDisabled, AbstractCard previewCard, AbstractRelic previewRelic) {
        optionList.add(new LargeDialogOptionButton(optionList.size(), text, isDisabled, previewCard, previewRelic));// 159
    }// 160

    public void updateDialogOption(int slot, String text) {
        optionList.set(slot, new LargeDialogOptionButton(slot, text));// 163
    }// 164

    public void updateBodyText(String text) {
        this.updateBodyText(text, AppearEffect.BUMP_IN);// 167
    }// 168

    public void updateBodyText(String text, AppearEffect ae) {
        this.s = new Scanner(text);// 171
        this.words.clear();// 172
        this.textDone = false;// 173
        this.a_effect = ae;// 174
        this.curLineWidth = 0.0F;// 175
        this.curLine = 0;// 176
    }// 177

    public void clearRemainingOptions() {
        for(int i = optionList.size() - 1; i > 0; --i) {// 180
            optionList.remove(i);// 181
        }

    }// 183

    private void bodyTextEffectCN() {
        this.wordTimer -= Gdx.graphics.getDeltaTime();// 186
        if (this.wordTimer < 0.0F && !this.textDone) {// 187
            if (Settings.FAST_MODE) {// 188
                this.wordTimer = 0.005F;// 189
            } else {
                this.wordTimer = 0.02F;// 191
            }

            if (this.s.hasNext()) {// 194
                String word = this.s.next();// 195
                if (word.equals("NL")) {// 197
                    ++this.curLine;// 198
                    this.curLineWidth = 0.0F;// 199
                    return;// 200
                }

                WordColor color = DialogWord.identifyWordColor(word);// 203
                if (color != WordColor.DEFAULT) {// 204
                    word = word.substring(2, word.length());// 205
                }

                WordEffect effect = DialogWord.identifyWordEffect(word);// 208
                if (effect != WordEffect.NONE) {// 209
                    word = word.substring(1, word.length() - 1);// 210
                }

                for(int i = 0; i < word.length(); ++i) {// 214
                    String tmp = Character.toString(word.charAt(i));// 215
                    this.gl.setText(FontHelper.charDescFont, tmp);// 217
                    if (this.curLineWidth + this.gl.width > DIALOG_MSG_W) {// 218
                        ++this.curLine;// 219
                        this.curLineWidth = this.gl.width;// 220
                    } else {
                        this.curLineWidth += this.gl.width;// 222
                    }

                    this.words.add(new DialogWord(FontHelper.charDescFont, tmp, this.a_effect, effect, color, DIALOG_MSG_X + this.curLineWidth - this.gl.width, DIALOG_MSG_Y - LINE_SPACING * (float)this.curLine, this.curLine));// 225
                    if (!this.show) {// 236
                        ((DialogWord)this.words.get(this.words.size() - 1)).dialogFadeOut();// 237
                    }
                }
            } else {
                this.textDone = true;// 241
                this.s.close();// 242
            }
        }

    }// 245

    private void bodyTextEffect() {
        this.wordTimer -= Gdx.graphics.getDeltaTime();// 248
        if (this.wordTimer < 0.0F && !this.textDone) {// 249
            if (Settings.FAST_MODE) {// 250
                this.wordTimer = 0.005F;// 251
            } else {
                this.wordTimer = 0.02F;// 253
            }

            if (this.s.hasNext()) {// 256
                String word = this.s.next();// 257
                if (word.equals("NL")) {// 259
                    ++this.curLine;// 260
                    this.curLineWidth = 0.0F;// 261
                    return;// 262
                }

                WordColor color = DialogWord.identifyWordColor(word);// 265
                if (color != WordColor.DEFAULT) {// 266
                    word = word.substring(2, word.length());// 267
                }

                WordEffect effect = DialogWord.identifyWordEffect(word);// 270
                if (effect != WordEffect.NONE) {// 271
                    word = word.substring(1, word.length() - 1);// 272
                }

                this.gl.setText(FontHelper.charDescFont, word);// 275
                if (this.curLineWidth + this.gl.width > DIALOG_MSG_W) {// 276
                    ++this.curLine;// 277
                    this.curLineWidth = this.gl.width + CHAR_SPACING;// 278
                } else {
                    this.curLineWidth += this.gl.width + CHAR_SPACING;// 280
                }

                this.words.add(new DialogWord(FontHelper.charDescFont, word, this.a_effect, effect, color, DIALOG_MSG_X + this.curLineWidth - this.gl.width, DIALOG_MSG_Y - LINE_SPACING * (float)this.curLine, this.curLine));// 283
                if (!this.show) {// 294
                    ((DialogWord)this.words.get(this.words.size() - 1)).dialogFadeOut();// 295
                }
            } else {
                this.textDone = true;// 299
                this.s.close();// 300
            }
        }

    }// 303

    public void render(SpriteBatch sb) {
        if (!AbstractDungeon.player.isDead) {// 306
            Iterator var2 = this.words.iterator();// 307

            while(var2.hasNext()) {
                DialogWord w = (DialogWord)var2.next();
                w.render(sb, -50.0F * Settings.scale);// 308
            }

            var2 = optionList.iterator();// 311

            LargeDialogOptionButton b;
            while(var2.hasNext()) {
                b = (LargeDialogOptionButton)var2.next();
                b.render(sb);// 312
            }

            var2 = optionList.iterator();// 315

            while(var2.hasNext()) {
                b = (LargeDialogOptionButton)var2.next();
                b.renderCardPreview(sb);// 316
            }

            var2 = optionList.iterator();// 319

            while(var2.hasNext()) {
                b = (LargeDialogOptionButton)var2.next();
                b.renderRelicPreview(sb);// 320
            }
        }

    }// 323

    static {
        CHAR_SPACING = 8.0F * Settings.scale;// 41
        LINE_SPACING = 34.0F * Settings.scale;// 42
        DIALOG_MSG_X = (float)Settings.WIDTH * 0.23F;// 43
        DIALOG_MSG_Y = 250.0F * Settings.scale;// 44
        DIALOG_MSG_W = (float)Settings.WIDTH * 0.8F;// 45
        optionList = new ArrayList();// 48
        selectedOption = -1;// 49
        waitForInput = true;// 50
    }
}
