package dev.game2048.app.domain.model

import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class Tile(val id: String = UUID.randomUUID().toString(), val value: Int)
