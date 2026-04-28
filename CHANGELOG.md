## [26.1.2.3]

### Fixed
* Hot fix to resolve an issue causing bone-mealed trees to instantly decay.

### Changed (from `[26.1.2.2]`)
* Reworked the way decay is detected
  * We no longer look for leaves as logs are broken, instead we hook into the games `block state` changes and watch for a leave block updating
  * When the leave is updated, and it's now too far away from a log, it's added to a queue of blocks to break
  * When the next server tick happens, 5 items are taken from this list and broken.
  * This reduces overhead of scanning for leaves as well as making the leaf decay no longer be directly tied to the block down trees but does preserve the behaviour of not decaying user placed leaves.
* The block decay queue is now stored in world data meaning if the game crashes softly or the user disconnects. The leaves will continued to decay when the world is reloaded.
* Leaves are no longer broken as a "player" as the hook does not have a player in scope. This should fix more issues than it causes.

## [26.1.2.2]

### Changed
* Reworked the way decay is detected
  * We no longer look for leaves as logs are broken, instead we hook into the games `block state` changes and watch for a leave block updating
  * When the leave is updated, and it's now too far away from a log, it's added to a queue of blocks to break
  * When the next server tick happens, 5 items are taken from this list and broken.
  * This reduces overhead of scanning for leaves as well as making the leaf decay no longer be directly tied to the block down trees but does preserve the behaviour of not decaying user placed leaves.
* The block decay queue is now stored in world data meaning if the game crashes softly or the user disconnects. The leaves will continued to decay when the world is reloaded.
* Leaves are no longer broken as a "player" as the hook does not have a player in scope. This should fix more issues than it causes.

## [26.1.2.1]

### Changed
* Ported to NeoForge 26.1.2.21-beta which breaks the `BlockBreak` event.

## [26.1.0.1]

### Changed

- Ported to 26.1
- Removed Architectury API dependency

## [21.0.0]

### Changed

- Ported to 1.21
- Switched to using the `Neoforge` version scheme meaning we've switched from 86.0.0 to 21.0.0... Sorry for the confusion... I don't like it either.

## [86.0.0]

### Changed

- Ported to 1.20.6
