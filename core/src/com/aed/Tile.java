package com.aed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by Administrator on 30/04/2017.
 */
public class Tile{
    private int itemID;
    private ImageRegion frame;
    private ImageRegion panel;
    private ImageRegion item;
    private int x;
    private int y;
    private int itemX;
    private int itemY;
    private float scale;
    private static int setID;
    private static int numTiles;
    private static int numColumns;
    private static int numRows;
    private static float gapProportion = 0.2f;
    private static float tileLength;
    private static float gapLength;
    private static float screenWidth;
    private static float screenHeight;
    private static float picScale = 0.8f;
    private static float testingTileSize = 1.5f;
    private static float scaleFactor;
    private static float xOffset = 0;
    private static boolean done = false;
    private static Tile testingTile;
    private static ArrayList<Integer> order;
    private static ArrayList<Tile> tiles;

    Tile(int itemID, int x, int y, float scale){
        this.x = x;
        this.y = y;
        this.itemID = itemID;
        this.scale = scale;
        itemX = (int) (x + ((1.0f - picScale)/2)*scale*tileLength);
        itemY = (int) (y + ((1.0f - picScale)/2)*scale*tileLength);
        frame = new ImageRegion(new Texture(Gdx.files.internal("frame.png")), x, y, tileLength*scale);
        frame.setBounds(x, y, tileLength*scale, tileLength*scale);
        panel = new ImageRegion(new Texture(Gdx.files.internal("panel.png")), x, y, tileLength*scale);
        panel.clip = true;
        panel.setBounds(x, y - scale*tileLength, scale*tileLength, scale*tileLength);
        item = new ImageRegion(new Texture(Gdx.files.internal("shape_" + Integer.toString(itemID) + ".png")), itemX, itemY, tileLength*scale);
        item.setBounds(itemX, itemY, tileLength*scale*picScale, tileLength*scale*picScale);
    }

    public static int init(int nT, int sID){
        numTiles = nT;
        setID = sID;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        numColumns = (int) Math.sqrt((double) numTiles);
        numRows = numTiles/numColumns;
        while (!isInteger(((double) numTiles) / ((double) numColumns))) numColumns--;
        tileLength = (int) (screenWidth/((float) numColumns * (1.0f + gapProportion) + gapProportion));
        gapLength = (int) (tileLength * gapProportion);
        scaleFactor = screenHeight/((gapLength + tileLength)*numRows + 2*gapLength + testingTileSize*tileLength);
        if(scaleFactor > 1){
            scaleFactor = 1;
        }
        else{
            xOffset = screenWidth*(1-scaleFactor)/2;
        }
        order = new ArrayList<Integer>();
        for (int i = 0; i < numTiles; i++) order.add(i);
        Collections.shuffle(order);
        tiles = new ArrayList<Tile>();
        for (int i = 0; i < order.size(); i++) {
            tiles.add(new Tile(order.get(i), (int) (xOffset + scaleFactor*(gapLength + (tileLength + gapLength) * (i%numColumns))), (int) (scaleFactor*(gapLength + (tileLength + gapLength) * (i/numColumns))), scaleFactor));
            tiles.get(i).appear();
            final int temp = i;
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    tiles.get(temp).slide(true, 2f);
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            testingTile.slide(false, 1f);
                        }
                    }, 3);
                }
            }, 5);
        }
        testingTile = new Tile(order.get((int) Math.random() * numTiles), (int) ((screenWidth - testingTileSize*tileLength*scaleFactor)/2f), (int) (screenHeight - scaleFactor*(gapLength + testingTileSize*tileLength)), scaleFactor*testingTileSize);
        testingTile.fade();
        testingTile.slide(true, 0f);
        return testingTile.itemID;
    }

    public static void drawAll(SpriteBatch batch, float delta){
        for(Tile tile : tiles){
            tile.draw(batch, delta);
        }
        testingTile.draw(batch, delta);
    }

    public void draw(SpriteBatch batch, float delta){
            item.act(delta);
            panel.act(delta);
            frame.act(delta);
            item.draw(batch, 1f);
            panel.draw(batch, 1f);
            frame.draw(batch, 1f);
    }

    public void appear(){
        item.addAction(sequence(alpha(0), scaleTo(.1f, .1f),
                parallel(fadeIn(2f, Interpolation.pow2),
                        scaleTo(1f, 1f, 2.5f, Interpolation.pow5),
                        moveTo(itemX, itemY, 2f, Interpolation.swing))));
        frame.addAction(sequence(alpha(0), scaleTo(.1f, .1f),
                parallel(fadeIn(2f, Interpolation.pow2),
                        scaleTo(1f, 1f, 2.5f, Interpolation.pow5),
                        moveTo(x, y, 2f, Interpolation.swing))));
        panel.addAction(moveTo(x, y - panel.getHeight()));
    }

    public void fade(){
        item.addAction(fadeIn(2f, Interpolation.pow2));
        frame.addAction(fadeIn(2f, Interpolation.pow2));
    }

    public void onTap(){
        frame.addAction(sequence(scaleTo(1.1f, 1.1f, 0.1f), scaleTo(1f, 1f, 0.1f)));
        panel.addAction(sequence(scaleTo(1.1f, 1.1f, 0.1f), scaleTo(1f, 1f, 0.1f)));
        item.addAction(sequence(scaleTo(1.1f, 1.1f, 0.1f), scaleTo(1f, 1f, 0.1f)));
    }

    public void glow(final Color c){
        slide(false, 1f);
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                frame.addAction(sequence(color(c, 0.1f),color(Color.WHITE, 0.1f)));
                item.addAction(sequence(color(c, 0.1f),color(Color.WHITE, 0.1f)));
                Timer.schedule(new Timer.Task(){
                    @Override
                    public void run() {
                        slide(true, 1f);
                    }
                }, 0.2f);
            }
        }, 1);
    }

    public static int verify(float x, float y){
        if(!done) {
            for (Tile tile : tiles) {
                if (tile.item.contains(x, y)) {
                    tile.onTap();
                    tile.glow(tile.itemID == testingTile.itemID ? Color.GREEN : Color.RED);
                    testingTile.glow(tile.itemID == testingTile.itemID ? Color.GREEN : Color.RED);
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            disappear();
                        }
                    }, 2);
                    return tile.itemID;
                }
            }
            done = true;
        }
        return -1;
    }

    private void slide(boolean close, float duration) {
        panel.addAction(parallel(alpha(1f, 0), moveTo(x, close ? y : y - scale*tileLength, duration, Interpolation.pow2)));
    }

    public static void disappear(){
        for(Tile tile : tiles){
            tile.leave();
        }
        testingTile.leave();
    }

    public void leave(){
        panel.clip = false;
        item.addAction(moveTo(-screenWidth, screenHeight/2, 1f, Interpolation.pow2));
        panel.addAction(moveTo(-screenWidth, screenHeight/2, 1f, Interpolation.pow2));
        frame.addAction(moveTo(-screenWidth, screenHeight/2, 1f, Interpolation.pow2));
    }

    public static boolean isInteger(double x){
        return x == (int) x;
    }
}