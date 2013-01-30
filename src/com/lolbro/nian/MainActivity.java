package com.lolbro.nian;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lolbro.nian.customs.SwipeScene;
import com.lolbro.nian.customs.SwipeScene.SwipeListener;


public class MainActivity extends SimpleBaseGameActivity implements SwipeListener, IUpdateHandler {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	private static final int STEPS_PER_SECOND = 60;
	private static final int MAX_STEPS_PER_UPDATE = 1;
	
	private static final int PLAYER_SIZE = 64;

	public static final int JUMP_UP = SwipeListener.DIRECTION_UP;
	public static final int JUMP_DOWN = SwipeListener.DIRECTION_DOWN;
	public static final int JUMP_LEFT = SwipeListener.DIRECTION_LEFT;
	public static final int JUMP_RIGHT = SwipeListener.DIRECTION_RIGHT;
	
	public static final Vector2 PLAYER_HOME_POSITION = new Vector2(CAMERA_WIDTH/2, -CAMERA_HEIGHT/2 + PLAYER_SIZE*2);
	
	public static final Vector2 PLAYER_SPRITE_SPAWN = new Vector2(PLAYER_HOME_POSITION.x - PLAYER_SIZE/2, PLAYER_HOME_POSITION.y -PLAYER_SIZE/2);

	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private SmoothCamera mCamera;
	
	private SwipeScene mScene;
	
	private PhysicsWorld mPhysicsWorld;
	
	private BitmapTextureAtlas mCharactersTexture;
	private ITextureRegion mPlayerRegion;
	
	private Body mPlayerBody;
	private Sprite mPlayerSprite;
	private Body mObstacleBody;
	private Sprite mObstacleSprite;
	
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean moveUp = false;
	private boolean moveDown = false;
	
	private short rollCounter = 0;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new SmoothCamera(0, -CAMERA_HEIGHT, CAMERA_WIDTH, CAMERA_HEIGHT, 10 * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 10 * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 10);
		
		//new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT)
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
	}
	
	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mCharactersTexture = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mPlayerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCharactersTexture, this, "player.png", 0, 0);
		this.mCharactersTexture.load();
		
	}
	
	@Override
	protected Scene onCreateScene() {
		
		this.mScene = new SwipeScene();
		
		this.mScene.registerUpdateHandler(this);
		
		this.mPhysicsWorld = new FixedStepPhysicsWorld(STEPS_PER_SECOND, MAX_STEPS_PER_UPDATE, new Vector2(0, 0), false, 10, 10);
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		initBackground();
		initPlayer();
		initObstacle();
//		showFPS();

		return this.mScene;
	}
	
	// ===========================================================
	// On update
	// ===========================================================
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		
		if (moveUp == true) {
			if (rollCounter < 7) {
				rollCounter++;
				mPlayerBody.setLinearVelocity(0, -40);
			} else {
				moveUp = false;
				rollCounter = 0;
				mPlayerBody.setLinearVelocity(0, 0);
			}
		} else if (moveDown == true) {
			if (rollCounter < 7) {
				rollCounter++;
				mPlayerBody.setLinearVelocity(0, 40);
			} else {
				moveDown = false;
				rollCounter = 0;
				mPlayerBody.setLinearVelocity(0, 0);
			}
		} else if (moveLeft == true) {
			if (rollCounter < 7) {
				rollCounter++;
				mPlayerBody.setLinearVelocity(-40, 0);
			} else {
				moveLeft = false;
				rollCounter = 0;
				mPlayerBody.setLinearVelocity(0, 0);
			}
		} else if (moveRight == true) {
			if (rollCounter < 7) {
				rollCounter++;
				mPlayerBody.setLinearVelocity(40, 0);
			} else {
				moveRight = false;
				rollCounter = 0;
				mPlayerBody.setLinearVelocity(0, 0);
			}
		}
				
	}
	
	@Override
	public synchronized void onResumeGame() {
		super.onResumeGame();
		mScene.registerForGestureDetection(this, this);
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public void onSwipe(int direction) {
		jump(direction);	
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void initBackground() {
		this.mScene.setBackground(new RepeatingSpriteBackground((float)CAMERA_WIDTH, (float)CAMERA_HEIGHT, getTextureManager(), AssetBitmapTextureAtlasSource.create(getAssets(), "gfx/floor_1.png"), this.getVertexBufferObjectManager()));
	}
	
	private void initObstacle() {
		mObstacleSprite = new Sprite(CAMERA_WIDTH/2-PLAYER_SIZE/2, -CAMERA_HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE, this.mPlayerRegion, this.getVertexBufferObjectManager());
		
		final FixtureDef obstacleFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		mObstacleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mObstacleSprite, BodyType.DynamicBody, obstacleFixtureDef);
		mObstacleBody.setFixedRotation(true);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mObstacleSprite, mObstacleBody, true, true));
		
		this.mScene.attachChild(mObstacleSprite);
	}
	
	private void initPlayer() {
		mPlayerSprite = new Sprite(PLAYER_SPRITE_SPAWN.x, PLAYER_SPRITE_SPAWN.y, PLAYER_SIZE, PLAYER_SIZE, this.mPlayerRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mObstacleBody.setLinearVelocity(0, 5);
//				mPlayerBody.setLinearVelocity(0, -10);
//				mCamera.setCenter(0, -2000);
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mPlayerSprite, BodyType.DynamicBody, playerFixtureDef);
		mPlayerBody.setFixedRotation(true);
//		mPlayerBody.setLinearDamping(10);
//		mPlayerBody.setAngularDamping(10);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayerSprite, mPlayerBody, true, true));
		
		this.mScene.attachChild(mPlayerSprite);
		this.mScene.registerTouchArea(mPlayerSprite);
	}
	
	/* Methods for debugging */
//	private void showFPS() {
//		final FPSCounter fpsCounter = new FPSCounter();
//		this.mEngine.registerUpdateHandler(fpsCounter);
//		HUD hud=new HUD();
//		Font font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
//		font.load();
//		final Text text = new Text(10, 10, font, "FPS: ", 20, new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
//		hud.attachChild(text);
//		mCamera.setHUD(hud);
//		mScene.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
//		                @Override
//		                public void onTimePassed(final TimerHandler pTimerHandler) {
//		                         text.setText("FPS: " + fpsCounter.getFPS());
//		        }
//		}));
//	}
	
	/* Methods for moving */
	
	private void jump(int direction){
		if(isMoving()){
			return;
		}
		
		switch(direction){
		case JUMP_UP:
			if ((int)mPlayerBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT >= PLAYER_HOME_POSITION.y) {
				moveUp = true;
			} break;
		case JUMP_DOWN:
			if ((int)mPlayerBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT <= PLAYER_HOME_POSITION.y) {
				moveDown = true;
			} break;
		case JUMP_LEFT:
			if ((int)mPlayerBody.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT >= PLAYER_HOME_POSITION.x) {
				moveLeft = true;
			} break;
		case JUMP_RIGHT:
			if ((int)mPlayerBody.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT <= PLAYER_HOME_POSITION.x) {
				moveRight = true;
			} break;
		}
	}
	
	private boolean isMoving() {
		return (moveLeft == true || moveRight == true || moveUp == true || moveDown == true);
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
}
