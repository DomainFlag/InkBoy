import core.Settings;
import modules.display.Display;
import modules.fighter.Fighter;
import modules.primitive.BezierCurve;
import modules.primitive.Quad;
import modules.primitive.Triangle;
import modules.terrain.Terrain;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import core.view.Camera;
import core.tools.Log;
import tools.Context;
import tools.Program;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Window {

    // Programs
    private HashSet<Program> programs = new HashSet<>();

    // Camera
    private Camera camera = null;

    // Window context
    private Context context = null;

    // The window handle
    private long window;

    public Window() {
        run();
    }

    private void run() {
        init();
        loop();

        // free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // terminate GLFW and free the error callback
        glfwTerminate();

        GLFWErrorCallback glfwErrorCallback = glfwSetErrorCallback(null);

        if(glfwErrorCallback != null)
            glfwErrorCallback.free();
    }

    private void init() {
        // setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // get the primary Monitor - default one
        long monitor = glfwGetPrimaryMonitor();

        // create window context
        context = new Context(monitor);

        // get the dimensions & resolution of the primary monitor and set it to global vidmode variable
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);

        // create the window
        window = glfwCreateWindow(1280, 720, "StarCannon", NULL, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if(context.getCamera() != null && action == GLFW_PRESS)
                context.getCamera().keyCallback(key, action);
        });

        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            if(context.getCamera() != null)
                context.getCamera().scrollCallback(xoffset, yoffset);
        });


        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if(context.getCamera() != null)
                context.getCamera().cursorPosCallback(xpos, ypos);
        });

        if(vidmode != null) {
            // get the thread stack and push a new frame
            try(MemoryStack stack = stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);

                // get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, width, height);

                // center the window
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - width.get(0)) / 2,
                        (vidmode.height() - height.get(0)) / 2
                );
            }

            // the stack frame is popped automatically
        }

        // make the OpenGL context current
        glfwMakeContextCurrent(window);

        // enable v-sync
        glfwSwapInterval(1);

        // make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // initialize context
        context.initialize();

        // generating the programs that need to be rendered
        programs.addAll(
                Arrays.asList(
                        new Terrain(context)
                )
        );

        // set the clear color
        glClearColor(
                Settings.CLEAR_COLOR.get(0),
                Settings.CLEAR_COLOR.get(1),
                Settings.CLEAR_COLOR.get(2),
                Settings.CLEAR_COLOR.get(3));

        // run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while(!glfwWindowShouldClose(window)) {
            // clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // rendering every program
            for(Program program : programs)
                program.render();

            context.getCamera().change();

            // swap the color buffers
            glfwSwapBuffers(window);

            // poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        // clear every program
        for(Program program : programs)
            program.clear();
    }
}