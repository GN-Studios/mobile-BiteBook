package com.example.bitebook.utils

import java.util.UUID

object IdProvider {
    fun newId(): String = UUID.randomUUID().toString()
}