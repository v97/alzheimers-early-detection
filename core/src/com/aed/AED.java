package com.aed;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

public class AED extends ApplicationAdapter implements GestureDetector.GestureListener{

	private float delta;
	private SpriteBatch batch;
	private Sprite mapSprite;

	ArrayList<Tile> tiles;
	int numTiles = 6;
	float stateTime = 0;
	int numRepsinSet;
	int correctResponse[];
	int userResponse[];
	int currentSet = 0;
	float timeTaken[];

	private GestureDetector gd;

	ProgressBar bar;
	Skin skin;
	Image test;

	@Override
	public void create () {
		//skin = new Skin(Gdx.files.internal("neon-ui.json"));
		//bar = new ProgressBar(0f, 100f, 1f, false, skin);
		//bar.setBounds(0, screenHeight - 100, screenWidth, 100);
		//bar.setValue(50);
		batch = new SpriteBatch();
		correctResponse = new int[15];
		correctResponse[currentSet] = Tile.init(numTiles, 1);
		gd = new GestureDetector(this);
		Gdx.input.setInputProcessor(gd);
		userResponse = new int[15];

		//a = new Animation(1 / 30f, new Array<Integer>());
		//cam = new OrthographicCamera(x, y);
		//cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		//cam.update();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		delta = Gdx.graphics.getDeltaTime();
		batch.begin();
		Tile.drawAll(batch, delta);
		//bar.draw(batch, 1);
		//stateTime += Gdx.graphics.getDeltaTime();
		//System.out.println(a.getKeyFrame(stateTime));
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		userResponse[currentSet] = Tile.verify(x,Gdx.graphics.getHeight() - y);
		if(userResponse[currentSet] != -1){
			/*correctResponse[++currentSet] = Tile.init(9, 1);
			if((numTiles = numTiles + 3) == 21){
				numTiles = 6;
			}*/
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {

	}
}
