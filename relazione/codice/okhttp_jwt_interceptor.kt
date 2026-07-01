class AuthInterceptor(private val prefs: BackendPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.jwtToken
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
