package com.example.data.model

enum class PackageStatus(
    val label: String,
    val description: String,
    val stepIndex: Int, // 0: Préparation, 1: Expédié, 2: En acheminement, 3: En livraison / Relais, 4: Livré
    val isFinal: Boolean,
    val colorHex: Long,
    val lightBgColorHex: Long
) {
    ORDER_CONFIRMED(
        label = "Commande confirmée",
        description = "Le vendeur prépare votre colis pour expédition",
        stepIndex = 0,
        isFinal = false,
        colorHex = 0xFF64748B,
        lightBgColorHex = 0xFFF1F5F9
    ),
    INFO_RECEIVED(
        label = "Étiquette créée",
        description = "L'expéditeur a créé l'étiquette d'envoi. En attente de remise au transporteur.",
        stepIndex = 1,
        isFinal = false,
        colorHex = 0xFF3B82F6,
        lightBgColorHex = 0xFFDBEAFE
    ),
    IN_TRANSIT(
        label = "En acheminement",
        description = "Votre colis circule sur le réseau de distribution",
        stepIndex = 2,
        isFinal = false,
        colorHex = 0xFF8B5CF6,
        lightBgColorHex = 0xFFEDE9FE
    ),
    OUT_FOR_DELIVERY(
        label = "En cours de livraison",
        description = "Votre colis est entre les mains du livreur et sera livré aujourd'hui",
        stepIndex = 3,
        isFinal = false,
        colorHex = 0xFFF59E0B,
        lightBgColorHex = 0xFFFEF3C7
    ),
    AVAILABLE_FOR_PICKUP(
        label = "Disponible au point relais",
        description = "Votre colis vous attend au point de retrait avec une pièce d'identité",
        stepIndex = 3,
        isFinal = false,
        colorHex = 0xFF06B6D4,
        lightBgColorHex = 0xFFCFFAFE
    ),
    DELIVERED(
        label = "Livré",
        description = "Colis remis en main propre ou déposé en boîte aux lettres",
        stepIndex = 4,
        isFinal = true,
        colorHex = 0xFF10B981,
        lightBgColorHex = 0xFFD1FAE5
    ),
    EXCEPTION(
        label = "Incident / Retard",
        description = "Retard d'acheminement ou anomalie détectée lors du transport",
        stepIndex = -1,
        isFinal = false,
        colorHex = 0xFFEF4444,
        lightBgColorHex = 0xFFFEE2E2
    );

    val isDelivered: Boolean get() = this == DELIVERED
    val isInProgress: Boolean get() = this != DELIVERED && this != EXCEPTION
}
