package scene;

import java.awt.FileDialog;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import blister.GameScreen;
import blister.GameTime;
import blister.ScreenState;
import blister.input.KeyboardEventDispatcher;
import blister.input.KeyboardKeyEventArgs;
import blister.input.MouseButton;
import blister.input.MouseButtonEventArgs;
import blister.input.MouseEventDispatcher;
import blister.input.MouseMoveEventArgs;
import anim.AnimTimeline;
import anim.Animator;
import anim.TimelineViewer;
import common.Scene;
import common.event.SceneReloadEvent;
import gl.CameraController;
import gl.GridRenderer;
import gl.RenderCamera;
import gl.RenderController;
import gl.RenderObject;
import gl.Renderer;
import gl.manip.ManipController;
import scene.form.RPMaterialData;
import scene.form.RPMeshData;
import scene.form.RPTextureData;
import scene.form.ScenePanel;
import egl.GLError;
import egl.math.Vector2;
import egl.math.Vector3;
import ext.csharp.ACEventFunc;
import ext.java.Parser;

public class ViewScreen extends GameScreen {
    Renderer renderer = new Renderer();
    int cameraIndex = 0;
    boolean pick;
    int prevCamScroll = 0;
    boolean wasPickPressedLast = false;
    boolean showGrid = true;
    boolean useTimelineMouseOver = false;

    SceneApp app;
    ScenePanel sceneTree;
    RPMeshData dataMesh;
    RPMaterialData dataMaterial;
    RPTextureData dataTexture;

    RenderController rController;
    CameraController camController;
    ManipController manipController;
    GridRenderer gridRenderer;
    boolean updateAnimation;
    Animator animator;
    TimelineViewer animTimeViewer = new TimelineViewer();

    @Override
    public int getNext() {
        return getIndex();
    }
    @Override
    protected void setNext(int next) {
    }

    @Override
    public int getPrevious() {
        return -1;
    }
    @Override
    protected void setPrevious(int previous) {
    }

    @Override
    public void build() {
        app = (SceneApp)game;

        renderer = new Renderer();
    }
    @Override
    public void destroy(GameTime gameTime) {
    }

    private final ACEventFunc<MouseButtonEventArgs> onMousePress = new ACEventFunc<MouseButtonEventArgs> () {
        @Override
        public void receive(Object sender, MouseButtonEventArgs args) {
            // System.err.println(args);
            // new Exception().printStackTrace();

            // calculate which frame mouse is on
            int frame = -1;
            frame = animTimeViewer.onTimeline(game.getWidth(), game.getHeight(),rController.animEngine, Mouse.getX(), Mouse.getY(), frame);
            boolean onTimeline = (frame != -1);

            int currFrame;
            RenderObject selected;
            String oName;
            if (onTimeline) {
                // left click select a frame
                if (args.button == 1) {
                    animTimeViewer.LeftDownTimeline = true;
                    selected = manipController.getCurrentObject();
                    currFrame = rController.animEngine.getCurrentFrame();
                    if (selected == null) {
                    	System.out.println("No object selected!\n");
                        rController.animEngine.moveToFrame(frame);
                    	return;
                    }
                    oName = selected.sceneObject.getID().name;
                    if (currFrame == frame) {
                        // if frame is equal to current frame, then add key frame if possibles
                        if (selected != null) {
                            rController.animEngine.addKeyframe(oName);
                            updateAnimation = true;
                        }
                    } else {
                        // Check if current frame has key frame; if not, reset rotation angles
                        rController.animEngine.resetFrameTransformation(oName);
                        // if frame is not equal to current frame, move frame to here
                        rController.animEngine.moveToFrame(frame);
                        updateAnimation = true;
                    }
                    // else make current frame selected
                    // right click remove key frame
                } else if (args.button == 2) {
                    selected = manipController.getCurrentObject();
                    if(selected != null) {
                        if (rController.animEngine.getCurrentFrame() == frame) {
                            // if frame is equal to current frame, then delete key frame
                            rController.animEngine.removeKeyframe(selected.sceneObject.getID().name);
                        }else {
                            // if frame is not equal to current frame, move frame to here
                            rController.animEngine.moveToFrame(frame);
                        }

                        updateAnimation = true;
                    }
                }
            }
        }
    };

