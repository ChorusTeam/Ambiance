package net.yeoxuhang.ambiance.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.world.level.Level;
import net.yeoxuhang.ambiance.client.particle.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class AmbianceClient implements ClientModInitializer {
    private static final List<ScheduledTask> tasks = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> onTick());
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.ASH, AshParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.END_PORTAL_ASH, AshParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.TRIAL, TrialParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.END_GATEWAY, TrialParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.FIRE_ASH, FireAshParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.PORTAL, PortalAshParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.ENDER_EYE_PLACE, EnderEyePlaceParticle.Provider::new);
    }
    @Environment(EnvType.CLIENT)
    public static void schedule(Level world, int delayTicks, Consumer<Level> action) {
        lock.lock();
        try {
            tasks.add(new ScheduledTask(world, delayTicks, action));
        } finally {
            lock.unlock();
        }
    }
    @Environment(EnvType.CLIENT)
    private static void onTick() {
        lock.lock();
        try {
            // Iterate in reverse to avoid ConcurrentModificationException
            for (int i = tasks.size() - 1; i >= 0; i--) {
                ScheduledTask task = tasks.get(i);
                task.decrementDelay();
                if (task.isReady()) {
                    task.execute();
                    tasks.remove(i); // Remove the task after execution
                }
            }
        } finally {
            lock.unlock();
        }
    }
    @Environment(EnvType.CLIENT)
    private static class ScheduledTask {
        private final Level world;
        private int delay;
        private final Consumer<Level> action;

        public ScheduledTask(Level world, int delayTicks, Consumer<Level> action) {
            this.world = world;
            this.delay = delayTicks;
            this.action = action;
        }

        public void decrementDelay() {
            if (delay > 0) {
                delay--;
            }
        }

        public boolean isReady() {
            return delay <= 0;
        }

        public void execute() {
            action.accept(world); // Execute the action, passing the world
        }
    }
}
