package com.lolbro.nian;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSCounter;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lolbro.nian.customs.SwipeScene;
import com.lolbro.nian.customs.SwipeScene.SwipeListener;


public class MainActivity extends SimpleBaseGameActivity implements SwipeListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	private static final int STEPS_PER_SECOND = 60;
	

	private static final int PLAYER_SIZE = 64;

	public static final int JUMP_UP = 1;
	public static final int JUMP_DOWN = 2;
	public static final int JUMP_LEFT = 3;
	public static final int JUMP_RIGHT = 4;

	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private SmoothCamera mCamera;
	
	private Scene mScene;
	
	private PhysicsWorld mPhysicsWorld;
	
	private BitmapTextureAtlas mCharactersTexture;
	private ITextureRegion mPlayerRegion;
	
	private Body mPlayerBody;
	private Sprite mPlayerSprite;
	
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
//		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 20, 20, 10);
		
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
		
		this.mPhysicsWorld = new FixedStepPhysicsWorld(STEPS_PER_SECOND, new Vector2(0, 0), false, 10, 10);
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		showFPS();
		initBackground();
		initPlayer();
		
		return this.mScene;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void initBackground() {
		this.mScene.setBackground(new RepeatingSpriteBackground((float)CAMERA_WIDTH, (float)CAMERA_HEIGHT, getTextureManager(), AssetBitmapTextureAtlasSource.create(getAssets(), "gfx/floor_1.png"), this.getVertexBufferObjectManager()));
	}
	
	private void initPlayer() {
		mPlayerSprite = new Sprite(20, 20, PLAYER_SIZE, PLAYER_SIZE, this.mPlayerRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
//				mPlayerBody.setLinearVelocity(0, -10);
				mCamera.setCenter(-100, 0);
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mPlayerSprite, BodyType.DynamicBody, playerFixtureDef);
//		mPlayerBody.setLinearDamping(10);

		mPlayerBody.setAngularDamping(10);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayerSprite, mPlayerBody, true, true));
		
		this.mScene.attachChild(mPlayerSprite);
		this.mScene.registerTouchArea(mPlayerSprite);
	}
	
	private void showFPS() {
		final FPSCounter fpsCounter = new FPSCounter();
		this.mEngine.registerUpdateHandler(fpsCounter);
		HUD hud=new HUD();
		Font font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		font.load();
		final Text text = new Text(10, 10, font, "FPS: ", 20, new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		hud.attachChild(text);
		mCamera.setHUD(hud);
		mScene.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
		                @Override
		                public void onTimePassed(final TimerHandler pTimerHandler) {
		                         text.setText("FPS: " + fpsCounter.getFPS());
		        }
		}));
	}
	
	private void jump(int direction){
		switch(direction){
		case JUMP_UP:
			mPlayerBody.setLinearVelocity(mPlayerBody.getLinearVelocity().x, -25);
			break;
		case JUMP_DOWN:
			mPlayerBody.setLinearVelocity(mPlayerBody.getLinearVelocity().x, 20);
			break;
		case JUMP_LEFT:
			mPlayerBody.setLinearVelocity(-3.5f, mPlayerBody.getLinearVelocity().y);
			break;
		case JUMP_RIGHT:
			mPlayerBody.setLinearVelocity(3.5f, mPlayerBody.getLinearVelocity().y);
			break;
		}
	}
	
	@Override
	public void onSwipe(int direction) {
		
		switch(direction){
		case SwipeListener.DIRECTION_UP:			
			jump(JUMP_UP);
			break;
		case SwipeListener.DIRECTION_LEFT:
			jump(JUMP_LEFT);
			break;
		case SwipeListener.DIRECTION_RIGHT:
			jump(JUMP_RIGHT);
			break;
		case SwipeListener.DIRECTION_DOWN:
			jump(JUMP_DOWN);
			break;
		}
		
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
}