    public ACEventFunc<MouseMoveEventArgs> onMouseMotion = new ACEventFunc<MouseMoveEventArgs>() {
        @Override
        public void receive(Object sender, MouseMoveEventArgs args) {
            RenderObject selected;
            // calculate which frame mouse is on
            int frame = -1;
            frame = animTimeViewer.onTimeline(game.getWidth(), game.getHeight(),rController.animEngine, Mouse.getX(), Mouse.getY(), frame);
            boolean onTimeline = (frame != -1);
            if(!onTimeline) animTimeViewer.LeftDownTimeline = false;
            if(animTimeViewer.LeftDownTimeline) {
                selected = manipController.getCurrentObject();
                if (selected != null) {
                    // if current frame has a key frame, delete it and add to where the mouse is
                    rController.animEngine.removeKeyframe(selected.sceneObject.getID().name);
                    rController.animEngine.moveToFrame(frame);
                    rController.animEngine.addKeyframe(selected.sceneObject.getID().name);
                    updateAnimation = true;
                }
            }
        }
    };

    public ACEventFunc<MouseButtonEventArgs> onMouseRelease = new ACEventFunc<MouseButtonEventArgs>() {
        @Override
        public void receive(Object sender, MouseButtonEventArgs args) {
            animTimeViewer.LeftDownTimeline = false;
        }
    };

    /**
     * Add Scene Data Hotkeys
     */
    private final ACEventFunc<KeyboardKeyEventArgs> onKeyPress = new ACEventFunc<KeyboardKeyEventArgs>() {
        @Override
        public void receive(Object sender, KeyboardKeyEventArgs args) {
            RenderObject selected;
            switch (args.key) {
                case Keyboard.KEY_G:
                    showGrid = !showGrid;
                    break;
                case Keyboard.KEY_F3:
                    FileDialog fd = new FileDialog(app.otherWindow);
                    fd.setVisible(true);
                    for(File f : fd.getFiles()) {
                        String file = f.getAbsolutePath();
                        if(file != null) {
                            Parser p = new Parser();
                            Object o = p.parse(file, Scene.class);
                            if(o != null) {
                                Scene old = app.scene;
                                app.scene = (Scene)o;
                                if(old != null) old.sendEvent(new SceneReloadEvent(file));
                                return;
                            }
                        }
                    }
                    break;
                case Keyboard.KEY_F4:
                    try {
                        app.scene.saveData("data/scenes/Saved.xml");
                    } catch (ParserConfigurationException | TransformerException e) {
                        e.printStackTrace();
                    }
                    break;
                case Keyboard.KEY_LBRACKET:
                    rController.animEngine.rewind(1);
                    updateAnimation = true;
                    break;
                case Keyboard.KEY_RBRACKET:
                    rController.animEngine.advance(1);
                    updateAnimation = true;
                    break;
                case Keyboard.KEY_N:
                    selected = manipController.getCurrentObject();
                    if(selected != null) {
                        System.out.println("Add frame for " + selected.sceneObject.getID().name);
                        rController.animEngine.addKeyframe(selected.sceneObject.getID().name);
                        updateAnimation = true;
                    }
                    break;
                case Keyboard.KEY_M:
                    selected = manipController.getCurrentObject();
                    if(selected != null) {
                        rController.animEngine.removeKeyframe(selected.sceneObject.getID().name);
                        updateAnimation = true;
                    }
                    break;
                case Keyboard.KEY_SPACE:
                    animator.togglePlaying();
                    break;
                case Keyboard.KEY_0:
                    rController.animEngine.moveToFrame(0);
                    updateAnimation = true;
                    break;
                /*
                case Keyboard.KEY_SLASH:
                    rController.animEngine.toggleRotationMode();
                    break;
                */
                default:
                    break;
            }
        }
    };

