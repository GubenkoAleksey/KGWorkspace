package com.hubenko.feature.admin.ui

import android.net.Uri
import com.hubenko.core.base.ViewIntent
import com.hubenko.core.base.ViewSideEffect
import com.hubenko.core.base.ViewState
import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.EmployeeStatus

/**
 * Стан екрана адміністратора.
 *
 * @property employees Список всіх співробітників.
 * @property statuses Список останніх статусів співробітників.
 * @property isLoading Прапор завантаження даних.
 * @property selectedTab Поточна активна вкладка (Працівники або Статуси).
 * @property isEmployeeDialogOpen Чи відкритий діалог створення/редагування співробітника.
 * @property editingEmployee Об'єкт співробітника, який редагується (null для створення нового).
 */
data class AdminState(
    val employees: List<Employee> = emptyList(),
    val statuses: List<EmployeeStatus> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: AdminTab = AdminTab.EMPLOYEES,
    val isEmployeeDialogOpen: Boolean = false,
    val editingEmployee: Employee? = null,
    val isDeleteStatusesDialogOpen: Boolean = false
) : ViewState

/**
 * Вкладки панелі адміністратора.
 */
enum class AdminTab(val title: String) {
    EMPLOYEES("Працівники"),
    STATUSES("Статуси")
}

/**
 * Інтенції (наміри) користувача на екрані адміністратора.
 */
sealed class AdminIntent : ViewIntent {
    /** Завантажити початкові дані (співробітників та статуси) */
    data object LoadData : AdminIntent()
    
    /** Зміна активної вкладки */
    data class OnTabSelected(val tab: AdminTab) : AdminIntent()
    
    /** Натиск на кнопку додавання нового співробітника */
    data object OnAddEmployeeClick : AdminIntent()
    
    /** Натиск на кнопку редагування співробітника */
    data class OnEditEmployeeClick(val employee: Employee) : AdminIntent()
    
    /** Натиск на кнопку видалення співробітника */
    data class OnDeleteEmployeeClick(val id: String) : AdminIntent()
    
    /** Збереження даних співробітника (новий або існуючий) */
    data class OnSaveEmployee(val employee: Employee) : AdminIntent()
    
    /** Закриття діалогового вікна */
    data object OnDismissDialog : AdminIntent()

    /** Натиск на кнопку експорту статусів */
    data object OnExportStatusesClick : AdminIntent()

    /** Натиск на кнопку видалення всіх статусів */
    data object OnDeleteAllStatusesClick : AdminIntent()

    /** Підтвердження видалення всіх статусів */
    data object OnConfirmDeleteAllStatuses : AdminIntent()
}

/**
 * Одноразові ефекти екрана адміністратора.
 */
sealed class AdminEffect : ViewSideEffect {
    /** Відображення текстового повідомлення */
    data class ShowToast(val message: String) : AdminEffect()
    
    /** Навігація назад */
    data object NavigateBack : AdminEffect()

    /** Відкриття діалогу поширення файлу */
    data class ShareFile(val uri: Uri) : AdminEffect()
}
