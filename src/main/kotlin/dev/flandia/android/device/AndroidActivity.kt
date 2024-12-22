package dev.flandia.android.device

data class AndroidActivity(
    val packageName: String,
    val activityName: String,
) {
    override fun toString(): String {
        if (activityName.isEmpty()) {
            return packageName
        }
        return "$packageName/$activityName"
    }
}