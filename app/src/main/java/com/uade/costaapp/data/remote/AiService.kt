package com.uade.costaapp.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

data class AiAnalysisData(
    val summary: String,
    val positives: List<String>,
    val warnings: List<String>
)

class AiService @Inject constructor() {
    suspend fun getAnalysis(prompt: String): AiAnalysisData {
        return try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "mock-key" // Falla intencionalmente
            )
            val response = generativeModel.generateContent(prompt)
            // Aquí iría el parseo real si la key existiese
            throw Exception("Forced to mock")
        } catch (e: Exception) {
            getMockAnalysis()
        }
    }

    private fun getMockAnalysis() = AiAnalysisData(
        summary = "Propiedad ideal para renta temporal de verano o inversión a mediano plazo.",
        positives = listOf(
            "Ubicación privilegiada cerca de la playa y zona céntrica.",
            "Excelente relación precio/superficie en el mercado actual."
        ),
        warnings = listOf(
            "Requiere mantenimiento anual preventivo por la humedad y salitre de la zona costera."
        )
    )
}
