package com.example.box2dplay;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public final class MainLayer extends CCLayer {
    private static final float TIMESTEP = 1.0f / Android2dActivity.TARGET_FPS;
    private static final int VELOCITY_ITERATIONS = 10;
    private static final int POSITION_ITERATIONS = 10;
    private static final Vec2 DEFAULT_GRAVITY = new Vec2(0.0f, 0.0f);
    private static final boolean ALLOW_SLEEP = false;
    private static final float SCREEN_TO_WORLD_RATIO = 2000.0f;

    private static final String SMILE_FILENAME = "smile.png";
    private static final float SMILE_RADIUS = 26.0f / SCREEN_TO_WORLD_RATIO;
    private static final float SMILE_DENSITY = 0.25f;
    private static final float SMILE_FRICTION = 0.1f;
    private static final float SMILE_RESTITUTION = 0.7f;

    private static final int BODY_COUNT = 20;

    public World world;

    public MainLayer() {
        // Setup world and body
        setUpWorld();
        setUpBodies();

        // Set up layer
        setIsAccelerometerEnabled(true);

        // Schedule the physics simulation
        schedule("tick");
    }

    private void setUpWorld() {
        // Set up world
        world = new World(DEFAULT_GRAVITY, ALLOW_SLEEP);

        // Get screen corners
        CGSize size = CCDirector.sharedDirector().winSize();

        Vec2 upperLeft = screenToWorld(0, size.height);
        Vec2 upperRight = screenToWorld(size.width, size.height);
        Vec2 lowerLeft = screenToWorld(0, 0);
        Vec2 lowerRight = screenToWorld(size.width, 0);

        // Set up walls body definitions
        BodyDef leftDef = new BodyDef();
        BodyDef rightDef = new BodyDef();
        BodyDef topDef = new BodyDef();
        BodyDef bottomDef = new BodyDef();

        leftDef.position.set(lowerLeft);
        rightDef.position.set(lowerRight);
        topDef.position.set(upperLeft);
        bottomDef.position.set(lowerLeft);

        // Set up wall shapes
        PolygonShape leftShape = new PolygonShape();
        PolygonShape rightShape = new PolygonShape();
        PolygonShape topShape = new PolygonShape();
        PolygonShape bottomShape = new PolygonShape();

        leftShape.setAsEdge(lowerLeft, upperLeft);
        rightShape.setAsEdge(lowerRight, upperRight);
        topShape.setAsEdge(upperLeft, upperRight);
        bottomShape.setAsEdge(lowerLeft, lowerRight);

        // Setup up world box body
        Body boxBody = world.createBody(bottomDef);

        boxBody.createFixture(leftShape, 0.0f);
        boxBody.createFixture(rightShape, 0.0f);
        boxBody.createFixture(topShape, 0.0f);
        boxBody.createFixture(bottomShape, 0.0f);
    }

    private void setUpBodies() {
        // Get start position
        CGSize size = CCDirector.sharedDirector().winSize();
        CGPoint pos = CGPoint.make(size.width / 2, size.height / 2);

        for (int i = 0; i < BODY_COUNT; i++) {
            // Create Dynamic Body
            BodyDef bodyDef = new BodyDef();

            bodyDef.type = BodyType.DYNAMIC;
            bodyDef.position.set(screenToWorld(pos));

            final Body droidBody = world.createBody(bodyDef);

            // Create Shape
            CircleShape droidShape = new CircleShape();

            droidShape.m_radius = SMILE_RADIUS;

            // Create fixture
            FixtureDef droidFixture = new FixtureDef();

            droidFixture.shape = droidShape;
            droidFixture.density = SMILE_DENSITY;
            droidFixture.friction = SMILE_FRICTION;
            droidFixture.restitution = SMILE_RESTITUTION;

            // Assign fixture to Body
            droidBody.createFixture(droidFixture);

            // Set sprite
            final CCSprite droidSprite = CCSprite.sprite(SMILE_FILENAME);

            droidSprite.setPosition(pos);

            addChild(droidSprite, 0);

            droidBody.setUserData(droidSprite);
        }
    }

    public void tick(float dt) {
        // Update Physics World
        synchronized (world) {
            world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }

        // Update sprites
        for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
            CCSprite sprite = (CCSprite) b.getUserData();

            if (sprite != null) {
                sprite.setRotation(-(float) Math.toDegrees(b.getAngle()));
                sprite.setPosition(worldToScreen(b.getPosition()));
            }
        }
    }

    public void ccAccelerometerChanged(float x, float y, float z) {
        synchronized (world) {
            world.setGravity(new Vec2(-x, -y));
        }
    }

    private CGPoint worldToScreen(final Vec2 coord) {
        return CGPoint.make(coord.x * SCREEN_TO_WORLD_RATIO, coord.y
                * SCREEN_TO_WORLD_RATIO);
    }

    private Vec2 screenToWorld(final CGPoint coord) {
        return screenToWorld(coord.x, coord.y);
    }

    private Vec2 screenToWorld(final float x, final float y) {
        return new Vec2(x / SCREEN_TO_WORLD_RATIO, y / SCREEN_TO_WORLD_RATIO);
    }
}
