import core.Settings;
import modules.terrain.Terrain;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import tools.Camera;
import tools.Program;

import javafx.scene.media.*;

import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Window {

    private HashSet<Program> programs = new HashSet<>();

    // The window handle
    private long window;

    public Window() {
        run();
    }

    private void audio() {
        Thread thread = new Thread(() -> {
            String path = Paths.get("src/res/the_xx_intro.mp3").toUri().toString();
            Media pick = new Media(path);
            MediaPlayer player = new MediaPlayer(pick);

            player.play();
        });

        thread.start();
    }

    private void run() {
        System.out.println("InkMan " + Version.getVersion() + "!");


//        audio();
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        GLFWErrorCallback glfwErrorCallback = glfwSetErrorCallback(null);
        if(glfwErrorCallback != null) glfwErrorCallback.free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Get the primary Monitor - default one
        long primaryMonitor = glfwGetPrimaryMonitor();

        // Get the dimensions & resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);

        // Create the window
        window = glfwCreateWindow(vidmode.width(), vidmode.height(), "Ink Man", primaryMonitor, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            for(Program program : programs)
                program.keyCallback(key, action);
        });

        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            for(Program program : programs)
                program.scrollCallback(xoffset, yoffset);
        });

        // Get the thread stack and push a new frame
        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Camera camera = new Camera();

        // Generating the programs that need to be rendered
        programs.addAll(
                Arrays.asList(
                        new Terrain(camera)
                )
        );

        // Set the clear color
        glClearColor(
                Settings.CLEAR_COLOR.x,
                Settings.CLEAR_COLOR.y,
                Settings.CLEAR_COLOR.z,
                Settings.CLEAR_COLOR.w);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while(!glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            // Rendering every program
            for(Program program : programs)
                program.render();

            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
}