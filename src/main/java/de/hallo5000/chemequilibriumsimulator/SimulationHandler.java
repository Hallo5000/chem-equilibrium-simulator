package de.hallo5000.chemequilibriumsimulator;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimulationHandler {

    public enum GameState {
        RUNNING,
        PAUSED,
        STOPPED
    }

    private int particleCountA;
    private int particleCountB;
    private double avgInitParticleSpeed;
    private double activationEnergy;
    public static final double MAX_SPEED = 4.0;
    private final Random random = new Random();
    private final ArrayList<ParticleWrapper> allParticles = new ArrayList<>();
    private final AnchorPane simPane;

    private GameState gamestate = GameState.STOPPED;
    private AnimationTimer timer;
    private ScheduledExecutorService physicsExecutor;
    private MainController mainController;

    public SimulationHandler(int particleCountA, int particleCountB, double avgInitParticleSpeed,
            double activationEnergy, AnchorPane simPane, MainController mainController) {
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
        this.activationEnergy = activationEnergy;
        this.simPane = simPane;
        this.mainController = mainController;
    }

    public void initSim() {
        if (gamestate == GameState.RUNNING)
            return;
        gamestate = GameState.RUNNING;
        synchronized (allParticles) {
            allParticles.clear();
            simPane.getChildren().clear();// clear the arraylist and simPane
            for (int i = 0; i < particleCountA; i++) {
                Point2D coords = genRandomCoords(100);
                if (coords == null)
                    break;
                allParticles.add(new ParticleWrapper(coords, genRandomVector(), getRandomSpeed(), Particle.State.A));
            }
            for (int i = 0; i < particleCountB; i++) {
                Point2D coords = genRandomCoords(100);
                if (coords == null)
                    break;
                allParticles.add(new ParticleWrapper(coords, genRandomVector(), getRandomSpeed(), Particle.State.B));
            }
        }
        simLoop(); // start moving particles
    }

    private void simLoop() {
        if (physicsExecutor != null && !physicsExecutor.isShutdown()) {
            physicsExecutor.shutdownNow();
        }

        // 1. cpu bound physics-loop
        physicsExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PhysicsThread");
            t.setDaemon(true);
            return t;
        });

        physicsExecutor.scheduleAtFixedRate(() -> {
            if (gamestate == GameState.RUNNING) {
                try {
                    updateSim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.MILLISECONDS);

        // 2. render-loop (synchronized with monitor refresh rate)
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                synchronized (allParticles) {
                    for (ParticleWrapper p : allParticles) {
                        p.updateView();
                    }
                    if (mainController != null) {
                        mainController.updateParticleBar(particleCountA, particleCountB);
                        mainController.updateParticleSliders(particleCountA, particleCountB);
                    }
                }
            }
        };
        timer.start();
    }

    private void updateSim() {
        synchronized (allParticles) {
            ArrayList<ParticleWrapper> compareTo = new ArrayList<>(allParticles);
            double paneLeft = simPane.getLayoutX();
            double paneTop = simPane.getLayoutY();
            double paneWidth = simPane.getWidth();
            double paneHeight = simPane.getHeight();

            for (ParticleWrapper p1 : allParticles) {
                Point2D v1 = p1.getVelocity();

                double maxFactorX = 1;// max factor for the direction vector to hit the border on the x-axis on
                if (p1.getDirection_vec().getX() > 0) {
                    maxFactorX = ((paneLeft + paneWidth) - (p1.getCoordinates().getX() + Particle.RADIUS)) / v1.getX();
                } else if (p1.getDirection_vec().getX() < 0) {
                    maxFactorX = (paneLeft - (p1.getCoordinates().getX() - Particle.RADIUS)) / v1.getX();
                }
                double maxFactorY = 1;// max factor for the direction vector to hit the border on the y-axis on
                if (p1.getDirection_vec().getY() > 0) {
                    maxFactorY = ((paneTop + paneHeight) - (p1.getCoordinates().getY() + Particle.RADIUS)) / v1.getY();
                } else if (p1.getDirection_vec().getY() < 0) {
                    maxFactorY = (paneTop - (p1.getCoordinates().getY() - Particle.RADIUS)) / v1.getY();
                }

                compareTo.remove(p1);
                for (ParticleWrapper p2 : compareTo) {

                    /*
                     * solve for t (factor for the vectors at which the particles collide and
                     * "end of collision"):
                     * a1 := coords of p1
                     * b1 := vector of p1
                     * a2 := coords of p2
                     * b2 := vector of p2
                     * |(a1+t*b1)-(a2+t*b2)|=2R
                     * -----use d(x) := delta function
                     * |d(a)+t*d(b)|=2R
                     * -----absolute value of vectors |v| := sqrt( (vx)^2 + (vy)^2 )
                     * sqrt((d(ax)+t*d(bx))^2 + (d(ay)+t*d(by))^2)=2R
                     * -----square to get rid of the square root
                     * ((d(ax)+t*d(bx))^2+(d(ay)+t*d(by))^2)=4R^2
                     * -----binomial theorem
                     * ((d(ax)^2+2d(ax)t*d(bx)+t^2d(bx)^2)+(d(ay)^2+2d(ay)d(by)+t^2d(by)^2))=4R^2
                     * -----get rid of a few unnecessary brackets
                     * d(ax)^2+2d(ax)t*d(bx)+t^2d(bx)^2+d(ay)^2+2d(ay)t*d(by)+t^2d(by)^2=4R^2
                     * -----quadratic form
                     * (d(bx)^2+d(by)^2)*t^2+(2d(ax)d(bx)+2d(ay)d(by))*t+(-4R^2+d(ax)^2+d(ay)^2)=0
                     * -----quadratic formula
                     * t1,2=( -(2d(ax)d(bx)+2d(ay)d(by)) +/- sqrt( (2d(ax)d(bx)+2d(ay)d(by))^2 -
                     * 4(d(bx)^2+d(by)^2)(-4R^2+d(ax)^2+d(ay)^2) ) )/2(d(bx)^2+d(by)^2)
                     */

                    Point2D deltaP = p1.getCoordinates().subtract(p2.getCoordinates());
                    Point2D deltaV = p1.getVelocity().subtract(p2.getVelocity());

                    double factorA = deltaV.dotProduct(deltaV);
                    double factorB = 2 * deltaP.dotProduct(deltaV);
                    double factorC = -4 * Math.pow(Particle.RADIUS, 2) + deltaP.dotProduct(deltaP);

                    if (factorA == 0)
                        continue; // extremely unlikely: movement of p1 and p2 is synchronized (e.g. both still
                                  // standing or same movement vector) this would cause division by 0

                    // Only collide if particles are moving towards each other
                    if (factorB >= 0)
                        continue;

                    double discriminant = Math.pow(factorB, 2)
                            - 4 * factorA * factorC; // b² - 4ac

                    if (discriminant >= 0) { // collision
                        double t1 = (-factorB - Math.sqrt(discriminant)) / (2 * factorA); // (-b - sqrt(b^2 - 4ac)) / 2a
                        if (t1 >= 0 && t1 <= 1) {
                            Point2D normal = deltaP.add(deltaV.multiply(t1)).normalize();
                            double impulse = deltaV.dotProduct(normal);
                            applyVelocity(p1, p1.getVelocity().subtract(normal.multiply(impulse)));
                            applyVelocity(p2, p2.getVelocity().add(normal.multiply(impulse)));
                            v1 = p1.getVelocity();

                            if (p1.getState() == p2.getState()) {
                                if (p1.getState() == Particle.State.A) {
                                    p1.setState(Particle.State.B);
                                    p2.setState(Particle.State.B);
                                    particleCountA -= 2;
                                    particleCountB += 2;
                                } else {
                                    p1.setState(Particle.State.A);
                                    p2.setState(Particle.State.A);
                                    particleCountA += 2;
                                    particleCountB -= 2;
                                }
                            }
                        }
                    }
                }

                if (maxFactorX >= 1 && maxFactorY >= 1) {
                    p1.setCoordinates(p1.getCoordinates().add(v1));
                } else {
                    double beforeReflect = Math.min(maxFactorX, maxFactorY); // the percentage of the vector applied
                                                                             // before reflecting of a wall

                    double midX = p1.getCoordinates().getX() + v1.getX() * beforeReflect;
                    double midY = p1.getCoordinates().getY() + v1.getY() * beforeReflect;

                    if (maxFactorX < maxFactorY)
                        p1.setDirection_vec(new Point2D(-p1.getDirection_vec().getX(), p1.getDirection_vec().getY()));
                    else
                        p1.setDirection_vec(new Point2D(p1.getDirection_vec().getX(), -p1.getDirection_vec().getY()));

                    v1 = p1.getVelocity();
                    p1.setCoordinates(new Point2D(
                            midX + v1.getX() * (1 - beforeReflect),
                            midY + v1.getY() * (1 - beforeReflect)));
                }
            }
        }
    }

    private void applyVelocity(ParticleWrapper p, Point2D velocity) {
        double speed = velocity.magnitude();
        p.setSpeed(speed);
        if (speed > 0) {
            p.setDirection_vec(velocity.normalize());
        }
    }

    private double getRandomSpeed() {
        if (avgInitParticleSpeed <= 0)
            return 0;
        double avg = avgInitParticleSpeed * MAX_SPEED;
        return Math.max(0.05, avg + random.nextGaussian() * avg * 0.3);
    }

    public void stopSim() {
        if (gamestate == GameState.STOPPED)
            return;
        gamestate = GameState.STOPPED;
        if (timer != null) {
            timer.stop();
        }
        if (physicsExecutor != null && !physicsExecutor.isShutdown()) {
            physicsExecutor.shutdownNow();
        }
        particleCountA = 0;
        particleCountB = 0;
        synchronized (allParticles) {
            allParticles.clear();
            simPane.getChildren().clear();
        }
    }

    /**
     * Tries to generate random coordinates that are not taken within the simPane
     * 
     * @param remainingTries specifies how often it will try
     * @return a <code>Point2D</code> which represents a point in the simPane with
     *         <code>Particle.RADIUS</code> free space
     */
    public Point2D genRandomCoords(int remainingTries) {
        if (remainingTries <= 0)
            return null;
        double x = new Random().nextDouble() * simPane.getWidth() - Particle.RADIUS;
        double y = new Random().nextDouble() * simPane.getHeight() - Particle.RADIUS;
        System.out.println("x: " + x + " y: " + y);
        boolean intersects = simPane.getChildren().stream().anyMatch(node -> {
            if (node instanceof Circle c) {
                return Math.sqrt((x - c.getCenterX()) * (x - c.getCenterX())
                        + (y - c.getCenterY()) * (y - c.getCenterY())) < Particle.RADIUS * 3;
            }
            return false;
        });
        Point2D finalCoords = new Point2D(x, y);
        if (x < 0 || y < 0 || intersects)
            finalCoords = genRandomCoords(remainingTries - 1);
        return finalCoords;
    }

    public Point2D genRandomVector() {
        double x = new Random().nextDouble(2.0) - 1;// upper bound is excluded,
        double y = new Random().nextDouble(2.0) - 1;// but it doesn't matter since the chance of getting it would be
                                                    // near impossible anyway

        return new Point2D(x, y).normalize();
    }

    public int getParticleCountA() {
        return particleCountA;
    }

    public int setParticleCountA(int particleCountA) {
        synchronized (allParticles) {
            if (particleCountA > this.particleCountA) {
                for (int i = 0; i < particleCountA - this.particleCountA; i++) {
                    Point2D coords = genRandomCoords(100);
                    if (coords == null)
                        break;
                    allParticles.add(new ParticleWrapper(coords, genRandomVector(), 0, Particle.State.A));
                }
            } else if (particleCountA < this.particleCountA) {
                for (int i = 0; i < this.particleCountA - particleCountA; i++) {
                    ArrayList<ParticleWrapper> particlesA = getParticlesA();
                    if (particlesA.isEmpty())
                        break;
                    ParticleWrapper toRemove = particlesA.get(new Random().nextInt(particlesA.size()));
                    allParticles.remove(toRemove);
                    simPane.getChildren().remove(toRemove.getCircle());
                }
            }
            this.particleCountA = particleCountA;
            if (allParticles.size() != particleCountA + particleCountB)
                this.particleCountA = allParticles.size() - particleCountB;
            return this.particleCountA;
        }
    }

    public int getParticleCountB() {
        return particleCountB;
    }

    public int setParticleCountB(int particleCountB) {
        synchronized (allParticles) {
            if (particleCountB > this.particleCountB) {
                for (int i = 0; i < particleCountB - this.particleCountB; i++) {
                    Point2D coords = genRandomCoords(100);
                    if (coords == null)
                        break;
                    allParticles.add(new ParticleWrapper(coords, genRandomVector(), 0, Particle.State.B));
                }
            } else if (particleCountB < this.particleCountB) {
                for (int i = 0; i < this.particleCountB - particleCountB; i++) {
                    ArrayList<ParticleWrapper> particlesB = getParticlesB();
                    if (particlesB.isEmpty())
                        break;
                    ParticleWrapper toRemove = particlesB.get(new Random().nextInt(particlesB.size()));
                    allParticles.remove(toRemove);
                    simPane.getChildren().remove(toRemove.getCircle());
                }
            }
            this.particleCountB = particleCountB;
            if (allParticles.size() != particleCountB + particleCountA)
                this.particleCountB = allParticles.size() - particleCountA;
            return this.particleCountB;
        }
    }

    public double getAvgInitParticleSpeed() {
        return avgInitParticleSpeed;
    }

    public void setAvgInitParticleSpeed(double avgInitParticleSpeed) {
        synchronized (allParticles) {
            double oldSpeed = this.avgInitParticleSpeed;
            this.avgInitParticleSpeed = avgInitParticleSpeed;

            if (oldSpeed > 0) {
                double factor = avgInitParticleSpeed / oldSpeed;
                for (ParticleWrapper p : allParticles) {
                    p.setSpeed(p.getSpeed() * factor);
                }
            } else if (avgInitParticleSpeed > 0) {
                for (ParticleWrapper p : allParticles) {
                    p.setSpeed(getRandomSpeed());
                }
            }
        }
    }

    public double getActivationEnergy() {
        return activationEnergy;
    }

    public void setActivationEnergy(double activationEnergy) {
        this.activationEnergy = activationEnergy;
    }

    public ArrayList<ParticleWrapper> getParticlesA() {
        synchronized (allParticles) {
            return allParticles.stream().filter(p -> p.getState() == Particle.State.A)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public ArrayList<ParticleWrapper> getParticlesB() {
        synchronized (allParticles) {
            return allParticles.stream().filter(p -> p.getState() == Particle.State.B)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public GameState getGamestate() {
        return gamestate;
    }

    public void setGamestate(GameState gamestate) {
        this.gamestate = gamestate;
    }

    public AnchorPane getSimPane() {
        return simPane;
    }

}
