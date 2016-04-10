/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.Creatures;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.awt.Point;
import pacman.Main;
import pacman.astar.AStar;

/**
 *
 * @author melvin
 */
public abstract class Creature extends Node implements MotionPathListener{
    AssetManager assetManager;
    AStar astar;
    public Creature(AssetManager assetManager){
        this.assetManager = assetManager;
        this.astar = new AStar(Main.BOARD);
    }
    
    protected abstract void createCreature();
    
    public abstract void moveToWaypoint(Vector3f location, float secTime);
}
