package com.aed;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Administrator on 30/04/2017.
 */
public class ImageRegion extends Image {

    boolean clip;
    int x;
    int y;
    float size;
    Texture content;

    ImageRegion(Texture texture, int x, int y, float size){
        super(texture);
        content = texture;
        this.x = x;
        this.y = y;
        this.size = size;
        clip = false;
    }

    @Override
    public void draw(Batch batch, float alpha){
        if(clip) {
            batch.draw(content, x, y, size, size + getY() - y, 0f, (y - getY())/size, 1f, 1f);
        }
        else{
            super.draw(batch, alpha);
        }
    }

    public boolean contains(float x, float y) {
        return (x <= getX() + getWidth()) && (x >= getX()) && (y <= getY() + getHeight()) && (y >= getY());
    }
}