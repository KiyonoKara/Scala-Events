package org.kiyo.events

enum Channel {
  case ALL
  case SOME(channels: String*)
}