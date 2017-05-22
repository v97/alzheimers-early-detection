package com.aed;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class AED implements ApplicationListener {
    private Color[] colors = new Color[]{Color.WHITE, Color.YELLOW, Color.BLUE, Color.GREEN, Color.PINK};

    private CustomShapeRenderer renderer;
    private Array<Box> levelBoxes = new Array<Box>();
    private Array<Box> candidateBoxes = new Array<Box>();
    private Stage stage;

    private int currentLevel = 0;
    private int currentSet = 1;

    private long lastScreen = 0;

    @Override
    public void create() {
        stage = new Stage(new ScalingViewport(Scaling.fillX, 480, 800));
        renderer = new CustomShapeRenderer();

        Gdx.input.setInputProcessor(stage);
    }

    public Array<Box> generateBoxes(int numBoxes, Color[] colors) {
        Array<Box> generatedBoxes = new Array<Box>();

        Array<Box> allBoxes = new Array<Box>(levelBoxes);
        allBoxes.addAll(candidateBoxes);

        int candidateX = 0, candidateY = 0;
        for (int i = 0; i < numBoxes; i++) {
            boolean unique = false;
            while (!unique) {
                candidateX = MathUtils.random(0, 480 - Box.SHAPE_WIDTH);
                candidateY = MathUtils.random(0, 800 - Box.SHAPE_HEIGHT);

                Rectangle candidateRectangle = new Rectangle(candidateX, candidateY, Box.SHAPE_WIDTH + 20f, Box.SHAPE_HEIGHT + 20f);

                unique = true;
                for (Box box : allBoxes) {
                    if (candidateRectangle.overlaps(new Rectangle(box.getX(), box.getY(), box.getWidth(), box.getHeight()))) {
                        unique = false;
                        break;
                    }
                }
            }

            Box box = new Box(renderer, colors == null ? Color.RED : colors[i % colors.length], candidateX, candidateY);

            generatedBoxes.add(box);
            allBoxes.add(box);
        }
        return generatedBoxes;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(stage.getBatch().getProjectionMatrix());
        renderer.setTransformMatrix(stage.getBatch().getTransformMatrix());
        if(currentSet <= 3) {
            if (TimeUtils.millis() - lastScreen >= (currentLevel == 0 ? 5000 : 3000)) {
                for (Box box : candidateBoxes) {
                    box.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
                }
                candidateBoxes.clear();

                if (currentLevel > 0) {
                    for (Box box : levelBoxes) {
                        if (box == levelBoxes.get(currentLevel - 1)) {
                            box.addAction(Actions.parallel(Actions.visible(true), Actions.alpha(0), Actions.sequence(Actions.delay(0.5f), Actions.fadeIn(0.5f))));
                        } else {
                            box.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                        }
                    }

                    candidateBoxes.addAll(generateBoxes(5, colors));
                    for (Box candidateBox : candidateBoxes) {
                        stage.addActor(candidateBox);
                        candidateBox.addAction(Actions.sequence(Actions.alpha(0), Actions.delay(0.5f), Actions.fadeIn(0.5f)));
                    }
                } else {
                    for (Box box : levelBoxes) box.remove();
                    levelBoxes.clear();

                    levelBoxes.addAll(generateBoxes(5, null));
                    for (Box box : levelBoxes) {
                        stage.addActor(box);
                        box.addAction(Actions.sequence(Actions.alpha(0), Actions.delay(0.5f), Actions.fadeIn(0.5f)));
                    }
                }
                currentLevel = (currentLevel + 1) % (levelBoxes.size + 1);
                currentSet += (currentLevel == 0) ? 1 : 0;
                lastScreen = TimeUtils.millis();
            }
        }
        else{
            //transitionscreen;
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        renderer.dispose();
    }
}
