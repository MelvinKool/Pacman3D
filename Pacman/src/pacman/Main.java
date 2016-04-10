
package pacman;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.Creatures.Ghost;
import pacman.Creatures.Pacman;
import pacman.objects.Cell;
import pacman.objects.Floor;
import pacman.objects.Wall;

/**

 */
public class Main extends SimpleApplication {
    
    public static final boolean[][] BOARD = new boolean[][] {
        { false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },
        { false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, true, false },
        { false, true, false, true, true, false, true, true, true, false, true, false, true, true, true, false, true, true, false, true, false },
        { false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false },
        { false, true, false, true, true, false, true, false, true, true, true, true, true, false, true, false, true, true, false, true, false },
        { false, true, false, false, false, false, true, false, false, false, true, false, false, false, true, false, false, false, false, true, false },
        { false, true, true, true, true, false, true, true, true, false, true, false, true, true, true, false, true, true, true, true, false },
        { false, false, false, false, true, false, true, false, false, false, false, false, false, false, true, false, true, false, false, false, false },
        { true, true, true, true, true, false, true, false, true, true, false, true, true, false, true, false, true, true, true, true, true },
        { false, false, false, false, false, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false },
        { true, true, true, true, true, false, true, false, true, true, true, true, true, false, true, false, true, true, true, true, true },
        { false, false, false, false, true, false, true, false, false, false, false, false, false, false, true, false, true, false, false, false, false },
        { false, true, true, true, true, false, true, false, true, true, true, true, true, false, true, false, true, true, true, true, false },
        { false, true, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, true, false },
        { false, true, false, true, true, false, true, true, true, false, true, false, true, true, true, false, true, true, false, true, false },
        { false, true, false, false, true, false, false, false, false, false, false, false, false, false, false, false, true, false, false, true, false },
        { false, true, true, false, true, false, true, false, true, true, true, true, true, false, true, false, true, false, true, true, false },
        { false, true, false, false, false, false, true, false, false, false, true, false, false, false, true, false, false, false, false, true, false },
        { false, true, false, true, true, true, true, true, true, false, true, false, true, true, true, true, true, true, false, true, false },
        { false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false },
        { false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false }
    };
    
