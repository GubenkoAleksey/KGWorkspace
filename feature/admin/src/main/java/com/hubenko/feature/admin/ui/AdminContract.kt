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
 * @property selectedTab Поточна активна вкладка/екран.
 * @property isEmployeeDialogOpen Чи відкритий діалог створення/редагування співробітника.
 * @property editingEmployee Об'єкт співробітника, який редагується (null для створення нового).
 */
data class AdminState(
    val employees: List<Employee> = emptyList(),
    val statuses: List<EmployeeStatus> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: AdminTab = AdminTab.DASHBOARD,
    val isEmployeeDialogOpen: Boolean = false,
    val editingEmployee: Employee? = null,
    val isDeleteStatusesDialogOpen: Boolean = false
) : ViewState

/**
 * Вкладки/Екрани панелі адміністратора.
 */
enum class AdminTab(val title: String) {
    DASHBOARD("Панель адміністратора"),
    EMPLOYEES("Керування працівниками"),
    SCHEDULE("Розклад сповіщень"),
    STATUSES("Статуси працівників")
}

/**
 * Інтенції (наміри) користувача на екрані адміністратора.
 */
sealed class AdminIntent : ViewIntent {
    /** Завантажити початкові дані (співробітників та статуси) */
    data object LoadData : AdminIntent()
    
    /** Зміна активного екрана/вкладки */
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

    /** Вибір співробітника для налаштування розкладу */
    data class OnEmployeeSelectedForSchedule(val employeeId: String) : AdminIntent()

    /** Обробка натиску назад */
    data object OnBackClick : AdminIntent()
}

/**
 * Одноразові ефекти екрана адміністратора.
 */
sealed class AdminEffect : ViewSideEffect {
    /** Відображення текстового повідомлення */
    data class ShowToast(val message: String) : AdminEffect()
    
    /** Навігація назад (вихід з панелі) */
    data object NavigateBack : AdminEffect()

    /** Навігація до екрана налаштування розкладу */
    data class NavigateToReminderSettings(val employeeId: String) : AdminEffect()

    /** Відкриття діалогу поширення файлу */
    data class ShareFile(val uri: Uri) : AdminEffect()
}
