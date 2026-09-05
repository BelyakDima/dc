package dev.darkness.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

/** Tracks click timestamps for CPS counters and keystroke highlighting. */
public final class CpManager {
	private static final Deque<Long> left = new ArrayDeque<>();
	private static final Deque<Long> right = new ArrayDeque<>();
	private static long leftDown = 0, rightDown = 0;

	public CpManager() {
	}

	public static void leftDown() {
		leftDown = System.currentTimeMillis();
	}

	public static void leftUp() {
		if (leftDown > 0) {
			left.add(System.currentTimeMillis());
			leftDown = 0;
		}
	}

	public static void rightDown() {
		rightDown = System.currentTimeMillis();
	}

	public static void rightUp() {
		if (rightDown > 0) {
			right.add(System.currentTimeMillis());
			rightDown = 0;
		}
	}

	private static int count(Deque<Long> q) {
		long now = System.currentTimeMillis();
		while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
		return q.size();
	}

	public static int leftCps() {
		return count(left);
	}

	public static int rightCps() {
		return count(right);
	}

	public static boolean leftHeld() {
		return leftDown > 0 || (!left.isEmpty() && System.currentTimeMillis() - left.peekLast() < 120);
	}

	public static boolean rightHeld() {
		return rightDown > 0 || (!right.isEmpty() && System.currentTimeMillis() - right.peekLast() < 120);
	}
}
