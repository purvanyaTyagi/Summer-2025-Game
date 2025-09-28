# Multiplayer Platformer (Java + LibGDX)

A **2D multiplayer platformer** game where **players race to the top** through procedurally generated levels.  
The game is built with **Java**, **LibGDX**, and a custom **UDP-based Game Server**.  

Players can run the game in three modes:
- **Client-only** → connect to an external server and play  
- **Server-only** → host a server without playing  
- **with-server** → host a server and play on the same machine  

---

## 🚀 Features

### 🖥️ Game Server
The **custom server** is implemented in Java Networking and runs as a **non-blocking process** on a background thread. It handles all multiplayer logic and synchronization:
- **Sole decider of player positions** (prevents cheating)  
- **Custom platform maps per level**, ensuring consistency across players  
- **Thread pool** for handling each client separately  
- **Custom UDP protocols** for fast game data transmission  
- **Idle player removal** and **level cleanup** once all players finish  
- **Level generator** function for new platform layouts  
- **Character allocation mechanism** with redundancy, ensuring fair assignment (4-player max)  
- **Concurrency-safe implementation** with proper synchronization to avoid race conditions  
- **Dedicated broadcast loop** on its own thread, efficiently transmitting player positions to all clients  

---

### 🎮 Game Client
The **LibGDX client** handles rendering, physics, input, and communication with the server:
- **DesktopNetworkHandler**  
  - Runs a background **receive loop** for other players’ positions & game data  
  - Updates the local game state every frame tick  
- **PhysicsHandler**  
  - Defines all **2D platformer physics** (gravity, collisions, jumping, movement)  
  - Updates the player state and sends it to the server  
- **ClientState**  
  - Shared object between client and server for transmitting game state (positions, actions, metadata)  
- **Animator**  
  - Manages sprite animations (idle, walk, roll, jump, etc.)  
- **Character selection**  
  - 4 available characters, selection and animation fully handled client-side  

---

### 🏗️ Platform Generation
- When a player reaches a **new unreached level**, the client generates a new platform layout and sends it to the server.  
- The **server stores this layout** and shares it with other players as they arrive.  
- Once **all players clear the level**, the server deletes it from memory.  

This ensures that all players always play on the **same synchronized map**, while keeping memory usage efficient.  

---

## ⚙️ Build System
This project uses **Gradle** and **LibGDX**:
- **Gradle**  
  - Handles dependency management, builds, and packaging  
  - Multi-module setup for `core`, `desktop`, and `server` code  
- **LibGDX**  
  - Java game framework for cross-platform 2D/3D games  
  - Provides rendering, input handling, and asset management  
  - Integrated with Gradle for smooth builds  

---

## 📂 Project Structure

/core
└── src/main/java/com/summer/
    ├── ClientState.java           # Serializable game state shared between client & server
    ├── DesktopNetworkHandler.java # Handles all client-side networking (UDP)
    ├── Main.java                  # Entry point for the LibGDX game
    ├── PhysicsHandler.java        # Defines 2D game physics (gravity, collisions, etc.)
    ├── NetworkHandler.java        # Interface for networking layer
    └── assets/
        ├── Animator.java          # Handles sprite animations (walk, jump, roll, etc.)
        └── PlatformGenerator.java # Generates procedural platform layouts

/lwjgl3
└── src/main/java/com/summer/lwjgl3/
    ├── Lwjgl3Launcher.java        # Desktop launcher for the LibGDX client
    ├── StartupHelper.java         # Utility for desktop startup
    └── networkServer/
        └── GameServer.java        # Main UDP server for multiplayer game
        
- **`/core`** → platform-independent game code (works across desktop, Android, iOS)  
- **`/lwjgl3`** → desktop-specific code using LWJGL3 backend Includes both the **game server** and the **desktop launcher**  
- **`/assets`** → contains all important game assets

## 🌐 Networking Flows

1. Client sends player state → Server

2. Server updates the authoritative state

3. Server broadcasts all players’ positions → Clients

4. Clients render the updated state each frame

5. This loop ensures fair play, consistency, and low-latency multiplayer.

## Build Project using Gradle 

```bash
cd ../Summer_2025_game # go to the project root directory after cloning the repo

./gradlew build # jar files will be created under /lwjgl3/build/libs

java -cp lwjgl3/build/libs/lwjgl3-<version>.jar com.summer.lwjgl3.Lwjgl3Launcher -withserver
```

## Run Client + Server (same machine)
This will start a server in the background and launch the game client that connects to it.  
```bash
./gradlew lwjgl3:run --args='-withserver'
```
- Server runs locally on **127.0.0.1:9999**

## Run Client only

Connect to a remote server (or another machine on your LAN).

```bash
./gradlew lwjgl3:run --args='-clientonly'
```

You’ll be prompted for:

- Server IP → defaults to 127.0.0.1

- Server Port → defaults to 9999

Example (joining a LAN server):

```bash
Enter your server IP (default: 127.0.0.1): 192.168.1.50
Enter server port (default: 9999): 9999
```

## Run Server only

Run only the game server, no client graphics.

```bash
./gradlew lwjgl3:run --args='-serveronly'
```
- Server listens on 127.0.0.1:9999 by default
- Other players can connect via Client Only mode

## 🎮 Controls

- WASD / Arrow keys → Move & Jump
- Shift hold to roll

- Goal → Race to the top and beat the other players!


