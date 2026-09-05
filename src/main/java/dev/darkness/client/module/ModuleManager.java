package dev.darkness.client.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public void register(Module m) {
		modules.add(m);
	}

	public List<Module> getModules() {
		return modules;
	}

	public Module byName(String name) {
		for (Module m : modules) {
			if (m.getName().equalsIgnoreCase(name)) return m;
		}
		return null;
	}

	public List<Module> byCategory(Category c) {
		List<Module> out = new ArrayList<>();
		for (Module m : modules) {
			if (m.getCategory() == c) out.add(m);
		}
		out.sort(Comparator.comparing(Module::getName));
		return out;
	}

	public List<Module> getEnabledModules() {
		List<Module> out = new ArrayList<>();
		for (Module m : modules) {
			if (m.isEnabled()) out.add(m);
		}
		return out;
	}

	public List<HudModule> getHudModules() {
		List<HudModule> out = new ArrayList<>();
		for (Module m : modules) {
			if (m instanceof HudModule h) out.add(h);
		}
		return out;
	}

	public void tickAll() {
		for (Module m : modules) {
			if (m.isEnabled()) m.onTick();
		}
	}
}
