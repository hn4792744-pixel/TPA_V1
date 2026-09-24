# ULTRAS_TPA 1.0.0
**Author:** UC_Hussein  
**Paper:** 1.21.x through modern Paper 26.2 runtime  
**Java:** 25

## New in this revision
- Smooth live teleport countdown in ActionBar, updated every 2 ticks by default.
- Default teleport delay: 3 seconds.
- Compact chat notifications: player name + request type/status.
- Countdown and all messages remain configurable.
- Movement cancellation remains configurable.

## Build
Use Java 25 and Gradle:
`gradle build`

The resulting JAR is in `build/libs/ULTRAS_TPA-1.0.0.jar`.

Paper 26.1+ requires Java 25. For older 1.21.x Paper, Java 21 is the documented baseline, although this source targets Java 25 as requested.
