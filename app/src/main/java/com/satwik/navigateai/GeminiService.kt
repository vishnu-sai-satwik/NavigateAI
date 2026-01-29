package com.satwik.navigateai

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream

// --- 1. THE DATA MODELS (What Google expects) ---

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    @SerializedName("inline_data") val inlineData: InlineData? = null
)

data class InlineData(
    @SerializedName("mime_type") val mimeType: String = "image/jpeg",
    val data: String // The Base64 image
)

// --- 2. THE RESPONSE (What Google sends back) ---

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)

// --- 3. THE INTERFACE (The Connection) ---

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- 4. THE HELPER OBJECT (To make it easy to use) ---

object GeminiHelper {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun analyzeTimetable(bitmap: Bitmap): String {
        // 1. Convert Image to Base64 String
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        // 2. Create the Request
        // We prompt Gemini to be a JSON machine.
        val prompt = "Look at this timetable. Extract the NEXT upcoming class, its Room Number, and the Time. " +
                "Return ONLY a JSON string like this: " +
                "{ \"subject\": \"Maths\", \"room\": \"304\", \"time\": \"10:00\" }. " +
                "Do not include markdown formatting."

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(data = base64Image))
                    )
                )
            )
        )

        // 3. Send to Google
        try {
            val response = api.generateContent("AIzaSyD5vFMU3FfdDz-NzbHaL3gM4NknQ-ZLtm8", request)
            // Extract the text from the response
            return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "{ \"subject\": \"Error\", \"room\": \"-\", \"time\": \"-\" }"
        } catch (e: Exception) {
            e.printStackTrace()
            return "{ \"subject\": \"Network Error\", \"room\": \"Check Wifi\", \"time\": \"-\" }"
        }
    }
}