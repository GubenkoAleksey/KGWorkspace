package com.hubenko.domain.model

/**
 * Роль користувача у системі.
 *
 * @property id Ідентифікатор ролі, що зберігається у Firestore та Room (напр. "USER", "ADMIN").
 * @property label Відображувана назва ролі українською мовою (напр. "Працівник").
 */
data class Role(
    val id: String,
    val label: String
)

