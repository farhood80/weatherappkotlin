import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

/**
 * 🌤️ Weather App - Kotlin Console Application
 *
 * Fetches real-time weather data from Weatherstack API
 * Provides beautiful console output with weather information
 *
 * @author Your Name
 * @version 1.0
 */
class WeatherstackApp {
    // TODO: Replace with your Weatherstack API key from https://weatherstack.com/
    private val apiKey = "YOUR_API_KEY_HERE"
    private val baseUrl = "http://api.weatherstack.com/current"

    private val weatherEmojis = mapOf(
        "Sunny" to "☀️", "Clear" to "🌙", "Partly cloudy" to "⛅",
        "Cloudy" to "☁️", "Overcast" to "☁️", "Mist" to "🌫️",
        "Light rain" to "🌦️", "Rain" to "🌧️", "Heavy rain" to "⛈️",
        "Thunderstorm" to "⛈️", "Snow" to "❄️", "Sleet" to "🌨️"
    )

    /**
     * Fetches weather data for a given city
     * @param city The city name to search for
     * @return Boolean indicating success or failure
     */
    fun getWeatherByCity(city: String): Boolean {
        return try {
            val url = "$baseUrl?access_key=$apiKey&query=$city&units=m"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            println("🌐 Fetching weather data for '$city'...")

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                if (json.has("success") && json.getBoolean("success") == false) {
                    val errorInfo = json.optJSONObject("error")?.getString("info") ?: "Unknown error"
                    println("❌ API Error: $errorInfo")
                    return false
                }

                displayWeather(json)
                true
            } else {
                println("❌ HTTP Error: ${connection.responseCode}")
                false
            }
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
            false
        }
    }

    /**
     * Displays weather information in a formatted way
     * @param json JSON object containing weather data
     */
    private fun displayWeather(json: JSONObject) {
        val location = json.getJSONObject("location")
        val current = json.getJSONObject("current")

        val cityName = location.getString("name")
        val country = location.getString("country")
        val region = location.getString("region")
        val temperature = current.getInt("temperature")
        val feelsLike = current.getInt("feelslike")
        val description = current.getJSONArray("weather_descriptions").getString(0)
        val humidity = current.getInt("humidity")
        val pressure = current.getInt("pressure")
        val windSpeed = current.getInt("wind_speed")
        val windDir = current.getString("wind_dir")
        val visibility = current.getInt("visibility")
        val uvIndex = current.getInt("uv_index")

        val emoji = weatherEmojis[description] ?: "🌤️"

        println("\n" + "═".repeat(55))
        println("$emoji  WEATHER IN ${cityName.uppercase()}, $country  $emoji")
        println("═".repeat(55))
        println("🌡️  Temperature: ${temperature}°C")
        println("💨  Feels like: ${feelsLike}°C")
        println("📊  Condition: $description")
        println("💧  Humidity: ${humidity}%")
        println("📏  Pressure: ${pressure} hPa")
        println("🌬️  Wind Speed: ${windSpeed} km/h")
        println("🧭  Wind Direction: $windDir")
        println("👁️  Visibility: ${visibility} km")
        println("☀️  UV Index: ${uvIndex}")
        println("📍  Region: $region")
        println("═".repeat(55))
    }
}

/**
 * Main function - Application entry point
 */
fun main() {
    val weatherApp = WeatherstackApp()

    val popularCities = listOf(
        "New York", "London", "Tokyo", "Paris", "Berlin",
        "Sydney", "Toronto", "Dubai", "Singapore", "Mumbai",
        "Moscow", "Beijing", "Seoul", "Istanbul", "Rome",
        "Madrid", "Cairo", "Bangkok", "Jakarta", "Los Angeles"
    )

    println("🌍 Welcome to Kotlin Weather App!")
    println("=================================")

    while (true) {
        println("\n📋 Main Menu:")
        println("1. 🔍 Search by city name")
        println("2. 🏙️  Popular cities")
        println("3. ℹ️   About this app")
        println("4. ❌ Exit")
        print("\nChoose option (1-4): ")

        when (readLine()) {
            "1" -> {
                print("Enter city name: ")
                val city = readLine()?.trim()
                if (!city.isNullOrEmpty()) {
                    if (!weatherApp.getWeatherByCity(city)) {
                        println("💡 Tip: Check the city name or try a major city nearby")
                    }
                } else {
                    println("❌ Please enter a city name")
                }
            }
            "2" -> {
                println("\n🏙️  Popular Cities:")
                println("─".repeat(30))
                popularCities.chunked(5).forEach { chunk ->
                    chunk.forEachIndexed { index, city ->
                        val number = popularCities.indexOf(city) + 1
                        print("$number. $city".padEnd(15))
                    }
                    println()
                }
                print("\nSelect city (1-${popularCities.size}): ")
                val choice = readLine()?.toIntOrNull()

                if (choice != null && choice in 1..popularCities.size) {
                    val city = popularCities[choice - 1]
                    weatherApp.getWeatherByCity(city)
                } else {
                    println("❌ Invalid selection")
                }
            }
            "3" -> {
                println("\nℹ️   About Kotlin Weather App:")
                println("─".repeat(30))
                println("Version: 1.0")
                println("Author: Your Name")
                println("API: Weatherstack")
                println("Language: Kotlin")
                println("Build Tool: Maven")
                println("\nA simple console app demonstrating:")
                println("• API integration")
                println("• JSON parsing")
                println("• Console UI design")
                println("• Error handling")
            }
            "4" -> {
                println("\n👋 Thank you for using Kotlin Weather App!")
                println("⭐ If you liked this project, star it on GitHub!")
                return
            }
            else -> {
                println("❌ Invalid option. Please choose 1-4.")
            }
        }

        println("\nPress Enter to continue...")
        readLine()
    }
}