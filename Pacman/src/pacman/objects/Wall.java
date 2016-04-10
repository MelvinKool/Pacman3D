/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.objects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author melvin
 */
public class Wall extends Node {
    AssetManager assetManager;
    public Wall(AssetManager assetManager, float length, float width, float height){
        this.assetManager = assetManager;
        createWall(length,width,height);
    }
    
    private void createWall(float length, float width, float height){
        Box box = new Box(width,height,length);
        Geometry geom = new Geometry("Box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        this.attachChild(geom);
    }
}
