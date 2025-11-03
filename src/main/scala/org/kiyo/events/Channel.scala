package org.kiyo.events

/**
 * Type of channel to emit.
 */
enum Channel {
  /**
   * All channels
   */
  case ALL

  /**
   * One or more channels
   */
  case SOME(channels: String*)
}