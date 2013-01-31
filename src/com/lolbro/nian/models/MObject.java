package com.lolbro.nian.models;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class MObject {

	public static final int SHAPE_BOX = 1;
	public static final int SHAPE_CIRCLE = 2;
	
	private Body body;
	private Sprite sprite;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 */
	public MObject(float x, float y, int width, int height, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld){
		this(x, y, width, height, textureRegion, vertexBufferObjectManager, physicsWorld, SHAPE_BOX);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 * @param shape
	 */
	public MObject(float x, float y, int width, int height, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld, int shape){
		this(x, y, width, height, textureRegion, vertexBufferObjectManager, physicsWorld, shape, PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f));
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 * @param shape
	 * @param fixtureDef
	 */
	public MObject(float x, float y, int width, int height, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld, int shape, FixtureDef fixtureDef){
		sprite = new Sprite(x, y, width, height, textureRegion, vertexBufferObjectManager);
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		switch(shape){
		case SHAPE_BOX:
			body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.DynamicBody, playerFixtureDef);
			break;
		case SHAPE_CIRCLE:
			body = PhysicsFactory.createCircleBody(physicsWorld, sprite, BodyType.DynamicBody, playerFixtureDef);
			break;
		}
		body.setFixedRotation(true);
	}
	
	// FOR ANIMATED SPRITES
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 */
	public MObject(float x, float y, ITiledTextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld){
		this(x, y, textureRegion, vertexBufferObjectManager, physicsWorld, SHAPE_BOX);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 * @param shape
	 */
	public MObject(float x, float y, ITiledTextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld, int shape){
		this(x, y, textureRegion, vertexBufferObjectManager, physicsWorld, shape, PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f));
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureRegion
	 * @param vertexBufferObjectManager
	 * @param physicsWorld
	 * @param shape
	 * @param fixtureDef
	 */
	public MObject(float x, float y, ITiledTextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, PhysicsWorld physicsWorld, int shape, FixtureDef fixtureDef){
		AnimatedSprite sprite = new AnimatedSprite(x, y, textureRegion, vertexBufferObjectManager);
		sprite.animate(30);
		sprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.sprite = sprite;
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		
		switch(shape){
		case SHAPE_BOX:
			body = PhysicsFactory.createBoxBody(physicsWorld, sprite, BodyType.DynamicBody, playerFixtureDef);
			break;
		case SHAPE_CIRCLE:
			body = PhysicsFactory.createCircleBody(physicsWorld, sprite, BodyType.DynamicBody, playerFixtureDef);
			break;
		}
		body.setFixedRotation(true);
	}
	
	public Body getBody() {
		return body;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * 
	 * @param worldPosition true for world position. false for scene position
	 * @return 
	 */
	public Vector2 getBodyPosition(boolean worldPosition) {
		Vector2 position = body.getPosition();
		if(worldPosition == false){
			position.set(position.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, position.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		}
		return position;
	}
	
	/**
	 * 
	 * @param worldPosition true for world position. false for scene position
	 * @return 
	 */
	public float getBodyPositionX(boolean worldPosition) {
		float x = body.getPosition().x;
		if(worldPosition == false){
			x *= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		}
		return x;
	}
	
	/**
	 * 
	 * @param worldPosition true for world position. false for scene position
	 * @return 
	 */
	public float getBodyPositionY(boolean worldPosition) {
		float y = body.getPosition().y;
		if(worldPosition == false){
			y *= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		}
		return y;
	}
	
	public float getSpritePositionX() {
		return sprite.getX();
	}
	
	public float getSpritePositionY() {
		return sprite.getY();
	}
}
