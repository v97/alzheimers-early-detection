package com.aed;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import java.util.Arrays;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class AED implements ApplicationListener {
    private Color[] colors = new Color[]{Color.WHITE, Color.YELLOW, Color.BLUE, Color.GREEN, Color.PINK};

    private CustomShapeRenderer renderer;
    private Array<Box> levelBoxes = new Array<Box>();
    private Array<Box> candidateBoxes = new Array<Box>();
    private ProgressBar bar;
    private Stage stage;
    private Stage ui;

    private int currentLevel = 0;
    private int currentSet = 1;
    private int numSets = 4;
    private float presentationDuration = 10f;
    private float trialDuration = 3f;
    private int numBoxes = 5;
    private int numAlternatives = 1;
    private Array<Boolean> correctness = new Array<Boolean>();
    private Array<Long> latencies = new Array<Long>();

    private long lastScreen = TimeUtils.millis();

    private int currentScore = 0;
    private int selectedBox = -1;
    private long lastTap = -1;

    public void setSelectedBox(Box selectedBox) {
        for (int i = 0; i < levelBoxes.size; i++)
            if (levelBoxes.get(i) == selectedBox) {
                this.selectedBox = i;
                break;
            }
        this.lastTap = TimeUtils.millis();
    }

    public Array<Boolean> getCorrectness() {
        return correctness;
    }

    public Array<Long> getLatencies() {
        return latencies;
    }

    public CustomShapeRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void create() {
        stage = new Stage(new ScalingViewport(Scaling.fillX, 480, 800));
        ui = new Stage(new ScalingViewport(Scaling.fillX, 480, 800));
        renderer = new CustomShapeRenderer();
        bar = new ProgressBar(renderer, 10);
        Gdx.input.setInputProcessor(stage);
        ui.addActor(bar);

        resetGame();
    }

    public Array<Box> generateBoxes(int count, Color[] colors) {
        Array<Box> generatedBoxes = new Array<Box>();

        Array<Box> allBoxes = new Array<Box>(levelBoxes);
        allBoxes.addAll(candidateBoxes);

        int candidateX = 0, candidateY = 0;
        for (int i = 0; i < count; i++) {
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

            Box box = new Box(this, Color.RED, candidateX, candidateY);

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

        bar.addAction(Actions.moveTo(currentLevel / (float) (numBoxes * numSets), bar.getY(), 0.2f, Interpolation.pow2InInverse));

        if (currentLevel <= numBoxes * numSets) {
            if (TimeUtils.millis() - lastScreen >= 1000 * ((currentLevel % (numBoxes + 1) == 0 ? presentationDuration : trialDuration))) {
                for (Box box : candidateBoxes) {
                    box.addAction(sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
                }
                candidateBoxes.clear();

                if (currentLevel % (numBoxes + 1) != 0) {
                    boolean correct = selectedBox != -1 && selectedBox == (currentLevel % numBoxes - 1);
                    correctness.add(correct);
                    latencies.add(lastTap == -1 || (lastTap - lastScreen) < 0 ? (long) (1000L * trialDuration) : (lastTap - lastScreen));

                    selectedBox = -1;
                    lastTap = -1;

                    System.out.println("\n" + correctness.size + " Elements.");
                    System.out.println("Correct: " + Arrays.toString(correctness.items));
                    System.out.println("Latencies: " + Arrays.toString(latencies.items));
                    System.out.println("\n");
                }

                if (currentLevel % (numBoxes + 1) == numBoxes) {
                    resetGame();
                } else {
                    for (Box box : levelBoxes) {
                        if (box == levelBoxes.get(currentLevel % numBoxes)) {
                            box.addAction(parallel(Actions.visible(true), Actions.alpha(0), sequence(delay(0.5f), Actions.fadeIn(0.5f))));
                        } else {
                            box.addAction(sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                        }
                    }

                    candidateBoxes.addAll(generateBoxes(numAlternatives, colors));
                    for (Box candidateBox : candidateBoxes) {
                        stage.addActor(candidateBox);
                        candidateBox.addAction(sequence(Actions.alpha(0), delay(0.5f), Actions.fadeIn(0.5f)));
                    }
                }

                bar.addAction(scaleTo(currentLevel / (float) (numBoxes * numSets), 1f, 0.5f, Interpolation.pow2InInverse));

                currentLevel++;
                lastScreen = TimeUtils.millis();

            }
        } else {
            System.out.println("GAME FINISHED");
        }


        float delta = Gdx.graphics.getDeltaTime();
        stage.act(delta);
        ui.act(delta);
        stage.draw();
        ui.draw();
    }

    public void resetGame() {
        for (Box box : levelBoxes) box.remove();
        levelBoxes.clear();

        levelBoxes.addAll(generateBoxes(numBoxes, null));
        for (Box box : levelBoxes) {
            stage.addActor(box);
            box.addAction(
                    parallel(
                            sequence(alpha(0), delay(0.5f), fadeIn(0.5f)),
                            repeat(5, sequence(
                                    scaleTo(1.1f, 1.1f, presentationDuration / 10),
                                    scaleTo(1f, 1f, presentationDuration / 10)
                            ))
                    ));
        }
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
