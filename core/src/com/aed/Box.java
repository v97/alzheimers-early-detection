package com.aed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Administrator on 23/05/2017.
 */
public class Box extends Actor {
    public static final int SHAPE_WIDTH = 100;
    public static final int SHAPE_HEIGHT = 100;

    private AED game;
    private CustomShapeRenderer renderer;

    public Box(final AED game, final Color color, int x, int y) {
        this.game = game;
        this.renderer = game.getRenderer();

        super.setColor(color);

        super.setBounds(x, y, SHAPE_WIDTH, SHAPE_HEIGHT);
        super.setOrigin(Align.center);
        super.setTouchable(Touchable.enabled);

//        addAction(sequence(moveTo((float) (x + (2 * Math.random() - 1) * getWidth() * 5), (float) (y + (2 * Math.random() - 1) * getHeight() * 5)), delay(1), alpha(0), scaleTo(.1f, .1f),
//                parallel(fadeIn(2f, Interpolation.pow2),
//                        scaleTo(1f, 1f, 2.5f, Interpolation.pow5),
//                        moveTo(x, y, 2f, Interpolation.swing))));

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("I AM COLOR " + color.toString());
                Box.this.addAction(Actions.sequence(
                        Actions.parallel(Actions.scaleTo(1.2f, 1.2f, 0.05f), Actions.rotateBy(-10f, 0.05f, Interpolation.bounce)),
                        Actions.parallel(Actions.scaleTo(1f, 1f, 0.05f), Actions.rotateBy(10f, 0.05f, Interpolation.bounce))
                ));
                game.setSelectedBox(Box.this);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setColor(getColor());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        //renderer.roundedRect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), getHeight()/4);
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}