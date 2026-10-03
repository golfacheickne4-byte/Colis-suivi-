package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Carrier
import com.example.data.model.PackageEntity
import com.example.data.model.PackageStatus
import com.example.data.model.PackageWithEvents
import com.example.data.model.TrackingEventEntity
import com.example.data.repository.PackageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    DETAIL,
    ADD_EDIT,
    STATISTICS,
    CARRIERS
}

enum class FilterTab(val label: String) {
    ALL("Tous"),
    IN_PROGRESS("En cours"),
    DELIVERED("Livrés"),
    PICKUP("Points Relais"),
    ARCHIVED("Archivés")
}

class PackageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PackageRepository
    val currentScreen = MutableStateFlow(Screen.HOME)
    val selectedPackageId = MutableStateFlow<Long?>(null)
    val packageToEdit = MutableStateFlow<PackageEntity?>(null)

    val filterTab = MutableStateFlow(FilterTab.ALL)
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)

    val userMessage = MutableStateFlow<String?>(null)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PackageRepository(database.packageDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val allPackages: StateFlow<List<PackageWithEvents>> = repository.allPackages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPackages: StateFlow<List<PackageWithEvents>> = combine(
        allPackages,
        filterTab,
        searchQuery,
        selectedCategory
    ) { packages, tab, query, category ->
        packages.filter { pkgWithEvents ->
            val pkg = pkgWithEvents.packageItem

            // Tab filtering
            val matchesTab = when (tab) {
                FilterTab.ALL -> !pkg.isArchived
                FilterTab.IN_PROGRESS -> !pkg.isArchived && pkg.status.isInProgress
                FilterTab.DELIVERED -> !pkg.isArchived && pkg.status.isDelivered
                FilterTab.PICKUP -> !pkg.isArchived && pkg.status == PackageStatus.AVAILABLE_FOR_PICKUP
                FilterTab.ARCHIVED -> pkg.isArchived
            }

            // Category filtering
            val matchesCategory = category == null || pkg.category.equals(category, ignoreCase = true)

            // Search query matching
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                pkg.title.lowercase().contains(q) ||
                        pkg.trackingNumber.lowercase().contains(q) ||
                        pkg.carrier.displayName.lowercase().contains(q) ||
                        pkg.sender.lowercase().contains(q) ||
                        pkg.recipient.lowercase().contains(q) ||
                        pkg.category.lowercase().contains(q)
            }

            matchesTab && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPackage: StateFlow<PackageWithEvents?> = combine(
        allPackages,
        selectedPackageId
    ) { packages, id ->
        if (id == null) null else packages.find { it.packageItem.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun navigateToHome() {
        currentScreen.value = Screen.HOME
        selectedPackageId.value = null
        packageToEdit.value = null
    }

    fun navigateToDetail(packageId: Long) {
        selectedPackageId.value = packageId
        currentScreen.value = Screen.DETAIL
    }

    fun navigateToAdd() {
        packageToEdit.value = null
        currentScreen.value = Screen.ADD_EDIT
    }

    fun navigateToEdit(packageEntity: PackageEntity) {
        packageToEdit.value = packageEntity
        currentScreen.value = Screen.ADD_EDIT
    }

    fun navigateToStatistics() {
        currentScreen.value = Screen.STATISTICS
    }

    fun navigateToCarriers() {
        currentScreen.value = Screen.CARRIERS
    }

    fun setFilterTab(tab: FilterTab) {
        filterTab.value = tab
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    fun savePackage(
        packageEntity: PackageEntity,
        initialEventLocation: String = "",
        initialEventTitle: String = ""
    ) {
        viewModelScope.launch {
            if (packageEntity.id == 0L) {
                repository.addPackage(packageEntity)
                userMessage.value = "Colis ajouté au suivi !"
            } else {
                repository.updatePackage(packageEntity)
                userMessage.value = "Colis mis à jour"
            }
            navigateToHome()
        }
    }

    fun deletePackage(packageId: Long) {
        viewModelScope.launch {
            repository.deletePackage(packageId)
            userMessage.value = "Colis supprimé"
            if (currentScreen.value == Screen.DETAIL) {
                navigateToHome()
            }
        }
    }

    fun togglePin(packageId: Long, currentPinState: Boolean) {
        viewModelScope.launch {
            repository.setPinned(packageId, !currentPinState)
            userMessage.value = if (!currentPinState) "Colis épinglé" else "Colis désépinglé"
        }
    }

    fun toggleArchive(packageId: Long, currentArchiveState: Boolean) {
        viewModelScope.launch {
            repository.setArchived(packageId, !currentArchiveState)
            userMessage.value = if (!currentArchiveState) "Colis archivé" else "Colis désarchivé"
            if (currentScreen.value == Screen.DETAIL) {
                navigateToHome()
            }
        }
    }

    fun simulateNextStep(pkgWithEvents: PackageWithEvents) {
        viewModelScope.launch {
            val advanced = repository.simulateNextStep(pkgWithEvents)
            if (advanced) {
                userMessage.value = "Statut du colis actualisé !"
            } else {
                userMessage.value = "Le colis est déjà livré."
            }
        }
    }

    fun addManualTrackingEvent(
        packageId: Long,
        title: String,
        location: String,
        description: String,
        status: PackageStatus
    ) {
        viewModelScope.launch {
            repository.addTrackingEvent(packageId, title, location, description, status)
            userMessage.value = "Nouvelle étape ajoutée à l'historique"
        }
    }

    fun clearUserMessage() {
        userMessage.value = null
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
            userMessage.value = "Données d'exemples réinitialisées"
        }
    }
}
