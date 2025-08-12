object LiteTest extends App {
  val strEmitter: LiteEmitter[String] = LiteEmitter[String]

  val id1 = strEmitter.on("c1", (_, data) => {
    println("C1: " + data)
  })

  strEmitter.emit("c1", "Test")

  strEmitter.off(id1)

  strEmitter.emit("c1", "This string shouldn't be printed")

  val id2 = strEmitter.on("c2", (_, data) => {
    println("C2: " + data)
  })
  val id3 = strEmitter.on("c3", (_, data) => {
    println("C3: " + data)
  })

  strEmitter.emit(strEmitter.ALL, "Everything")
}
