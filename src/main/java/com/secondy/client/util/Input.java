package com.secondy.client.util;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Direct keyboard and mouse polling through GLFW. */
public final class Input {
    private Input() {}

    private static long window() {
        return MinecraftClient.getInstance().getWindow().getHandle();
    }

    public static boolean key(int glfwKey) {
        return GLFW.glfwGetKey(window(), glfwKey) == GLFW.GLFW_PRESS;
    }

    public static boolean mouse(int button) {
        return GLFW.glfwGetMouseButton(window(), button) == GLFW.GLFW_PRESS;
    }
}
