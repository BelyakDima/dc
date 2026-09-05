using System;
using System.Runtime.InteropServices;

public static class SendKey {
  [StructLayout(LayoutKind.Sequential)]
  public struct INPUT { public uint type; public ushort wVk; public ushort wScan; public uint dwFlags; public uint time; public IntPtr dwExtraInfo; }

  [DllImport("user32.dll")]
  public static extern uint SendInput(uint n, INPUT[] p, int size);

  public static void Tap(ushort scan, int holdMs) {
    INPUT down = new INPUT(); down.type = 1; down.wScan = scan; down.dwFlags = 0x0008;
    INPUT up = new INPUT(); up.type = 1; up.wScan = scan; up.dwFlags = 0x000A;
    SendInput(1, new INPUT[]{ down }, Marshal.SizeOf(typeof(INPUT)));
    System.Threading.Thread.Sleep(holdMs);
    SendInput(1, new INPUT[]{ up }, Marshal.SizeOf(typeof(INPUT)));
  }

  public static int Main(string[] args) {
    ushort scan = ushort.Parse(args[0]);
    int hold = args.Length > 1 ? int.Parse(args[1]) : 100;
    int repeat = args.Length > 2 ? int.Parse(args[2]) : 1;
    for (int i = 0; i < repeat; i++) { Tap(scan, hold); System.Threading.Thread.Sleep(150); }
    return 0;
  }
}
