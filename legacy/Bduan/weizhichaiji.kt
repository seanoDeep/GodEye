// 原始文件归档: Bduan/weizhichaiji.kt
class LocationCollector(context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    
    fun startLocationUpdates(callback: (LocationData) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .build()
        
        fusedClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    callback(LocationData(it.latitude, it.longitude, it.accuracy))
                }
            }
        }, Looper.getMainLooper())
    }
}
// 原始文件归档
