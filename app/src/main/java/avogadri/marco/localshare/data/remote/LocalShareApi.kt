package avogadri.marco.localshare.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LocalShareApi {

    @POST("auth/register")
    suspend fun register(): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("history/")
    suspend fun recordTransfer(@Body body: RecordTransferRequest)

    @GET("history/")
    suspend fun getGroupHistory(@Query("group_code") groupCode: String): List<BackendTransferResponse>

    @POST("groups/create")
    suspend fun createGroup(): GroupResponse

    @POST("groups/join")
    suspend fun joinGroup(@Body body: GroupResponse): GroupResponse
}
