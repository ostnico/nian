package com.lolbro.nian;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class MainActivity extends SimpleBaseGameActivity {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	private static final int STEPS_PER_SECOND = 30;
	
	private static final int PLAYER_SIZE = 64;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Camera mCamera;
	
	private Scene mScene;
	
	private PhysicsWorld mPhysicsWorld;
	
	private BitmapTextureAtlas mCharactersTexture;
	private ITextureRegion mPlayerRegion;
	
	private Body mPlayerBody;
	private Sprite mPlayer;
	
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
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
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
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(255, 255, 255));
		
		this.mPhysicsWorld = new FixedStepPhysicsWorld(STEPS_PER_SECOND, new Vector2(0, 0), false, 10, 10);
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		showFPS();
		initPlayer();
		
		return this.mScene;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void initPlayer() {
		mPlayer = new Sprite(20, 20, PLAYER_SIZE, PLAYER_SIZE, this.mPlayerRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mPlayerBody.setLinearVelocity(10, 10);
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mPlayer, BodyType.DynamicBody, playerFixtureDef);
		mPlayerBody.setLinearDamping(10);
		mPlayerBody.setAngularDamping(10);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayer, mPlayerBody, true, true));
		
		this.mScene.attachChild(mPlayer);
		this.mScene.registerTouchArea(mPlayer);
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
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
}
