# Scala Events

<div>
  <p>
    <a href="https://github.com/KiyonoKara/Scala-Events/pulls"><img src="https://shields.io/github/issues-pr/KiyonoKara/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KiyonoKara/Scala-Events?color=da301b" alt="Code Size" /></a>
    <a><img src="https://img.shields.io/github/last-commit/KiyonoKara/Scala-Events?color=007ace" alt="Last Commit" /></a>
  </p>
</div>

An event emitter tool written in Scala.

## Why

To make and handle events. Can be used to make a chat app or something that needs a publisher-subscriber pattern.

1. [How it works](#how-it-works)
2. [Usage](#usage)
    + [Basic Event Handling](#basic-event-handling)
    + [Removing channels](#removing-channels)
    + [Using the `Channel` enum](#using-the-channel-enum)
        - [Multiple channels](#multiple-channels)
        - [All channels](#all-channels)

## How it works

1. Emitter emits to one or more channels → All listeners subscribed the channel(s) receives the event.
2. Listener listens to one or more channels → All emitted events trigger all subscribed listeners.
3. Listener listens to all channels → All emitted events trigger that listener.

## Usage

### Basic Event Handling
- Make an emitter and give it a type.
- Create a listener, and set it to a channel (this creates a channel if it's never been there before).
- Emit an event or data to the channel when necessary.

```scala
val emitter = Emitter[Any]
// listens to channel_1
val listener = emitter.on("channel_1", (callback, data) => {
  println("Channel 1: " + data)

// emits to channel_1
emitter.emit("channel_1", "hi")
})
```

### Removing channels
Example: Turning off `channel_1` means no more events for that channel, this can be turned on again by declaring a new listener.
```scala
emitter.off("channel_1")
```

Event connections can be manually removed with `.remove()`. **This doesn't remove listeners.**
```scala
emitter.remove(listener.uuid)
```

Event listeners can be manually removed with `.removedListener()`. **This removes listeners and connections to them**.
```scala
emitter.removeListener(listener)
```

### Using the `Channel` enum
Using a regular string in the first argument only works for one channel. The `Channel` enum allows you to pass multiple channels or all.

#### Multiple channels
```scala
val listener = emitter.on(Channel.SOME("c1", "c2"), (...) => {
 // do something with the data here...
})
emitter.emit(Channel.SOME("c1", "c2"), "hi again")
```

#### All channels

Example: Despite `c1` not being declared as a listener, `Channel.ALL` listens to everything.
```scala
val listener = emitter.on(Channel.ALL, (...) => {
 // do something with the data here...
})
emitter.emit(Channel.SOME("c1"), "hi all")
```

Example: All listeners subscribed to their own respective channels can receive events from an emitter that emits to `Channel.ALL`.
```scala
val listener = emitter.on("c1", (...) => {
 // do something with the data here...
})
val listener2 = emitter.on("c2", (...) => {
 // do something with the data here...
})

emitter.emit(Channel.ALL, "hi all")
```