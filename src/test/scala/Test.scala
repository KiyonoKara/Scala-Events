import org.kiyo.events.Emitter
import org.kiyo.events.Channel

object Test extends App {
  val mapEmitter = Emitter[Map[String, String]]
  val stringEmitter = Emitter[String]

  val id1 = mapEmitter.on(Channel.SOME("c1"), (channel, data) => {
    println("Data: " + data)
  })

  println("Emitting to channel1")
  mapEmitter.emit(Channel.SOME("c1"), Map("1" -> "One"))

  println("Stopping emission 1")
  //mapEmitter.remove(id1.uuid)
  id1.off()
  //mapEmitter.removeListener(id1)

  Thread.sleep(1000)
  mapEmitter.emit(Channel.SOME("c1"), Map("1" -> "One again"))

  val id2 = mapEmitter.on(Channel.SOME("c2"), (channel, data) => {
    println("Data for c2: " + data)
  })

  mapEmitter.emit(Channel.SOME("c2"), Map("2" -> "Two"))

  val id3 = mapEmitter.on(Channel.ALL, (_, data) => {
    println("Data for ALL: " + data)
  })

  mapEmitter.emit(Channel.SOME("c1"), Map("1" -> "1"))
  mapEmitter.emit(Channel.SOME("c2"), Map("2" -> "2"))
  println("Stopping emission 2")
  Thread.sleep(1000)

  val id6 = mapEmitter.on(Channel.SOME("c6"), (channel, data) => {
    println("C6: " + data)
  })
  val id7 = mapEmitter.on(Channel.SOME("c7"), (channel, data) => {
    println("C7: " + data)
  })
  val id8 = mapEmitter.on(Channel.SOME("c8"), (channel, data) => {
    println("C8: " + data)
  })
  val id9 = mapEmitter.on(Channel.SOME("c9"), (channel, data) => {
    println("C9: " + data)
  })

  mapEmitter.emit(Channel.ALL, Map("Everything" -> "A map with strings, all events should be emitted"))

  println("Stopping emission 3")
  Thread.sleep(1000)
  mapEmitter.emit(Channel.SOME("c1", "c2", "c6", "c7", "c8", "c9"), Map("Multiple" -> "Seen"))

  Thread.sleep(1000)
  val id10 = mapEmitter.on(Channel.SOME("c1", "c2"), (_, data) => {
    println("C1 or C2: " + data)
  })

  mapEmitter.emit(Channel.SOME("c1"), Map("1st" -> "First"))
  mapEmitter.emit(Channel.SOME("c2"), Map("2nd" -> "Second"))
  mapEmitter.emit(Channel.SOME("c1", "c2"), Map("Both" -> "Both C1 and C2"))

  mapEmitter.remove(id10.uuid)

  val id11 = mapEmitter.on(Channel.SOME("c1"), (_, data) => {
    println("C1 again: " + data)
  })

  mapEmitter.emit(Channel.SOME("c1"), Map("1st" -> "First"))

  println(mapEmitter.eventNames)
  Thread.sleep(1000)

  println("Check if listener for ALL triggers after c6 is removed")
  //mapEmitter.remove(id6.uuid)
  mapEmitter.removeListener(id6)

  mapEmitter.emit(Channel.SOME("c6"), Map("c6" -> "won't print"))
  Thread.sleep(1000)
  
  println("\nTest with Any data")
  val emitter = Emitter[Any]
  
  val e1 = emitter.on("c1", (_, data) => {
    println("c1: " + data)    
  })
  
  emitter.emit("c1", Seq(1, 2, 3))

  /*
  // scrap paper
  object Emitter {
    def apply[T](implicit i1: DummyImplicit) = new Emitter[T, T]
  
    def apply[T, U](implicit i1: DummyImplicit, i2: DummyImplicit) = new Emitter[T, U]
  }
   */
}
