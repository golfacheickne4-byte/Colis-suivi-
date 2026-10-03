package com.example.data.model

enum class Carrier(
    val displayName: String,
    val brandColorHex: Long,
    val textColorHex: Long,
    val shortName: String,
    val trackingUrlTemplate: String,
    val customerServicePhone: String,
    val prefixes: List<String>
) {
    COLISSIMO(
        displayName = "Colissimo (La Poste)",
        brandColorHex = 0xFFFFCC00,
        textColorHex = 0xFF1E293B,
        shortName = "Colissimo",
        trackingUrlTemplate = "https://www.laposte.fr/outils/suivre-vos-envois?code=%s",
        customerServicePhone = "3631",
        prefixes = listOf("6A", "6C", "6M", "6Q", "7T", "8R", "9V", "CC", "CB", "CL", "CP")
    ),
    CHRONOPOST(
        displayName = "Chronopost Express",
        brandColorHex = 0xFF004B93,
        textColorHex = 0xFFFFFFFF,
        shortName = "Chronopost",
        trackingUrlTemplate = "https://www.chronopost.fr/tracking-no-cms/suivi-page?listeNumerosLT=%s",
        customerServicePhone = "09 69 391 391",
        prefixes = listOf("FW", "XT", "DW", "EE", "XJ")
    ),
    MONDIAL_RELAY(
        displayName = "Mondial Relay",
        brandColorHex = 0xFFC41230,
        textColorHex = 0xFFFFFFFF,
        shortName = "Mondial Relay",
        trackingUrlTemplate = "https://www.mondialrelay.fr/suivi-de-colis?numeroExpedition=%s",
        customerServicePhone = "09 69 32 23 32",
        prefixes = listOf("MR", "8", "9")
    ),
    DPD(
        displayName = "DPD France",
        brandColorHex = 0xFFDC0032,
        textColorHex = 0xFFFFFFFF,
        shortName = "DPD",
        trackingUrlTemplate = "https://trace.dpd.fr/fr/trace/%s",
        customerServicePhone = "09 70 80 85 66",
        prefixes = listOf("068", "250", "099")
    ),
    DHL(
        displayName = "DHL Express",
        brandColorHex = 0xFFFFCC00,
        textColorHex = 0xFFD40511,
        shortName = "DHL",
        trackingUrlTemplate = "https://www.dhl.com/fr-fr/home/suivi.html?tracking-id=%s",
        customerServicePhone = "0 825 10 00 80",
        prefixes = listOf("JJD", "JVGL", "DHL")
    ),
    UPS(
        displayName = "UPS",
        brandColorHex = 0xFF351C15,
        textColorHex = 0xFFFFB500,
        shortName = "UPS",
        trackingUrlTemplate = "https://www.ups.com/track?tracknum=%s",
        customerServicePhone = "01 73 00 66 61",
        prefixes = listOf("1Z")
    ),
    FEDEX(
        displayName = "FedEx",
        brandColorHex = 0xFF4D148C,
        textColorHex = 0xFFFFFFFF,
        shortName = "FedEx",
        trackingUrlTemplate = "https://www.fedex.com/fedextrack/?trknbr=%s",
        customerServicePhone = "0825 886 886",
        prefixes = listOf("FDX", "7")
    ),
    AMAZON(
        displayName = "Amazon Logistics",
        brandColorHex = 0xFF232F3E,
        textColorHex = 0xFFFF9900,
        shortName = "Amazon",
        trackingUrlTemplate = "https://www.amazon.fr/gp/your-account/order-history",
        customerServicePhone = "0800 94 77 15",
        prefixes = listOf("TBA", "TBC", "TBM")
    ),
    GLS(
        displayName = "GLS France",
        brandColorHex = 0xFF002D72,
        textColorHex = 0xFFFDB813,
        shortName = "GLS",
        trackingUrlTemplate = "https://gls-group.com/FR/fr/suivi-colis?match=%s",
        customerServicePhone = "0806 00 60 06",
        prefixes = listOf("GLS")
    ),
    RELAIS_COLIS(
        displayName = "Relais Colis",
        brandColorHex = 0xFFE30613,
        textColorHex = 0xFFFFFFFF,
        shortName = "Relais Colis",
        trackingUrlTemplate = "https://www.relaiscolis.com/suivi-de-colis/recherche/%s",
        customerServicePhone = "09 70 25 22 31",
        prefixes = listOf("RC")
    ),
    AUTRE(
        displayName = "Autre transporteur",
        brandColorHex = 0xFF64748B,
        textColorHex = 0xFFFFFFFF,
        shortName = "Autre",
        trackingUrlTemplate = "https://www.google.com/search?q=suivi+colis+%s",
        customerServicePhone = "",
        prefixes = emptyList()
    );

    fun buildTrackingUrl(trackingNumber: String): String {
        return try {
            String.format(trackingUrlTemplate, trackingNumber.trim())
        } catch (_: Exception) {
            trackingUrlTemplate
        }
    }

    companion object {
        fun detectFromTrackingNumber(number: String): Carrier {
            val clean = number.trim().uppercase()
            if (clean.isEmpty()) return AUTRE

            // Check standard prefixes
            if (clean.startsWith("1Z")) return UPS
            if (clean.startsWith("TBA") || clean.startsWith("TBC") || clean.startsWith("TBM")) return AMAZON
            if (clean.startsWith("JJD") || clean.startsWith("JVGL")) return DHL
            if (clean.startsWith("FW") || clean.startsWith("XT") || clean.startsWith("DW") || clean.startsWith("XJ")) return CHRONOPOST
            if (clean.startsWith("068") || clean.startsWith("250")) return DPD

            // French Postal / Colissimo checks:
            // Often starts with 6A, 6C, 6M, 7T, 8R, 9V or 13 chars ending in FR
            if (clean.endsWith("FR") && clean.length == 13) return COLISSIMO
            if (clean.startsWith("6A") || clean.startsWith("6C") || clean.startsWith("6M") ||
                clean.startsWith("7T") || clean.startsWith("8R") || clean.startsWith("9V")) return COLISSIMO

            // Mondial Relay often has 8-10 digits or starts with MR
            if (clean.startsWith("MR")) return MONDIAL_RELAY
            if (clean.length in 8..10 && clean.all { it.isDigit() }) return MONDIAL_RELAY

            return AUTRE
        }
    }
}
