// 原始文件归档: Bduan/dongtai.kt
private fun requestPermissions() {
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
    ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE)
}
// 原始文件归档