    @Override
    public void onEntry(GameTime gameTime) {
        cameraIndex = 0;

        rController = new RenderController(app.scene, new Vector2(app.getWidth(), app.getHeight()));
        renderer.buildPasses(rController.env.root);
        camController = new CameraController(app.scene, rController.env, null, animTimeViewer, rController.animEngine);
        createCamController();
        manipController = new ManipController(rController.env, app.scene, app.otherWindow, animTimeViewer, rController.animEngine);
        gridRenderer = new GridRenderer();
        animator = new Animator();

        KeyboardEventDispatcher.OnKeyPressed.add(onKeyPress);
        MouseEventDispatcher.OnMousePress.add(onMousePress);
        MouseEventDispatcher.OnMouseMotion.add(onMouseMotion);
        MouseEventDispatcher.OnMouseRelease.add(onMouseRelease);
        manipController.hook();

        Object tab = app.otherWindow.tabs.get("Object");
        if(tab != null) sceneTree = (ScenePanel)tab;
        tab = app.otherWindow.tabs.get("Material");
        if(tab != null) dataMaterial = (RPMaterialData)tab;
        tab = app.otherWindow.tabs.get("Mesh");
        if(tab != null) dataMesh = (RPMeshData)tab;
        tab = app.otherWindow.tabs.get("Texture");
        if(tab != null) dataTexture = (RPTextureData)tab;

        wasPickPressedLast = false;
        updateAnimation = false;
        animTimeViewer.init();
        prevCamScroll = 0;
    }

    @Override
    public void onExit(GameTime gameTime) {

        KeyboardEventDispatcher.OnKeyPressed.remove(onKeyPress);
        MouseEventDispatcher.OnMousePress.remove(onMousePress);
        manipController.unhook();

        rController.dispose();
        animTimeViewer.dispose();
        manipController.dispose();
        gridRenderer.dispose();
    }

    private void createCamController() {
        if(rController.env.cameras.size() > 0) {
            RenderCamera cam = rController.env.cameras.get(cameraIndex);
            camController.camera = cam;
        }
        else {
            camController.camera = null;
        }
    }

    @Override
    public void update(GameTime gameTime) {
        pick = false;
        int curCamScroll = 0;

        if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) curCamScroll++;
        if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)) curCamScroll--;
        if(rController.env.cameras.size() != 0 && curCamScroll != 0 && prevCamScroll != curCamScroll) {
            if(curCamScroll < 0) curCamScroll = rController.env.cameras.size() - 1;
            cameraIndex += curCamScroll;
            cameraIndex %= rController.env.cameras.size();
            createCamController();
        }
        prevCamScroll = curCamScroll;

        if(camController.camera != null) {
            camController.update(gameTime.elapsed);
            manipController.checkMouse(Mouse.getX(), Mouse.getY(), camController.camera);
        }

        if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0) && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
            if(!wasPickPressedLast) pick = true;
            wasPickPressedLast = true;
        }
        else wasPickPressedLast = false;

        // View A Different Scene
        if(rController.isNewSceneRequested()) {
            setState(ScreenState.ChangeNext);
        }

        // Update Animation
        int frames = animator.update((float)gameTime.elapsed);
        if(frames > 0) {
            rController.animEngine.advance(frames);
            updateAnimation = true;
        }
        if(updateAnimation) {
            rController.animEngine.updateTransformations();
            updateAnimation = false;
        }
    }

    @Override
    public void draw(GameTime gameTime) {


        rController.update(renderer, camController);

        if(pick && camController.camera != null) {
            manipController.checkPicking(renderer, camController.camera, Mouse.getX(), Mouse.getY());
        }

        Vector3 bg = app.scene.background;
        GL11.glClearColor(bg.x, bg.y, bg.z, 0);
        GL11.glClearDepth(1.0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if(camController.camera != null){
            renderer.draw(camController.camera, rController.env.lights, (float) gameTime.total);
            manipController.draw(camController.camera);
            if (showGrid)
                gridRenderer.draw(camController.camera);
        }

        RenderObject co = manipController.getCurrentObject();
        animTimeViewer.draw(
                game.getWidth(), game.getHeight(),
                rController.animEngine,
                co == null ? "" : co.sceneObject.getID().name,
                Mouse.getY() < 40 || !useTimelineMouseOver, (float)gameTime.elapsed);

        GLError.get("draw");
    }
}

