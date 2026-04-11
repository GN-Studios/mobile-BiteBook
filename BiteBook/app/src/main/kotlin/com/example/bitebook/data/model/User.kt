package org.example.com.example.bitebook.data.model

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val name: String,
    val profileImageUrl: String = ""
)