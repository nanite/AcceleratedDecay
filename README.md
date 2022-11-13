# Accelerated Decay

> Leaves go bye-bye

Accelerated Decay is an alternative `fast leaf decay` concept where instead of actually ticking the leaf blocks rapidly, we just remove them as soon as possible. Our mod implements this logic in the most vanilla possible way by delaying our leave checking logic to allow the game to naturally mark the leaves ready for removal. 

One of the goals of Accelerated Delay was reduce the lag on clients & servers. We've achieved this by using a time based checking system and heavily reducing the particals and ticking logic on blocks.

## How it works

Once the last block supporting leaves has been removed, the mod will attempt to seek out any leaves marked with the `#minecraft:leaves` tag and remove them as long as they're ready to be decayed.

We support any tree made up with the `#minecraft:logs` tag.

## Why use yours over others

- Less lag is basically the only differentiating factor and a greatly simplified codebase allow for a very minimal performance impact.
- Relatively Open License
- No complicated configs, it's either on or it's not
- Forge & Fabric support out of the box

### Why do you require the `architectury-api`?

I'm lazy, stop judging me, we fire and listen on some events that it's much simpler to implement with `architectury`. Also, lots of mods use it now so if you're on a modpack or making a modpack, you likely already have it!
