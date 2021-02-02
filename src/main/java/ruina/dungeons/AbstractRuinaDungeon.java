package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import ruina.RuinaMod;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.scenes.RuinaScene;

import java.util.ArrayList;

public class AbstractRuinaDungeon extends CustomDungeon {
    public Floor floor;

    public AbstractRuinaDungeon(String NAME, String ID, String event, boolean genericEvents, int weak, int strong, int elite) {
        super(NAME, ID, event, genericEvents, weak, strong, elite);
        setFloor();
    }

    public AbstractRuinaDungeon(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
        setFloor();
    }

    public AbstractRuinaDungeon(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
        setFloor();
    }

    public enum Floor {
        TIPHERETH,
        GEBURA,
        CHESED
    }

    public void setFloor() {
        System.out.println("bosskey: " + bossKey);
        if (bossKey != null) {
            if (bossKey.equals(JesterOfNihil.ID)) {
                floor = Floor.TIPHERETH;
            } else if (bossKey.equals(EncounterIDs.RED_AND_WOLF)) {
                floor = Floor.GEBURA;
            } else {
                floor = Floor.CHESED;
            }
            setMusic();
            AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
            System.out.println(floor.toString());
        }
    }

    protected void setMusic() {
        System.out.println("hello");
        if (floor != null) {
            switch (floor) {
                case TIPHERETH:
                    this.setMainMusic(RuinaMod.makeMusicPath("Tiphereth2.ogg"));
                    break;
                case GEBURA:
                    System.out.println("FUCK");
                    this.setMainMusic(RuinaMod.makeMusicPath("Gebura2.ogg"));
                    break;
                case CHESED:
                    this.setMainMusic(RuinaMod.makeMusicPath("Chesed2.ogg"));
                    break;
                default:
                    this.setMainMusic(RuinaMod.makeMusicPath("Gebura2.ogg"));
                    break;
            }
        }
    }

    @Override
    public AbstractScene DungeonScene() {
        return new RuinaScene();
    }

    @Override
    protected void initializeShrineList() {
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in receivePostInitialize()
    }
}