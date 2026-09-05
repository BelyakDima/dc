package dev.darkness.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tracks the entity the player recently attacked: for TargetHUD, combo counter,
 * reach display and damage-number estimation via synced health deltas.
 */
public final class TargetManager {
	public interface DamageListener {
		void onDamage(double amount, double x, double y, double z, boolean crit);
	}

	private final List<DamageListener> listeners = new ArrayList<>();
	public static class DamageNumber {
		public final double amount;
		public final double x, y, z;
		public final boolean crit;
		public long created = System.currentTimeMillis();
		public float offset = 0;

		DamageNumber(double amount, double x, double y, double z, boolean crit) {
			this.amount = amount;
			this.x = x;
			this.y = y;
			this.z = z;
			this.crit = crit;
		}
	}

	private Entity target;
	private long lastAttackMs = 0;
	private double lastTargetHealth = -1;
	private int combo = 0;
	private long lastComboHit = 0;
	private double lastReach = 0;
	private final List<DamageNumber> damageNumbers = new ArrayList<>();
	private final Minecraft mc = Minecraft.getInstance();
	// хит-маркер (наш удар достиг цели)
	public long lastHitMs = 0;
	// направление урона по нам
	public long selfHurtMs = 0;
	public double selfHurtX, selfHurtY, selfHurtZ;
	private int prevHurtTime = 0;
	public int sessionKills = 0;
	public int sessionDeaths = 0;
	private boolean prevDead = false;
	private boolean targetWasAlive = false;

	public void onAttack(Entity entity) {
		target = entity;
		lastAttackMs = System.currentTimeMillis();
		lastReach = mc.player != null ? mc.player.distanceTo(entity) : 0;
		if (entity instanceof LivingEntity living) {
			lastTargetHealth = living.getHealth();
		} else {
			lastTargetHealth = -1;
		}
	}

	public void tick() {
		long now = System.currentTimeMillis();
		if (now - lastAttackMs > 4000) {
			target = null;
			lastTargetHealth = -1;
		}
		if (combo > 0 && now - lastComboHit > 4000) combo = 0;

		if (mc.player != null && mc.player.hurtTime > 0 && mc.player.hurtTime == mc.player.hurtDuration - 1) {
			combo = 0;
		}

		if (mc.player != null && mc.player.hurtTime > 0 && prevHurtTime == 0) {
			// нас только что ударили — запоминаем, откуда
			Entity attacker = mc.player.getLastHurtByMob();
			if (attacker != null) {
				selfHurtX = attacker.getX();
				selfHurtY = attacker.getY() + attacker.getBbHeight() * 0.7;
				selfHurtZ = attacker.getZ();
				selfHurtMs = now;
			}
		}
		if (mc.player != null) prevHurtTime = mc.player.hurtTime;

		// счётчики сессии
		boolean dead = mc.player != null && mc.player.isDeadOrDying();
		if (dead && !prevDead) sessionDeaths++;
		prevDead = dead;
		if (target instanceof LivingEntity le) {
			boolean alive = le.isAlive();
			if (targetWasAlive && !alive && now - lastHitMs < 3000) sessionKills++;
			targetWasAlive = alive;
		}

		// damage numbers via synced health deltas of recent target
		if (target instanceof LivingEntity living && lastTargetHealth >= 0 && living.isAlive()) {
			float nowHealth = living.getHealth();
			if (nowHealth < lastTargetHealth - 0.01f) {
				double dmg = lastTargetHealth - nowHealth;
				combo++;
				lastComboHit = now;
				lastHitMs = now;
				damageNumbers.add(new DamageNumber(dmg,
					living.getX(), living.getY() + living.getBbHeight() * 0.6, living.getZ(),
					mc.player != null && mc.player.fallDistance > 0));
				if (damageNumbers.size() > 30) damageNumbers.remove(0);
				for (DamageListener l : new ArrayList<>(listeners)) {
					l.onDamage(dmg, living.getX(), living.getY() + living.getBbHeight() * 0.8, living.getZ(),
						mc.player != null && mc.player.fallDistance > 0);
				}
			}
			lastTargetHealth = nowHealth;
		} else if (target != null && !target.isAlive()) {
			lastTargetHealth = -1;
		}
	}

	public void addListener(DamageListener l) {
		listeners.add(l);
	}

	public void removeListener(DamageListener l) {
		listeners.remove(l);
	}

	public Entity getTarget() {
		return target;
	}

	public long getLastAttackMs() {
		return lastAttackMs;
	}

	public boolean hasTarget() {
		return target != null && target.isAlive() && System.currentTimeMillis() - lastAttackMs < 3000;
	}

	public int getCombo() {
		return combo;
	}

	public double getLastReach() {
		return lastReach;
	}

	public List<DamageNumber> getDamageNumbers() {
		return damageNumbers;
	}

	public void tickDamageNumbers() {
		Iterator<DamageNumber> it = damageNumbers.iterator();
		long now = System.currentTimeMillis();
		while (it.hasNext()) {
			if (now - it.next().created > 1200) it.remove();
		}
	}
}
