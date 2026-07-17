# com-tinno-demo
Tinno Demo Store App Source

# com.tinno.demo — Full Feature Teardown

Static analysis of the Tinno retail "Explore this tablet" demo (richer sibling of Lightcomm's
`com.hcn.appwidget`). Pulled from a Walmart floor unit. This is what it does *beyond* the visible
Home / Models / Selling-Points / Video screens.

## Components (manifest)
| Type | Class | Role |
|---|---|---|
| activity | `HomeActivity` | Launcher + attract-loop entry; self-enables the accessibility lock |
| activity | `ModelsActivity` | Device/model showcase (likely uses `READ_PHONE_STATE` for specs) |
| activity | `SellingPointActivity` | Marketing feature carousel |
| activity | `VideoActivity` | Fullscreen video via **ExoPlayer** (`res/raw/onn.mp4`) |
| service | `InactivityMonitorService` | Foreground service **+ AccessibilityService** — the brains |
| receiver | `receiver.BootReceiver` | Auto-start on `BOOT_COMPLETED` |
| receiver | `widget.DemoAppWidget` | Home-screen app widget |

## Permissions
`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `WAKE_LOCK`, `READ_PHONE_STATE`,
`RECEIVE_BOOT_COMPLETED`, `GET_TASKS`, `WRITE_SECURE_SETTINGS`, `ACCESS_NETWORK_STATE`.

## 1. Idle / attract-loop engine  — PORTABLE, standard API (the valuable core)
`InactivityMonitorService`:
- Configurable inactivity thresholds: `background_` / `foreground_` / `screen_off_inactivity_millisecond`
  driving `CountDownTimer`s. On timeout → `startHomeActivity()` (relaunch the loop).
- "User is interacting" reset sources (any of these resets the idle timer):
  - `onAccessibilityEvent` + `onKeyEvent` (registered AccessibilityService).
  - **`CameraManager.registerAvailabilityCallback`** — detects when *another app opens the camera*
    (shopper testing the Camera app) and holds off the loop. NOT face/presence detection.
  - Broadcasts: `ACTION_POWER_CONNECTED`, `DEVICE_IDLE_MODE_CHANGED`, screen on/off.
  - (A 4th source — a device-wide touch hook — was removed; see §OEM.)
- Methods: `initTimer` / `startAllTimer` / `stopAllTimer` / `isForeground` / `registerBroadcast`
  / `initCameraListener` / `startHomeActivity`.

## 2. Kiosk lock + watchdog — privileged / disposable
- **Self-provisioning**: `HomeActivity` writes `enabled_accessibility_services` + `accessibility_enabled`
  via `Settings.Secure` (the `WRITE_SECURE_SETTINGS` use) → turns its own lockdown on with no human step.
  (Un-provisioned floor units skip this → why Walmart demo was escapable via Files→QuickShare.)
- **Key blocking**: `onKeyEvent` consumes hardware keys.
- **Foreground watchdog**: `getRunningTasks` → `isForeground()` → relaunch `HomeActivity` if the user
  leaves (`GET_TASKS`).

## 3. Presentation & lifecycle
- Immersive fullscreen (`setSystemUiVisibility`), `PowerManager.isInteractive`, `WAKE_LOCK` keep-awake.
- ExoPlayer video, foreground-service notification channel, app widget, boot auto-start.

## 4. What it does NOT do
- **No network / server / telemetry** — zero URLs, no HTTP client. Fully offline.
- **No runtime brand detection** — own code reads only `ro.build.type` (logging). Branding =
  **per-build baked assets** (`onn.mp4` + model list). Rebrand = asset/string swap, no config server.

## OEM dependency (removed in this fork)
`InactivityMonitorService` called `IWindowManager.registerIOnTouchListener(android.app.IOnTouchListener)`
— a Tinno framework addition for device-wide touch capture, absent on stock/GSI. It threw
`NoClassDefFoundError` (an `Error`, so their `catch Exception` missed it) → startup crash. Removed +
orphan `$1` (which extended `android.app.IOnTouchListener$Stub`) deleted. No public substitute exists;
`android.view.View.OnTouchListener` is per-view/in-process, not equivalent. In-app interaction reset
can be re-added via `Activity.onUserInteraction()` if desired.
  
