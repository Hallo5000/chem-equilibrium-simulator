An attempt to simulate chemical equilibrium reactions in an extremely simplified but visually appealing way.

### Dependencies and Build Tools
- Microsoft OpenJDK 25.0.4
- Gradle Wrapper 9.0.1
- based on JavaFX 25.0.2 (used SceneBuilder)

# Todo:
- cut the simPane in a grid to check for particle collisions (reduces compute time)
- handle multiple particle collision in one simulation step
- handle situations in which one particle catches up with another (e.g. they only collide because of different speeds)
- counter for successful/failed/total collisions/reactions
- maybe a debug mode in which particles can be set manually in the simPane