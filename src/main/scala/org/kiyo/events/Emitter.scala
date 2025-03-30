package org.kiyo.events

import java.util.UUID
import scala.collection.mutable

/**
 * The event emitter class
 * @tparam T Output type for the callback data
 */
class Emitter[T] {
  private val callbacks: mutable.Map[String, mutable.ListBuffer[(String, Callback[T])]] = mutable.Map.empty
  // Randomized UUID for the ALL channels enum, done to avoid direct access
  private final val ALL_CHANNELS_ID: String = "ALL_CHANNELS" + UUID.randomUUID().toString

  /**
   * Emits an event based on the provided channel(s)
   * @param channel The channel(s)
   * @param data    The data to emit
   */
  //noinspection DuplicatedCode
  def emit(channel: Channel, data: T): Unit = {
    var channelsToEmit: Iterable[String] = Iterable.empty
    var targetChannel: String = new String()

    // Check channel enum
    channel match {
      // For all channels
      case Channel.ALL =>
        // Special character for
        targetChannel = this.ALL_CHANNELS_ID
        channelsToEmit = this.callbacks.keys.toSeq
      case some: Channel.SOME =>
        some.channels match {
          case Seq(_) =>
            targetChannel = some.channels.head
            channelsToEmit = Seq(ALL_CHANNELS_ID, targetChannel)
          case _ +: _ =>
            targetChannel = ALL_CHANNELS_ID
            channelsToEmit = some.channels
        }
    }

    // Make calls on the selected channels
    channelsToEmit.flatMap(this.callbacks.get).foreach(callbackPairs => {
      callbackPairs.foreach((_, callback) =>
        callback.call(targetChannel, data))
    })
  }

  /**
   * Listens to an event channel
   * @param channel  The channel(s)
   * @param callback The callback to relay the data
   * @return
   */
  def on(channel: Channel, callback: Callback[T]): Event = {
    // Generate UUID
    val uuid: String = UUID.randomUUID.toString
    // Start with an empty buffer
    var callbackPairBuffer: mutable.ListBuffer[(String, Callback[T])] = mutable.ListBuffer.empty
    channel match {
      // Case for all channels
      case Channel.ALL =>
        callbackPairBuffer = this.callbacks.getOrElseUpdate(ALL_CHANNELS_ID, mutable.ListBuffer())
      // Case for one or more channels
      case some: Channel.SOME =>
        some.channels match {
          // If only one channel
          case Seq(_) => callbackPairBuffer = this.callbacks.getOrElseUpdate(some.channels.head, mutable.ListBuffer())
          // If there's more than one channel
          case _ :+ _ => for (elem <- some.channels) {
            val bufferPair: mutable.Buffer[(String, Callback[T])] = this.callbacks.getOrElseUpdate(elem, mutable.ListBuffer())
            bufferPair += Tuple2(uuid, callback)
          }
        }
    }
    callbackPairBuffer += Tuple2(uuid, callback)
    // Return event data
    Event(channel, uuid)
  }

  /**
   * Remove an event entry based on the UUID of the event listener
   * @param uuid The provided UUID
   */
  def remove(uuid: String): Unit = {
    // Start with empty buffer of keys to remove
    val removedKeys: mutable.Buffer[Option[String]] = mutable.Buffer.empty

    // Get all keys from the callbacks for removal
    this.callbacks.foreach({ case (key, buffer) =>
      // If the UUID exists in the buffer, add the key
      if (buffer.exists(_._1.equals(uuid))) {
        removedKeys.addOne(Some(key))
      }
    })

    // Traverse through target keys
    removedKeys.foreach(_.foreach({ key =>
      // Remove everything in the buffer containing the UUID
      val filteredBuffer: mutable.ListBuffer[(String, Callback[T])] = this.callbacks(key).filter(!_._1.equals(uuid))
      this.callbacks.remove(key)
      this.callbacks.put(key, filteredBuffer)
    }))
  }

  /**
   * Event case class for holding listener data
   * @param channel The channels of the listener
   * @param uuid    The UUID of the listener
   */
  protected case class Event(channel: Channel, uuid: String) {
    /**
     * Turns off events for the channel(s)
     * Used for context for Event-type data
     */
    def off(): Unit = {
      Emitter.this.remove(this.uuid)
    }
  }

  /**
   * Returns all (registered) event names
   * @return Iterable set of strings
   */
  def eventNames: Iterable[String] = {
    this.callbacks.keys.filter(!_.contains(this.ALL_CHANNELS_ID))
  }

  /**
   * Removes listeners using an Event type
   * Ensures there are no instances of the specified channels
   * @param event The event containing the channel(s) to remove
   */
  def removeListener(event: Event): Unit = {
    // Check for the channels
    event.channel match {
      case Channel.ALL => this.callbacks.clear()
      case some: Channel.SOME =>
        // Filter all keys out from the callbacks map
        this.callbacks --= this.callbacks.keys.filter(some.channels.contains)
    }
    // Remove remaining connections
    this.remove(event.uuid)
  }
}