    public static final float BLOCKHEIGHT = 0.5f, BLOCKLENGTH = 0.5f, BLOCKWIDTH = 0.5f;
    private boolean threeDimensional;
    private Pacman pacman;
    private List<Ghost> ghostList;
    private List<Cell> cells;
    private BitmapText hudText;
    private boolean gameRunning;
    private List<Point> cellLocations;
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(false);
        settings.setTitle("Pacman");
        settings.setSettingsDialogImage("Textures/logo_nhl.png");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        System.out.println(Main.BOARD[10][9] + " " + Main.BOARD[10][15]);
        gameRunning = true;
        unregisterFlyCam();
        initFlyCam2D();
        createFloor();
        createWalls();
        createPacman();
        createCells();
        createGhosts().start();
        initBillBoard();
        initInput();
    }
    
    @Override
    public void simpleUpdate(float tpf){
        for(int i = cells.size() - 1;i > -1; i--){
            CollisionResults result = new CollisionResults();
            BoundingVolume bv = cells.get(i).getWorldBound();
            pacman.collideWith(bv, result);
            if(result.size() > 0){
                pacman.cellsEaten++;
                rootNode.detachChild(cells.get(i));
                cells.remove(i);
            }
        }
        if(gameRunning){
            if(cells.isEmpty()){
                gameWon();
            }
            else{
                CollisionResults result = new CollisionResults();
                BoundingVolume bv = pacman.getWorldBound();
                for(Ghost ghost : ghostList)
                    ghost.collideWith(bv, result);
                if(result.size() > 0)
                    gameOver();
            }
        }
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        if(threeDimensional){
            initFlyCam3D();
        }
        updateBillBoard("Cells eaten: " + pacman.cellsEaten);
    }
    
    private void initFlyCam2D(){
        cam.setLocation(new Vector3f(10f, 28f, 10f));
        cam.setRotation(new Quaternion(0f, 0.7f, -0.7f, 0f));
        cam.setFrustumPerspective(45f, 1.5f, 1f, 19000f);
    }
    
    private void initFlyCam3D(){
        cam.setLocation(pacman.getWorldTranslation());
        cam.setRotation(pacman.getWorldRotation());
    }
    
    private void unregisterFlyCam(){
        flyCam.setMoveSpeed(30f);
        flyCam.setEnabled(false);
        flyCam.unregisterInput();
    }
    
    private void gameOver(){
        gameRunning = false;
        BitmapText gameOverText = new BitmapText(guiFont, false);          
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 10f);      // font size
        gameOverText.setColor(ColorRGBA.Red);                             // font color
        gameOverText.setText("You lost :( ");             // the text
        gameOverText.setLocalTranslation(300, gameOverText.getLineHeight(), 0); // position
        guiNode.attachChild(gameOverText);
    }
    
    private void gameWon(){
        gameRunning = false;
        BitmapText gameWonText = new BitmapText(guiFont, false);          
        gameWonText.setSize(guiFont.getCharSet().getRenderedSize() * 10f);      // font size
        gameWonText.setColor(ColorRGBA.Green);                             // font color
        gameWonText.setText("You won! :)");             // the text
        gameWonText.setLocalTranslation(300, gameWonText.getLineHeight(), 200); // position
        guiNode.attachChild(gameWonText);
    }
    
    private void createWalls(){
        for(int row = 0; row < BOARD.length;row++){
            for(int i = 0; i < BOARD[row].length;i++){
                if(BOARD[row][i]){
                    Node wall = new Wall(assetManager,Main.BLOCKLENGTH,Main.BLOCKWIDTH,Main.BLOCKHEIGHT);
                    wall.setLocalTranslation(new Vector3f(i, Main.BLOCKHEIGHT,row));
                    rootNode.attachChild(wall);
                }
            }
        }
    }
    
    private void createFloor(){
        float width = Main.BLOCKWIDTH * Main.BOARD[0].length;
        float height = Main.BLOCKHEIGHT;
        float length = Main.BLOCKLENGTH * Main.BOARD.length;
        Node floor = new Floor(assetManager,length,width,height);
        floor.setLocalTranslation(new Vector3f(width - Main.BLOCKWIDTH,-Main.BLOCKHEIGHT,length - Main.BLOCKLENGTH));
        rootNode.attachChild(floor);
    }
    
    private Thread createGhosts() {
        ghostList = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            ghostList.add(new Ghost(assetManager, pacman,cellLocations, i));
            rootNode.attachChild(ghostList.get(i));
            ghostList.get(i).setLocalTranslation(new Vector3f(18f*Main.BLOCKWIDTH+i-FastMath.floor(i/3f)*2f,Main.BLOCKHEIGHT,18f*Main.BLOCKLENGTH-FastMath.floor(i/3f)));
        }
        return new Thread(()->{
            for(int i = 0; i < 4; i++) {
                ghostList.get(i).moveToPacman();
                try {
                    Thread.sleep(5000l);
                } catch (InterruptedException ex) {
                    System.out.println("Ghost spawn interrupt");
                }
            }
        });
    }
    
    private void createPacman(){
        this.pacman = new Pacman(assetManager);
        //18,19,20
        pacman.setLocalTranslation(new Vector3f(20f*Main.BLOCKWIDTH,Main.BLOCKHEIGHT,30f*Main.BLOCKLENGTH));
        System.out.println(pacman.getLocalTranslation());
        System.out.println(pacman.getWorldTranslation());
        System.out.println("end");
        rootNode.attachChild(pacman);
    }
    
    /** Custom Keybinding: Map named actions to inputs. */
    private void initInput() {
      inputManager.addMapping("moveUp", new KeyTrigger(KeyInput.KEY_W));
      inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A));
      inputManager.addMapping("moveDown", new KeyTrigger(KeyInput.KEY_S));
      inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D));
      inputManager.addListener((ActionListener) (String name, boolean keyPressed, float tpf) -> {
          if(!keyPressed){
              movePacman(name);
          }
      }, "moveUp", "moveRight", "moveDown","moveLeft");
      inputManager.addMapping("changeDimension", new KeyTrigger(KeyInput.KEY_C));
      inputManager.addListener((ActionListener) (String name, boolean keyPressed, float tpf) -> {
          if(!keyPressed){
              changeDimension();
          }
      }, "changeDimension");
    }
    
    private void changeDimension(){
        if(threeDimensional)
            initFlyCam2D();
        else
            initFlyCam3D();
        threeDimensional = !threeDimensional;
    }
    /**
     * Calculates the given world translation to a board location
     * @param location
     * @return the board location
     */
    private Vector3f toBoardLocation(Vector3f location){
        return location;
    }
    
    private void movePacman(String keyInput){
        //for loop based on direction
        Direction moveDirection = Direction.NORTH;
        float[] angles = new float[3];
        angles = pacman.getLocalRotation().toAngles(angles);
        double yAngleDegrees = angles[1] * 180.0 / Math.PI;
        if(threeDimensional){
            if(yAngleDegrees > -180 && yAngleDegrees < -45){
                //looking to the west
                switch(keyInput){
                    case "moveLeft" :
                        moveDirection = Direction.SOUTH;
                        break;
                    case "moveDown" :
                        moveDirection = Direction.EAST;
                        break;
                }
            }
            else if(yAngleDegrees > -45 && yAngleDegrees < 45){
                //looking to the south
                switch(keyInput){
                    case "moveRight" :
                        moveDirection = Direction.WEST;
                        break;
                    case "moveLeft" :
                        moveDirection = Direction.EAST;
                        break;
                }
            }
            else if(yAngleDegrees > 45 && yAngleDegrees < 135){
                //looking to the east
                switch(keyInput){
                    case "moveRight" :
                        moveDirection = Direction.SOUTH;
                        break;
                    case "moveDown" :
                        moveDirection = Direction.WEST;
                        break;
                }
            }
            else if(yAngleDegrees > 135 && yAngleDegrees < 210){
                //looking to the north
                switch(keyInput){
                    case "moveRight" :
                        moveDirection = Direction.EAST;
                        break;
                    case "moveLeft" :
                        moveDirection = Direction.WEST;
                        break;
                    case "moveDown" :
                        moveDirection = Direction.SOUTH;
                        break;
                }
            }
//            System.out.println(pacmanAngleY);
              //if pointing to 
        }
        else{
            switch(keyInput){
                case "moveRight" :
                    moveDirection = Direction.EAST;
                    break;
                case "moveLeft" :
                    moveDirection = Direction.WEST;
                    break;
                case "moveDown" :
                    moveDirection = Direction.SOUTH;
                    break;
            }
        }
        
        Vector3f currentLocation = toBoardLocation(pacman.getWorldTranslation());
        int x = (int)Math.round(currentLocation.x), y = (int)Math.round(currentLocation.z);
        float secTime = 0;
        switch(moveDirection){
            case NORTH: 
                for(; y > 0 && !Main.BOARD[y-1][x]; y--){
                    secTime += 0.30f;
                }
                break;
            case EAST : 
               for(; x < BOARD[0].length - 1 && !Main.BOARD[y][x+1]; x++){
                    secTime += 0.30f;
                }
                break;
            case SOUTH : 
                for(; y < BOARD.length - 1 && !Main.BOARD[y+1][x]; y++){
                    secTime += 0.30f;
                }
                break;
            case WEST :
                for(; x > 0 && !Main.BOARD[y][x-1]; x--){
                    secTime += 0.30f;
                }
                break;
        }
         pacman.moveToWaypoint(new Vector3f(x, Main.BLOCKHEIGHT, y), secTime);
    }
    
    private void createCells(){
        cells = new ArrayList<Cell>();
        cellLocations = new ArrayList<Point>();
        for(int y = 1; y < Main.BOARD.length / 3;y++){
            for(int x = 1; x < Main.BOARD[y].length - 1;x++){
                if(!Main.BOARD[y][x]){
                    Cell cellNode = new Cell(assetManager,cells.size());
                    Vector3f vect = new Vector3f(x,Main.BLOCKHEIGHT / 2,y);
                    cellNode.setLocalTranslation(vect);
                    rootNode.attachChild(cellNode);
                    cells.add(cellNode);
                    cellLocations.add(new Point((int)vect.x, (int)vect.z));
                }
            }
        }
        for(int y = Main.BOARD.length / 3; y < Main.BOARD.length / 3 * 2 - 1;y++){
            for(int x = Main.BOARD[y].length / 4; x < Main.BOARD[y].length / 4 * 3 + 1;x++){
                if(!Main.BOARD[y][x]){
                    Cell cellNode = new Cell(assetManager,cells.size());
                    Vector3f vect = new Vector3f(x,Main.BLOCKHEIGHT / 2,y);
                    cellNode.setLocalTranslation(vect);
                    rootNode.attachChild(cellNode);
                    cells.add(cellNode);
                    cellLocations.add(new Point((int)vect.x, (int)vect.z));
                }
            }
        }
        for(int y = Main.BOARD.length / 3 * 2 - 1; y < Main.BOARD.length;y++){
            for(int x = 1; x < Main.BOARD[y].length - 1;x++){
                if(!Main.BOARD[y][x]){
                    Cell cellNode = new Cell(assetManager,cells.size());
                    Vector3f vect = new Vector3f(x,Main.BLOCKHEIGHT / 2,y);
                    cellNode.setLocalTranslation(vect);
                    rootNode.attachChild(cellNode);
                    cells.add(cellNode);
                    cellLocations.add(new Point((int)vect.x, (int)vect.z));
                }
            }
        }
    }

    private void initBillBoard() {
        this.hudText = new BitmapText(guiFont, false);          
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText("Cells eaten: " + pacman.cellsEaten);             // the text
        hudText.setLocalTranslation(300, hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }
    
    private void updateBillBoard(String text){
        hudText.setText(text);
    }
}
