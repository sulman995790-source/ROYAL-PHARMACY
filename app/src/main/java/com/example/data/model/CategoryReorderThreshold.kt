package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_reorder_thresholds")
data class CategoryReorderThreshold(
  @PrimaryKey val category: String,
  val threshold: Int
)
