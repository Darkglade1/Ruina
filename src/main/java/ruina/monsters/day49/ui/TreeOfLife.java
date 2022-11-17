package ruina.monsters.day49.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.monsters.day49.ui.FloorENUM.*;
import static ruina.monsters.day49.ui.FloorTYPE.*;

public class TreeOfLife {

    private float renderX = 700F * Settings.scale;
    private float renderY = 300F * Settings.scale;
    private ArrayList<FloorOption> floorOptions = new ArrayList<>();
    public static final String TREE_PATH = RuinaMod.makeVfxPath("TreeOfLife.png");

    private final Texture TREE_TEXTURE = TexLoader.getTexture(TREE_PATH);
    public TreeOfLife(){ }

    public void init(){

        floorOptions.clear();
        floorOptions.add(new FloorOption(MALKUTH, ASIYAH));
        floorOptions.add(new FloorOption(YESOD, ASIYAH));
        floorOptions.add(new FloorOption(NETZACH, ASIYAH));
        floorOptions.add(new FloorOption(HOD, ASIYAH));
        floorOptions.add(new FloorOption(TIPHERETH, BRIAH));
        floorOptions.add(new FloorOption(GEBURA, BRIAH));
        floorOptions.add(new FloorOption(CHESED, BRIAH));
        floorOptions.add(new FloorOption(BINAH, ATZILUTH));
        floorOptions.add(new FloorOption(HOKMA, ATZILUTH));
        floorOptions.add(new FloorOption(KETER, FINAL));

        for(FloorOption f: floorOptions){
           f.adjustHB(renderX + f.buttonXPosition, renderY + f.buttonYPosition);
        }
    }

    public void render(SpriteBatch sb){
        sb.setColor(Color.WHITE.cpy());
        sb.draw(TREE_TEXTURE, renderX, renderY, 0.0F, 0.0F, 250.0F, 400.0F, 1.8F * Settings.scale, 1.8F * Settings.scale, 0.0F, 0, 0, 250, 400, false, false);
        for(FloorOption f: floorOptions){
            f.render(sb);
        }
    }

    public void update(){
        for(FloorOption f: floorOptions){
            f.update();
        }
    }
}
