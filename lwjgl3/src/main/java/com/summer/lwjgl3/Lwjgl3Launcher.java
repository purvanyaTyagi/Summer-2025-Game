package com.summer.lwjgl3;


import java.util.Scanner;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.summer.Main;
import com.summer.lwjgl3.networkServer.*;
/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    static GameServer server;
    private static boolean withServer = false;
    private static boolean clientOnly = false;
    private static boolean serverOnly = false;
    public static String server_address = "127.0.0.1";

    public static void main(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case "-withserver":
                    withServer = true;
                    break;
                case "-clientonly":
                    clientOnly = true;
                    break;
                case "-serveronly":
                    serverOnly = true;
                    break;
                default:
                    System.err.println("Unknown option: " + arg);
                    printUsage();
                    System.exit(1);
            }
        }
        int optionCount = (withServer ? 1 : 0) + (clientOnly ? 1 : 0) + (serverOnly ? 1 : 0);
        if (optionCount != 1) {
            System.err.println("Error: You must specify exactly one mode");
            printUsage();
            System.exit(1);
        }

        if (withServer) {
            runWithServer();
        } else if (clientOnly) {
            runClientOnly();
        } else if (serverOnly) {
            runServerOnly();
        }

        // GameServer game_server = new GameServer();
        // Thread server = new Thread(game_server);
        // server.start();

        // if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        // createApplication();
        // try {
        //     game_server.stop();
        //     server.join(); // wait for server to finish
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
    
    private static void printUsage() {
        System.out.println("Usage: java Main <mode>");
        System.out.println("Modes (select one):");
        System.out.println("  -withserver   Run both client and server");
        System.out.println("  -clientonly   Run only the client");
        System.out.println("  -serveronly   Run only the server");
    }

    private static void runWithServer() {
        System.out.println("Running in combined client+server mode");
        GameServer game_server = new GameServer();
        Thread server = new Thread(game_server);
        server.start();

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
        try {
            game_server.stop();
            server.join(); // wait for server to finish
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void runClientOnly() {
        // Scanner sc = new Scanner(System.in);
        // System.out.print("Required! Enter your server ip(remote/public), write 127.0.0.1 if server is on localMachine: ");
        // server_address = sc.nextLine();

        System.out.println("Running in client-only mode");
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static void runServerOnly() {
        System.out.println("Running in server-only mode");
        GameServer server = new GameServer();
        server.run();
        server.stop();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(server_address, 9999), getDefaultConfiguration()); //server address goes here. If using -withServer keep this as local host.
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Game_Proj");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(1500, 1000);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
