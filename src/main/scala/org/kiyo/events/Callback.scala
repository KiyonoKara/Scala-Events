package org.kiyo.events

trait Callback[T] {
  def call(channel: String, target: T): Unit
}
