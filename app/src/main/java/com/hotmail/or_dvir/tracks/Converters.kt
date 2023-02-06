package com.hotmail.or_dvir.tracks

import com.hotmail.or_dvir.tracks.database.entities.TrackedEventEntity
import com.hotmail.or_dvir.tracks.models.TrackedEventModel

fun List<TrackedEventModel>.toEntities() = this.map { it.toEntity() }
fun TrackedEventModel.toEntity() = TrackedEventEntity(
    id = id,
    startMillis = startMillis,
    endMillis = endMillis,
    note = note
)

fun List<TrackedEventEntity>.toModels() = this.map { it.toModel() }
fun TrackedEventEntity.toModel() = TrackedEventModel(
    id = id,
    startMillis = startMillis,
    endMillis = endMillis,
    note = note
)
