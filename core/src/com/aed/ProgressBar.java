package com.aed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Administrator on 23/05/2017.
 */
public class ProgressBar extends Actor {

    private CustomShapeRenderer renderer;

    public ProgressBar(CustomShapeRenderer renderer, float height){
        this.renderer = renderer;
        setBounds(0, 790, 480, 10);
        setOrigin( 0, Gdx.graphics.getHeight() - height/2);
        setScaleX(0f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        renderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(0, 790, 0, 795, 480, 10, getScaleX(), 1f, 0f);
        renderer.end();
    }
}
