import org.kiyo.events.Callback
import java.util.UUID
import scala.collection.mutable

/**
 * The lighter version of the original Emitter, has the same features but 
 * @tparam T The type of data to emit
 */
class LiteEmitter[T] {
  private val callbacks: mutable.Map[String, mutable.Buffer[(String, Callback[T])]] = mutable.Map.empty
  final val ALL: String = "ALL_CHANNELS" + UUID.randomUUID().toString
  
  def emit(channel: String, data: T): Unit = {
    val channelsToEmit: Seq[String] = if (channel.equals(this.ALL)) this.callbacks.keys.toSeq else Seq(this.ALL, channel)
    channelsToEmit.flatMap(this.callbacks.get).foreach(callbackPairs => {
      callbackPairs.foreach((_, callback) => {
        callback.call(channel, data)
      })
    })
  }

  def on(channel: String, callback: Callback[T]): String = {
    val uuid: String = UUID.randomUUID().toString
    val callbackPairBuffer: mutable.Buffer[(String, Callback[T])] = this.callbacks.getOrElseUpdate(channel, mutable.Buffer())
    callbackPairBuffer += Tuple2(uuid, callback)
    uuid
  }

  def off(uuid: String): Unit = {
    var removedKey: Option[String] = None

    callbacks.foreach({ 
      case (key, buffer) => 
      if (buffer.exists(_._1.equals(uuid))) {
        removedKey = Some(key)
      }
    })

    removedKey.foreach(key => {
      val filteredBuffer = callbacks(key).filter(!_._1.equals(uuid))
      callbacks.remove(key)
      callbacks.put(key, filteredBuffer)
    })
  }
}
