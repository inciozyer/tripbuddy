package com.example.bitirme_projesi

import android.Manifest
import com.example.bitirme_projesi.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bitirme_projesi.ui.theme.Bitirme_projesiTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import android.widget.Toast
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*



// FIREBASE IMPORTLARI
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.tasks.await

// --- RENK PALETİ ---
// --- YENİ "COOL TURKUAZ" PALETİ ---
// --- YENİ FERAH TURKUAZ PALETİ ---
val TripBuddyBlue = Color(0xFF38A3A5) // O koyu yeşil yerine, çok daha canlı, ferah ve modern bir turkuaz
val RouteLineColor = Color(0xFF2C7A7B) // Butonlar veya rota çizgisi için ana turkuazın bir tık daha tok/kontrast hali
val TripBuddyOffWhite = Color(0xFFF4F6F8) // Zemin için yine o göz yormayan, yumuşak kırık beyaz
val TripBuddyWhite = Color.White

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(applicationContext, "api")
        }
        enableEdgeToEdge()
        setContent {
            Bitirme_projesiTheme {
                Box(modifier = Modifier.fillMaxSize().background(TripBuddyOffWhite)) {
                    Bitirme_projesiApp()
                }
            }
        }
    }
}

// --- DİL YÖNETİMİ ---
enum class AppLanguage(val code: String, val flagEmoji: String, val displayName: String, val locale: Locale) {
    TR("tr", "🇹🇷", "Türkçe", Locale("tr", "TR")),
    EN("en", "🇬🇧", "English", Locale.ENGLISH),
    DE("de", "🇩🇪", "Deutsch", Locale.GERMAN),
    FR("fr", "🇫🇷", "Français", Locale.FRENCH),
    ES("es", "🇪🇸", "Español", Locale("es", "ES"))
}

data class AppStrings(
    val welcome: String, val login: String, val signup: String, val username: String,
    val email: String, val password: String, val noAccount: String, val alreadyMember: String,
    val errorEmpty: String, val errorLogin: String, val errorEmailExists: String,
    val home: String, val map: String, val profile: String, val createRoute: String,
    val planDream: String, val whereTo: String, val dateRange: String, val dateStart: String,
    val dateEnd: String, val popularPlaces: String, val seeAll: String, val planTripButton: String,
    val hello: String, val plannedTrips: String, val visitedCities: String, val settings: String,
    val language: String, val logout: String, val currentLocation: String, val locationPlaceholder: String,
    val historyTitle: String, val listen: String, val stop: String, val detailBtn: String,
    val visitHours: String, val streetView: String, val addToRoute: String, val daysSelected: String,
    val magicRoute: String,
    val routeCreated: String,
    val aiMoreInfo: String,
    val aiAssistant: String,
    val errorLoading: String,
    val rotayiHaritadaGor: String,
    val aiRehberInit: String,
    val appSettings: String,
    val settingsMenu: String,
    val myFriends: String,
    val sharedRoutes: String,
    val profileEditTitle: String, val personalInfo: String, val nameHint: String,
    val surnameHint: String, val phoneHint: String, val saveInfoBtn: String,
    val securityTitle: String, val currentPassHint: String, val newPassHint: String,
    val updatePassBtn: String, val friendsTitle: String, val searchUserHint: String,
    val searchResultsTitle: String, val currentFriendsTitle: String, val noFriendsDesc: String,
    val addBtn: String, val incomingRoutesTitle: String, val noRoutesDesc: String,
    val routesFromFriendsTitle: String, val routeSentText: String, val totalStopsText: String,
    val inspectRouteBtn: String,
    val mapRouteSelected: String,
    val mapPlacesFound: String,
    val mapRouteOrder: String,
    val startNavBtn: String,
    val placesBtn: String, val hotelsBtn: String,
    val aiDialogTitle: String, val aiDialogHint: String,
    val aiDialogAsk: String, val aiDialogCancel: String, val aiDialogLoading: String,
    val aiSeeOnMapBtn: String,
    val magicRouteDesc: String, val magicRouteDrawBtn: String, val clearBtn: String, val routeClearedMsg: String,
    val bookNowBtn: String, val reviewsTitle: String, val giveRatingText: String,
    val reviewPlaceholder: String, val submitReviewBtn: String, val noReviewsYet: String,
    val shareRouteTitle: String, val sendRouteBtn: String,
    val notificationsTitle: String
)

val trStrings = AppStrings(
    "TripBuddy\nHoşgeldiniz", "Giriş Yap", "Kayıt Ol", "Kullanıcı Adı", "E-posta", "Şifre", "Hesabın yok mu? ", "Zaten üye misin? ", "Alanları doldurun.", "Bilgiler hatalı!", "E-posta kayıtlı!", "Planla", "Harita", "Profil", "Rotanı Oluştur", "Hayalindeki tatili planlamaya başla", "Nereye Gidiyorsun?", "Tarih Aralığı", "Gidiş", "Dönüş", "Gezilecek Yerler", "Tümünü Gör", "ROTAYI HARİTADA GÖR", "Merhaba", "Planlanan Seyahatler", "Ziyaret Edilen İller", "Ayarlar / Dil", "Dil Seçimi", "Çıkış Yap", "Konumun & Rota", "Harita yükleniyor...", "Tarihçe & Bilgi", "Dinle", "Durdur", "İncele", "Ziyaret Saatleri", "Sokak Görünümü", "Rotaya Ekle", "Gün Seçildi", "Sihirli Rota", "En Popüler Rota Oluşturuldu! ⭐",
    // 👇 EKSİK OLAN 5 METİN BURAYA EKLENDİ 👇
    "Yapay Zeka ile Daha Fazla Bilgi Al",
    "TripBuddy Asistan",
    "Bilgi alınırken bir hata oluştu.",
    "Rotayı Haritada Gör",
    "Merhaba! Ben TripBuddy akıllı asistanın. Sana nasıl yardımcı olabilirim?",
    "Uygulama Ayarları",
    "Ayarlar",
    "Arkadaşlarım",
    "Gelen Rotalar",
    "Profil Düzenleme", "Kişisel Bilgiler", "İsim", "Soyisim", "Telefon Numarası", "Bilgileri Kaydet", "Güvenlik", "Mevcut Şifre", "Yeni Şifre", "Şifreyi Güncelle", "Arkadaşlarım", "Kullanıcı adı ile ara...", "Arama Sonuçları", "Mevcut Arkadaşlar", "Henüz arkadaşın yok. Yukarıdan arayabilirsin!", "Ekle", "Gelen Rotalar", "Henüz kimse seninle bir rota paylaşmadı.", "Arkadaşlarından Gelenler", "bir rota gönderdi!", "Toplam durak sayısı:", "Rotayı İncele",
    "📍 Rota: %d Durak Seçildi",
    "📍 Haritada %d Mekan Bulundu",
    "📌 Rota Sıralaması (%d Durak)",
    "NAVİGASYONU BAŞLAT",
    "Mekanlar", "Oteller",
    "AI Tur Rehberi", "Örn: Buranın en meşhur yemeği ne?", "Sor", "Kapat", "TripBuddy mekanları haritaya hazırlıyor... 🤔",
    "Haritada Gör 🗺️",
    "Şehirdeki en iyi puanlı yerlerden sana özel bir rota çizeceğiz. Kaç durak istersin?", "Rotayı Çiz", "🧹 Temizle", "Rota temizlendi! 🧹",
    "Rezervasyon Yap", "Değerlendirmeler", "Puan Verin (%s):", "Deneyiminizi diğer gezginlerle paylaşın...", "Gönder", "Henüz yorum yapılmamış. İlk yorumu siz yapın!",
     "Rotayı Arkadaşınla Paylaş", "Gönder", notificationsTitle = "Bildirimler"
)

val enStrings = AppStrings(
    "Welcome to\nTripBuddy", "Log In", "Sign Up", "Username", "Email", "Password", "No account? ", "Already a member? ", "Fill fields.", "Invalid creds!", "Email exists!", "Plan", "Map", "Profile", "Create Route", "Start planning", "Where to?", "Dates", "Start", "End", "Places to Visit", "See All", "VIEW ROUTE ON MAP", "Hello", "Planned Trips", "Visited Cities", "Settings", "Language", "Log Out", "Location & Route", "Loading map...", "History & Info", "Listen", "Stop", "Details", "Visiting Hours", "Street View", "Add to Route", "Days Selected", "Magic Route", "Most Popular Route Created! ⭐",
    // 👇 EKSİK OLAN 5 METİN BURAYA EKLENDİ 👇
    "Get More Info with AI",
    "TripBuddy Assistant",
    "An error occurred while fetching information.",
    "See Route on Map",
    "Hello! I am your TripBuddy smart assistant. How can I help you today?",
    "App Settings",
    "Settings",
    "My Friends",
    "Shared Routes",
    "Edit Profile", "Personal Information", "First Name", "Last Name", "Phone Number", "Save Information", "Security", "Current Password", "New Password", "Update Password", "My Friends", "Search by username...", "Search Results", "Current Friends", "You have no friends yet. Search above!", "Add", "Shared Routes", "No one has shared a route with you yet.", "Routes from Friends", "sent you a route!", "Total stops:", "Inspect Route",
    "📍 Route: %d Stops Selected", "📍 %d Places Found on Map", "📌 Route Order (%d Stops)", "START NAVIGATION",
    "Places", "Hotels",
    "AI Tour Guide", "E.g.: What is the most famous food here?", "Ask", "Close", "TripBuddy is preparing the map... 🤔",
    "See on Map 🗺️",
    "We will draw a custom route for you from the top-rated places in the city. How many stops do you want?", "Draw Route", "🧹 Clear", "Route cleared! 🧹","Book Now", "Reviews", "Rate (%s):", "Share your experience with other travelers...", "Submit", "No reviews yet. Be the first to review!",
    "Share Route with Friend", "Send", notificationsTitle = "Notifications",
)

// 🇩🇪 ALMANCA TAM ÇEVİRİ PAKETİ
val deStrings = AppStrings(
    "Willkommen bei\nTripBuddy", "Anmelden", "Registrieren", "Benutzername", "E-Mail", "Passwort", "Kein Konto? ", "Bereits Mitglied? ", "Felder ausfüllen.", "Ungültige Daten!", "E-Mail existiert!", "Planen", "Karte", "Profil", "Route erstellen", "Planung starten", "Wohin?", "Daten", "Start", "Ende", "Sehenswürdigkeiten", "Alle ansehen", "ROUTE AUF KARTE ANSEHEN", "Hallo", "Geplante Reisen", "Besuchte Städte", "Einstellungen", "Sprache", "Abmelden", "Standort & Route", "Karte wird geladen...", "Geschichte & Info", "Zuhören", "Stopp", "Details", "Besuchszeiten", "Street View", "Zur Route hinzufügen", "Tage ausgewählt", "Magische Route", "Beliebteste Route erstellt! ⭐",
    "Mehr Infos mit KI", "TripBuddy Assistent", "Fehler beim Abrufen der Informationen.", "Route auf Karte ansehen", "Hallo! Ich bin dein TripBuddy-Assistent. Wie kann ich dir helfen?", "App-Einstellungen", "Einstellungen", "Meine Freunde", "Geteilte Routen",
    "Profil bearbeiten", "Persönliche Daten", "Vorname", "Nachname", "Telefonnummer", "Daten speichern", "Sicherheit", "Aktuelles Passwort", "Neues Passwort", "Passwort aktualisieren", "Meine Freunde", "Nach Benutzernamen suchen...", "Suchergebnisse", "Aktuelle Freunde", "Du hast noch keine Freunde. Suche oben!", "Hinzufügen", "Geteilte Routen", "Noch niemand hat eine Route mit dir geteilt.", "Routen von Freunden", "hat dir eine Route gesendet!", "Gesamtstopps:", "Route prüfen",
    "📍 Route: %d Stopps ausgewählt", "📍 %d Orte auf der Karte gefunden", "📌 Routenreihenfolge (%d Stopps)", "NAVIGATION STARTEN","Orte", "Hotels","KI-Reiseführer", "Z.B.: Was ist das berühmteste Essen hier?", "Fragen", "Schließen", "TripBuddy bereitet die Karte vor... 🤔","Auf Karte ansehen 🗺️","Wir erstellen für dich eine individuelle Route aus den am besten bewerteten Orten der Stadt. Wie viele Stopps möchtest du?", "Route zeichnen", "🧹 Löschen", "Route gelöscht! 🧹","Jetzt Buchen", "Bewertungen", "Bewerten (%s):", "Teile deine Erfahrungen mit anderen Reisenden...", "Senden", "Noch keine Bewertungen. Sei der Erste!", "Route mit Freund teilen", "Senden", notificationsTitle = "Benachrichtigungen",
)

// 🇫🇷 FRANSIZCA TAM ÇEVİRİ PAKETİ
val frStrings = AppStrings(
    "Bienvenue sur\nTripBuddy", "Connexion", "S'inscrire", "Nom d'utilisateur", "E-mail", "Mot de passe", "Pas de compte ? ", "Déjà membre ? ", "Remplir les champs.", "Identifiants invalides !", "E-mail existant !", "Planifier", "Carte", "Profil", "Créer un itinéraire", "Commencer", "Où allez-vous ?", "Dates", "Début", "Fin", "Lieux à visiter", "Voir tout", "VOIR L'ITINÉRAIRE SUR LA CARTE", "Bonjour", "Voyages prévus", "Villes visitées", "Paramètres", "Langue", "Déconnexion", "Lieu & Itinéraire", "Chargement de la carte...", "Histoire & Infos", "Écouter", "Arrêter", "Détails", "Heures de visite", "Street View", "Ajouter à l'itinéraire", "Jours sélectionnés", "Route Magique", "Itinéraire le plus populaire créé ! ⭐",
    "Plus d'infos avec l'IA", "Assistant TripBuddy", "Erreur lors de la récupération des infos.", "Voir l'itinéraire sur la carte", "Bonjour ! Je suis votre assistant TripBuddy. Comment puis-je vous aider ?", "Paramètres de l'app", "Paramètres", "Mes amis", "Itinéraires partagés",
    "Modifier le profil", "Informations personnelles", "Prénom", "Nom", "Numéro de téléphone", "Enregistrer les infos", "Sécurité", "Mot de passe actuel", "Nouveau mot de passe", "Mettre à jour le mot de passe", "Mes amis", "Rechercher un nom d'utilisateur...", "Résultats de recherche", "Amis actuels", "Vous n'avez pas encore d'amis. Cherchez-en haut !", "Ajouter", "Itinéraires partagés", "Personne n'a encore partagé d'itinéraire avec vous.", "Itinéraires d'amis", "vous a envoyé un itinéraire !", "Nombre total d'arrêts :", "Inspecter l'itinéraire",
    "📍 Itinéraire : %d arrêts sélectionnés", "📍 %d lieux trouvés sur la carte", "📌 Ordre de l'itinéraire (%d arrêts)", "DÉMARRER LA NAVIGATION","Lieux", "Hôtels","Guide IA", "Ex : Quel est le plat le plus célèbre ici ?", "Demander", "Fermer", "TripBuddy prépare la carte... 🤔","Voir sur la carte 🗺️","Nous tracerons un itinéraire sur mesure pour vous à partir des endroits les mieux notés de la ville. Combien d'arrêts voulez-vous ?", "Tracer l'itinéraire", "🧹 Effacer", "Itinéraire effacé ! 🧹","Réserver", "Avis", "Noter (%s) :", "Partagez votre expérience avec d'autres voyageurs...", "Envoyer", "Aucun avis pour le moment. Soyez le premier !", "Partager l'itinéraire avec un ami", "Envoyer" ,notificationsTitle = "Notifications",
)

// 🇪🇸 İSPANYOLCA TAM ÇEVİRİ PAKETİ
val esStrings = AppStrings(
    "Bienvenido a\nTripBuddy", "Iniciar sesión", "Registrarse", "Usuario", "Correo", "Contraseña", "¿Sin cuenta? ", "¿Ya eres miembro? ", "Llenar campos.", "¡Datos inválidos!", "¡El correo ya existe!", "Planear", "Mapa", "Perfil", "Crear ruta", "Empezar a planear", "¿A dónde vas?", "Fechas", "Inicio", "Fin", "Lugares para visitar", "Ver todo", "VER RUTA EN EL MAPA", "Hola", "Viajes planeados", "Ciudades visitadas", "Ajustes", "Idioma", "Cerrar sesión", "Ubicación y ruta", "Cargando mapa...", "Historia e Info", "Escuchar", "Detener", "Detalles", "Horarios de visita", "Street View", "Añadir a la ruta", "Días seleccionados", "Ruta Mágica", "¡Ruta más popular creada! ⭐",
    "Más info con IA", "Asistente TripBuddy", "Error al obtener la información.", "Ver ruta en el mapa", "¡Hola! Soy tu asistente inteligente TripBuddy. ¿Cómo puedo ayudarte?", "Ajustes de la app", "Ajustes", "Mis amigos", "Rutas compartidas",
    "Editar perfil", "Información personal", "Nombre", "Apellido", "Número de teléfono", "Guardar información", "Seguridad", "Contraseña actual", "Nueva contraseña", "Actualizar contraseña", "Mis amigos", "Buscar por nombre de usuario...", "Resultados de búsqueda", "Amigos actuales", "Aún no tienes amigos. ¡Busca arriba!", "Añadir", "Rutas compartidas", "Nadie ha compartido una ruta contigo todavía.", "Rutas de amigos", "¡te envió una ruta!", "Paradas totales:", "Inspeccionar ruta",
    "📍 Ruta: %d paradas seleccionadas", "📍 %d lugares encontrados en el mapa", "📌 Orden de la ruta (%d paradas)", "INICIAR NAVEGACIÓN","Lugares", "Hoteles","Guía IA", "Ej: ¿Cuál es la comida más famosa aquí?", "Preguntar", "Cerrar", "TripBuddy está preparando el mapa... 🤔","Ver en el mapa 🗺️","Trazaremos una ruta personalizada para ti desde los lugares mejor valorados de la ciudad. ¿Cuántas paradas quieres?", "Trazar ruta", "🧹 Borrar", "¡Ruta borrada! 🧹","Reservar", "Reseñas", "Calificar (%s):", "Comparte tu experiencia con otros viajeros...", "Enviar", "Aún no hay reseñas. ¡Sé el primero!", "Compartir ruta con amigo", "Enviar", notificationsTitle = "Notificaciones",
)// --- YER VERİ MODELİ (Firebase Uyumlu Hale Getirildi) ---
// Firebase'in veriyi çekebilmesi için varsayılan değerler (= "") eklendi.
data class PlaceData(
    val name: String = "",
    val location: String = "",
    val distance: String = "",
    val rating: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val detailedDescription: String = "",
    val detailedDescriptionEn: String = "",
    val detailedDescriptionDe: String = "",
    val detailedDescriptionFr: String = "",
    val detailedDescriptionEs: String = "",
    val descEn: String = "",
    val descDe: String = "",
    val descFr: String = "",
    val descEs: String = "",
    val hoursTr: String = "",
    val hoursEn: String = ""
) {
    // Firebase için boş yapıcı (no-argument constructor) gereklidir, yukarıdaki varsayılan değerler bunu sağlar.

    fun getDescription(lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.TR -> description; AppLanguage.EN -> descEn; AppLanguage.DE -> descDe; AppLanguage.FR -> descFr; AppLanguage.ES -> descEs
        }
    }
    fun getHours(lang: AppLanguage): String {
        return if(lang == AppLanguage.TR) hoursTr else hoursEn
    }
}

// --- VERİ YÖNETİMİ ---
data class User(val username: String, val email: String, val password: String)

// --- ANA UYGULAMA ---
@PreviewScreenSizes
@Composable
fun Bitirme_projesiApp() {
    val appLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
    val currentLangCode = if (!appLocales.isEmpty) appLocales.get(0)?.language else "tr"

    // Kayıtlı koda göre dilimizi bul, yoksa TR başla
    val initialLang = AppLanguage.entries.find { it.code == currentLangCode } ?: AppLanguage.TR

    // Artık sabit bir TR yok, sistem neyse o!
    var currentLanguage by remember { mutableStateOf(initialLang) }
    val strings = when (currentLanguage) {
        AppLanguage.TR -> trStrings; AppLanguage.EN -> enStrings; AppLanguage.DE -> deStrings; AppLanguage.FR -> frStrings; AppLanguage.ES -> esStrings
    }

    // --- AUTH (GİRİŞ) İŞLEMLERİ ---
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember {
        mutableStateOf<User?>(auth.currentUser?.let { User(it.displayName ?: "Kullanıcı", it.email ?: "", "") })
    }

    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status -> if (status != TextToSpeech.SUCCESS) { } }
        onDispose { tts?.stop(); tts?.shutdown() }
    }

    // --- VERİTABANI VE DURUM DEĞİŞKENLERİ ---
    val aiApiKey = "APİ" // Lütfen buraya kendi geçerli Gemini API anahtarını gir
    var places by remember { mutableStateOf<List<PlaceData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var city by remember { mutableStateOf("İstanbul") } // Başlangıç şehri
    var isDropdownExpanded by remember { mutableStateOf(false) } // Menü açık mı kapalı mı?

    // ====================================================================
    // ⚙️ AKILLI MOTOR (Firestore Alt Koleksiyon & Yapay Zeka)
    // ====================================================================
    LaunchedEffect(key1 = city) {
        isLoading = true
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // Şehrin altındaki "places" klasörüne bakıyoruz (Profesyonel mimari)
        val cityDocRef = db.collection("city_guides").document(city)
        val placesCollection = cityDocRef.collection("places")

        placesCollection.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // ✅ VERİ VAR: Firestore'dan saniyeler içinde çek
                try {
                    val cachedList = querySnapshot.toObjects(PlaceData::class.java)
                    places = cachedList
                    isLoading = false
                    android.util.Log.e("AKILLI_SİSTEM", "✅ $city mekanları Firestore'dan çekildi!")
                } catch (e: Exception) {
                    isLoading = false
                    android.util.Log.e("AKILLI_SİSTEM", "🚨 Dönüştürme Hatası: ${e.message}")
                }
            } else {
                // ⚠️ VERİ YOK: Yapay Zekayı çalıştır
                scope.launch {
                    android.util.Log.e("AKILLI_SİSTEM", "🚀 $city için Yapay Zeka çalışıyor...")
                    val aiResponse = getPlacesGuideFromGemini(city, aiApiKey)

                    try {
                        // 👇 1. DÜZELTME: Temizlik İstasyonu (JSON Çökmesini Engeller)
                        val cleanResponse = aiResponse.replace("```json", "").replace("```", "").trim()

                        val jsonArray = org.json.JSONArray(cleanResponse)
                        val aiList = mutableListOf<PlaceData>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)

                            // Eğer Gemini sayı yerine yanlışlıkla metin("41.0") yollarsa diye zırhlı okuma:
                            var pLat = obj.optDouble("lat", 0.0)
                            if (pLat == 0.0) pLat = obj.optString("lat", "0.0").toDoubleOrNull() ?: 0.0

                            var pLng = obj.optDouble("lng", 0.0)
                            if (pLng == 0.0) pLng = obj.optString("lng", "0.0").toDoubleOrNull() ?: 0.0

                            // 1. O anki dile göre Gemini'nin JSON'da ürettiği anahtar kelimeyi yakalayalım
                            val jsonKey = when (currentLanguage) {
                                AppLanguage.EN -> "descEn"
                                AppLanguage.DE -> "descDe"
                                AppLanguage.FR -> "descFr"
                                AppLanguage.ES -> "descEs"
                                else -> "description"
                            }

// 2. Gemini bazen inatçılık yapıp etiketi değiştiremezse diye güvenlik ağı kuruyoruz
                            val incomingText = obj.optString(jsonKey).let {
                                if (it.isNullOrBlank()) obj.optString("description", "Açıklama bulunamadı.") else it
                            }

// 3. Veriyi doğru dilin cebine koyarak nesnemizi oluşturalım
                            val place = PlaceData(
                                name = obj.optString("name", "Bilinmeyen Mekan"),
                                imageUrl = obj.optString("imageUrl", ""),
                                location = obj.optString("location", city),
                                rating = obj.optString("rating", "4.0"),
                                lat = obj.optDouble("lat", 0.0),
                                lng = obj.optDouble("lng", 0.0),

                                // Hangi dildeysek sadece o dilin cebini dolduruyoruz, diğerleri boş kalıyor
                                description = if (currentLanguage == AppLanguage.TR) incomingText else "",
                                descEn = if (currentLanguage == AppLanguage.EN) incomingText else "",
                                descDe = if (currentLanguage == AppLanguage.DE) incomingText else "",
                                descFr = if (currentLanguage == AppLanguage.FR) incomingText else "",
                                descEs = if (currentLanguage == AppLanguage.ES) incomingText else ""
                            )

// Son adım: Firebase'e kaydetme satırı (Burası nesneyi direkt Firebase'e gömer)
                            db.collection("city_guides").document(city).collection("places")
                                .document(place.name).set(place, com.google.firebase.firestore.SetOptions.merge())
                        }

                        places = aiList

                        // 👇 2. DÜZELTME: FİREBASE'E KAYDETME DÖNGÜSÜ EKLENDİ 👇
                        if (aiList.isNotEmpty()) {
                            aiList.forEach { mekan ->
                                placesCollection.document(mekan.name).set(mekan)
                                    .addOnSuccessListener {
                                        android.util.Log.d("AKILLI_SİSTEM", "💾 ${mekan.name} Firebase'e kaydedildi!")
                                    }
                                    .addOnFailureListener { e ->
                                        android.util.Log.e("AKILLI_SİSTEM", "🚨 Kayıt Hatası: ${e.message}")
                                    }
                            }
                        }

                    } catch (e: Exception) {
                        android.util.Log.e("AKILLI_SİSTEM", "🚨 JSON Hatası: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }
        }.addOnFailureListener { e ->
            isLoading = false
            android.util.Log.e("AKILLI_SİSTEM", "🚨 Firestore Hatası: ${e.message}")
        }
    }

    // Eğer kullanıcı giriş yapmadıysa sadece Giriş Ekranını göster
    if (currentUser == null) {
        SignInScreen(
            strings = strings,
            onSignInSuccess = { user -> currentUser = user },
            currentLanguage = currentLanguage,
            onLanguageChange = { currentLanguage = it }
        )
    }
// Kullanıcı giriş yaptıysa, Yapay Zekanın bulduğu mekanları göster!
    else {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. YÜKLENİYOR ANİMASYONU (Modernleştirildi)
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    // Şık turkuaz dönen çember eklendi
                    androidx.compose.material3.CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color(0xFF38A3A5)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TripBuddy Mekanları Keşfediyor...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            // 2. YÜKLEME BİTTİYSE KARTLARI ÇİZ
            else if (places.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp), // Kartlar arası nefes payını artırdık
                    modifier = Modifier.weight(1f) // Ekranı tam kaplaması için
                ) {
                    items(places) { place ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            // Dış kartın köşelerini yuvarlattık
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Havada süzülme hissi
                        ) {
                            // ANA KOLONDAKİ PADDING'İ SİLDİK Kİ RESİM KENARLARA YAPIŞSIN
                            Column() {

                                // 📸 WIKIPEDIA RESMİ (Artık tam ekran genişliğinde)
                                coil.compose.AsyncImage(
                                    model = getPlaceImage(place),
                                    contentDescription = place.name ?: "Mekan Görseli",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp), // Fotoğrafı biraz daha büyütüp dergi havası verdik
                                    contentScale = ContentScale.Crop
                                )

                                // YAZILAR İÇİN AYRI BİR KOLON AÇIP PADDING'İ BURAYA VERDİK
                                Column(modifier = Modifier.padding(16.dp)) {

                                    // 🏷️ MEKANIN ADI (Çökmeye karşı zırhlandı)
                                    Text(
                                        text = place.name ?: "Bilinmeyen Mekan",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 📜 MEKANIN TARİHÇESİ (Çökmeye karşı zırhlandı)
                                    Text(
                                        text = place.description ?: "Bu mekan için henüz bir açıklama bulunamadı.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
            // DİKKAT: Senin uygulamanın asıl gövdesi olan MainAppContent'i buraya koydum.
            // places (mekanlar) listesini ona başarıyla iletiyoruz.
            // İleride tasarımı değiştirmek istersen LazyColumn'u buradan kesip MainAppContent'in içine taşıyabilirsin.
    // Güvenli Zırh: Sadece currentUser boş DEĞİLSE içeri girer, çökmez!
    currentUser?.let { guvenliKullanici ->
        MainAppContent(
            currentUser = guvenliKullanici, // BOMBA (!!) İMHA EDİLDİ
            strings = strings,
            currentLanguage = currentLanguage,
            onLanguageChange = { currentLanguage = it },
            onLogout = {
                auth.signOut()
                currentUser = null
            },
            tts = tts,
            places = places,
            city = city,
            onCityChange = { secilenSehir -> city = secilenSehir },
            onClearPlaces = { places = emptyList() }
        )
    }
        }

// GİRİŞ VE KAYIT EKRANI (İLK EKRAN KEDİLERİN ORADA, FORM YUKARIDA - KUSURSUZ)
@Composable
fun SignInScreen(
    strings: AppStrings,
    onSignInSuccess: (User) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by rememberSaveable { mutableStateOf("START") }
    var isLoginMode by rememberSaveable { mutableStateOf(true) }

    var username by rememberSaveable { mutableStateOf("") }
    var ad by rememberSaveable { mutableStateOf("") }
    var soyad by rememberSaveable { mutableStateOf("") }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    // Şifremi unuttum penceresini açıp kapatacak şalter
    var showResetDialog by remember { mutableStateOf(false) }
    // Hata/Başarı mesajlarını ekranda göstermek için context
    val context = androidx.compose.ui.platform.LocalContext.current

    val scrollState = rememberScrollState()

    // Ekran boyunu hesaplıyoruz ki ilk butonları orantılı aşağı itelim
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    Box(modifier = modifier.fillMaxSize()) {

        // Yerel arka plan resmi
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.bitirme_projesi.R.drawable.tripbuddy),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Cool şeffaf karanlık filtre gradyanı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color(0xFF0F172A).copy(alpha = 0.80f)
                        )
                    )
                )
        )

        // Ana kaydırılabilir sütun
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // DİL SEÇİMİ BUTTONU
            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, end = 24.dp), contentAlignment = Alignment.TopEnd) {
                TextButton(onClick = { showLanguageMenu = true }) {
                    Text("${currentLanguage.flagEmoji} ${currentLanguage.code.uppercase()}", color = Color.White)
                }
                // NOT: Bu context değişkeninin bu kodların üstünde, Composable fonksiyonunun içinde
// sadece bir kere tanımlandığından kesinlikle emin ol:
// val context = LocalContext.current

                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    AppLanguage.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.flagEmoji} ${lang.displayName}") },
                            onClick = {
                                onLanguageChange(lang)
                                showLanguageMenu = false

                                // Sadece bu komut yeterli, sistem kendi kendine yeniden başlatacak
                                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                                    androidx.core.os.LocaleListCompat.forLanguageTags(lang.code)
                                )
                            }
                        )
                    }
                }
                }

            Spacer(modifier = Modifier.height(20.dp))

            // UYGULAMA LOGO VE BAŞLIĞI
            Icon(
                androidx.compose.material.icons.Icons.Default.Place,
                null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "TripBuddy",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            if (currentStep == "START") {
                // === 1. AŞAMA: İLK BUTONLAR (Kedilerin hizasına inen bölüm) ===
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 🚨 ÖZÜR DİLERİM BOŞLUĞU: 0.35f yapıp yukarı çekmiştim, şimdi 0.50f yaptım ki tam o kedilerin ve korkuluğun üstüne ağır ağır otursun!
                    Spacer(modifier = Modifier.height(screenHeight * 0.50f))

                    Button(
                        onClick = { isLoginMode = true; currentStep = "FORM" },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38A3A5).copy(alpha = 0.70f)
                        )
                    ) {
                        Text(strings.login, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isLoginMode = false; currentStep = "FORM" },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
                    ) {
                        Text(strings.signup, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // GELİŞTİRİCİ GİRİŞİ (Sadece ilk ekranda gözüküyor)
                    Row(modifier = Modifier.clickable { onSignInSuccess(User("Burçin", "dev", "")) }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(androidx.compose.material.icons.Icons.Default.Build, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                        Text(" Geliştirici Girişi", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            } else {
                // === 2. AŞAMA: FORM ALANI (Logo ile arasında sadece zarif bir boşluk var!) ===
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 🎯 Form ekranında logo ile form arasında minik, şık bir boşluk
                    Spacer(modifier = Modifier.height(30.dp))

                    TextButton(onClick = { currentStep = "START"; errorMessage = null }, modifier = Modifier.align(Alignment.Start)) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        Text(" Geri Dön", color = Color.White)
                    }

                    val tfColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38A3A5),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedContainerColor = Color.Black.copy(alpha = 0.35f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF38A3A5),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                    )

                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = ad,
                            onValueChange = { ad = it },
                            label = { Text("Ad") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = tfColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = soyad,
                            onValueChange = { soyad = it },
                            label = { Text("Soyad") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = tfColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text(strings.username) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = tfColors
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (isLoginMode) "E-posta veya Kullanıcı Adı" else strings.email) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = tfColors,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text(strings.password) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = tfColors,
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        trailingIcon = {
                            val img = if (passwordVisible) androidx.compose.material.icons.Icons.Filled.Visibility else androidx.compose.material.icons.Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(img, null, tint = Color.White.copy(alpha = 0.5f)) }
                        }
                    )

                    if (!isLoginMode) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword, onValueChange = { confirmPassword = it },
                            label = { Text("Şifreyi Doğrula") },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = tfColors,
                            visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            trailingIcon = {
                                val img = if (confirmPasswordVisible) androidx.compose.material.icons.Icons.Filled.Visibility else androidx.compose.material.icons.Icons.Filled.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(img, null, tint = Color.White.copy(alpha = 0.5f)) }
                            }
                        )
                    }
                    if (isLoginMode) {
                        TextButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.align(Alignment.End) // Butonu sağa yaslar
                        ) {
                            Text("Şifremi Unuttum?", color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color(0xFFFF6B6B), modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isLoading) return@Button
                            errorMessage = null
                            if (!isLoginMode && password != confirmPassword) { errorMessage = "Şifreler eşleşmiyor!"; return@Button }
                            isLoading = true

                            val girdiAlanininMetni = email.trim()

                            if (isLoginMode) {
                                if (girdiAlanininMetni.isEmpty() || password.isEmpty()) {
                                    isLoading = false
                                    errorMessage = "Lütfen tüm alanları doldurun!"
                                    return@Button
                                }

                                if (girdiAlanininMetni.contains("@")) {
                                    auth.signInWithEmailAndPassword(girdiAlanininMetni, password).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            onSignInSuccess(User(auth.currentUser?.displayName ?: "Gezgin", girdiAlanininMetni, ""))
                                        } else {
                                            isLoading = false
                                            errorMessage = task.exception?.localizedMessage
                                        }
                                    }
                                } else {
                                    db.collection("users")
                                        .whereEqualTo("username", girdiAlanininMetni)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                val gercekEmail = documents.documents.first().getString("email")
                                                if (gercekEmail != null) {
                                                    auth.signInWithEmailAndPassword(gercekEmail, password).addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            onSignInSuccess(User(auth.currentUser?.displayName ?: "Gezgin", gercekEmail, ""))
                                                        } else {
                                                            isLoading = false
                                                            errorMessage = task.exception?.localizedMessage
                                                        }
                                                    }
                                                } else {
                                                    isLoading = false
                                                    errorMessage = "Kullanıcıya ait e-posta bulunamadı."
                                                }
                                            } else {
                                                isLoading = false
                                                errorMessage = "Kullanıcı adı bulunamadı!"
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            errorMessage = e.localizedMessage
                                        }
                                }
                            } else {
                                if (ad.isEmpty() || soyad.isEmpty() || username.isEmpty() || girdiAlanininMetni.isEmpty()) {
                                    isLoading = false
                                    errorMessage = "Lütfen tüm alanları doldurun!"
                                    return@Button
                                }

                                auth.createUserWithEmailAndPassword(girdiAlanininMetni, password).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        val tamIsim = "${ad.trim()} ${soyad.trim()}"
                                        val profile = com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(tamIsim).build()

                                        user?.updateProfile(profile)?.addOnCompleteListener { guncellemeTask ->
                                            if (guncellemeTask.isSuccessful) {
                                                db.collection("users").document(user.uid).set(
                                                    hashMapOf(
                                                        "username" to username.trim(),
                                                        "ad" to ad.trim(),
                                                        "soyad" to soyad.trim(),
                                                        "email" to girdiAlanininMetni
                                                    )
                                                ).addOnSuccessListener { onSignInSuccess(User(tamIsim, girdiAlanininMetni, "")) }
                                            } else {
                                                isLoading = false
                                                errorMessage = guncellemeTask.exception?.localizedMessage
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                        errorMessage = task.exception?.localizedMessage
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text(if (isLoginMode) strings.login else strings.signup, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = Color(0xFF1E293B), // Koyu temaya uygun arka plan
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(alpha = 0.8f),
                title = { Text("Şifreyi Sıfırla") },
                text = {
                    Column {
                        Text("Şifrenizi sıfırlamak için kayıtlı e-posta adresinizi girin. Size bir bağlantı göndereceğiz.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = email, // Kullanıcı yukarıya mail yazdıysa otomatik buraya düşer
                            onValueChange = { email = it },
                            label = { Text("E-posta Adresi", color = Color.White.copy(alpha = 0.6f)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38A3A5),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (email.isNotBlank() && email.contains("@")) {
                                // Firebase'in kendi şifre sıfırlama fonksiyonu!
                                auth.sendPasswordResetEmail(email.trim())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            android.widget.Toast.makeText(context, "Sıfırlama e-postası gönderildi! Lütfen gelen kutunuzu kontrol edin.", android.widget.Toast.LENGTH_LONG).show()
                                            showResetDialog = false // Pencereyi kapat
                                        } else {
                                            android.widget.Toast.makeText(context, "Hata: ${task.exception?.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                android.widget.Toast.makeText(context, "Lütfen geçerli bir e-posta adresi girin.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
                    ) {
                        Text(strings.sendRouteBtn, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("İptal", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            )
        }
        // ========================================
    } // Bu senin ana Column'unun kapanışı (zaten kodunda var)

    // === ŞİFRE SIFIRLAMA POP-UP PENCERESİ ===


} // Bu da senin en dıştaki Box'ının kapanışı (zaten kodunda var)

// =======================================================
// YENİ HARMONİK İKİ RENKLİ PALET (ÜST VE BUTON AYNI RENK)
// =======================================================
val BaliCanliTurkuaz = Color(0xFF62A8A6)  // 🚨 Üst arka plan ve harita butonu için asil, mat turkuaz!
val BaliKartRengi = Color(0xFFFFFFFF)     // Kartların iç yüzeyi temiz saf beyaz
val BaliKoyuYazi = Color(0xFF2C3E44)      // Beyaz sayfadaki başlıklar ve metinler için koyu ton
val BaliAcikYazi = Color(0xFFF8FAFC)      // Turkuaz alanın üzerindeki yazılar ve ikonlar için beyaz ton

// --- ANA İÇERİK ---
@Composable
fun HotelDetailScreen(
    hotelName: String,
    imageUrl: String,
    rating: Double, // YILDIZ PUANI İÇİN YENİ EKLENDİ
    bookingUrl: String,
    lat: Double,
    lng: Double,
    strings: AppStrings,
    onBackClick: () -> Unit

){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()) // 👈 İŞTE EKSİK OLAN SİHİRLİ KOD BU!
    ) {
        // === 1. KISIM: OTEL FOTOĞRAFI VE GERİ BUTONU ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = hotelName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
        }

        // === 2. KISIM: İSİM, YILDIZ VE BUTONLAR ===
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // BAŞLIK VE SARI YILDIZ ROZETİ YAN YANA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Otel Adı
                Text(
                    text = hotelName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C3E44),
                    modifier = Modifier.weight(1f) // İsim uzunsa alt satıra geçsin, yıldızı itmesin
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Sarı Yıldız Rozeti (Attığın resimdeki gibi)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFE032), shape = RoundedCornerShape(8.dp)) // Sarı arka plan
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Puan",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rating.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // REZERVASYON BUTONU (Tam Kavisli - CircleShape)
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookingUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape, // <-- Hap şeklini veren kod bu
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
            ) {
                Text(strings.bookNowBtn, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HARİTALARDA GÖR BUTONU (Tam Kavisli - CircleShape)
            OutlinedButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($hotelName)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                        context.startActivity(browserIntent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape, // <-- Hap şeklini veren kod bu
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38A3A5)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38A3A5))
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(strings.aiSeeOnMapBtn, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp)) // Araya biraz boşluk koyalım

            // 🎯 İŞTE EKSİK OLAN KISIM BURASI! YORUM BÖLÜMÜNÜ BURADA ÇAĞIRIYORUZ:
            HotelReviewSection(hotelName = hotelName, strings = strings)
        }
    }
}
data class UserReview(
    val userName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelReviewSection(hotelName: String, strings: AppStrings) {
    var userRating by remember { mutableStateOf(0) }
    var userComment by remember { mutableStateOf("") }

    // Firebase servislerini başlatıyoruz
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Misafir"

    // Canlı olarak güncellenecek boş bir yorum listesi oluşturuyoruz
    val reviewsList = remember { mutableStateListOf<UserReview>() }

    // 🔄 1. ADIM: FIRESTORE'DAN YORUMLARI CANLI (REAL-TIME) OLARAK ÇEKME
    // Bu blok, otel sayfası her açıldığında veya veritabanına yeni bir yorum düştüğünde otomatik tetiklenir.
    LaunchedEffect(hotelName) {
        db.collection("hotel_reviews")
            .whereEqualTo("hotelName", hotelName) // Sadece bu otele ait yorumları getir
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener // Hata varsa sistemi kilitleme, çık

                if (snapshot != null) {
                    reviewsList.clear() // Eski listeyi temizle ki üst üste binmesin

                    // Gelen verileri tek tek bizim UserReview modeline çeviriyoruz
                    val fetchedReviews = snapshot.documents.mapNotNull { doc ->
                        val uName = doc.getString("userName") ?: "Misafir"
                        val rate = doc.getLong("rating")?.toInt() ?: 0
                        val comm = doc.getString("comment") ?: ""
                        val time = doc.getLong("timestamp") ?: 0L
                        UserReview(uName, rate, comm, time)
                    }.sortedByDescending { it.timestamp } // En yeni yorumu en üstte göstermek için sıralıyoruz

                    reviewsList.addAll(fetchedReviews) // Canlı listeye ekle, ekranda şak diye belirecek!
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = strings.reviewsTitle, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = strings.giveRatingText.replace("%s", currentUserName), fontWeight = FontWeight.Bold, color = Color.Gray)
                // 5 Yıldız Seçim Alanı
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { userRating = i }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$i Yıldız",
                                tint = if (i <= userRating) Color(0xFFFFE032) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = userComment,
                    onValueChange = { userComment = it },
                    placeholder = { Text(strings.reviewPlaceholder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38A3A5),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        // 💾 2. ADIM: GÖNDERME ANINDA FIRESTORE'A KAYDETME
                        if (userRating > 0 && userComment.isNotBlank()) {

                            // Veritabanına göndereceğimiz paketi (Haritayı) hazırlıyoruz
                            val reviewData = hashMapOf(
                                "hotelName" to hotelName,
                                "userName" to currentUserName,
                                "rating" to userRating,
                                "comment" to userComment,
                                "timestamp" to System.currentTimeMillis() // Sıralama için şimdiki zamanı milisaniye olarak kaydeder
                            )

                            // "hotel_reviews" adında bir tablo (collection) oluşturup içine fırlatıyoruz
                            db.collection("hotel_reviews")
                                .add(reviewData)
                                .addOnSuccessListener {
                                    // Yorum başarıyla buluta gitti! Kutuları temizle
                                    userComment = ""
                                    userRating = 0
                                }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
                ) {
                    Text(strings.submitReviewBtn, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === BULUTTAN GELEN CANLI YORUMLARIN LİSTELENMESİ ===
        if (reviewsList.isEmpty()) {
            Text(
                text = strings.noReviewsYet,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
                )

        } else {
            reviewsList.forEach { review ->
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF38A3A5), shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = review.userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (i <= review.rating) Color(0xFFFFE032) else Color.LightGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.comment, fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}
@Composable
fun RestaurantDetailScreen(
    restaurantName: String,
    imageUrl: String,
    rating: Double,
    lat: Double,
    lng: Double,
    strings: AppStrings,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()) // Kaydırma açık
    ) {
        // === 1. KISIM: MEKAN FOTOĞRAFI VE GERİ BUTONU ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
        }

        // === 2. KISIM: İSİM, YILDIZ ROZETİ VE HARİTA BUTONU ===
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = restaurantName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2C3E44),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Sarı Yıldız Rozeti
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFE032), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = rating.toString(), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // REZERVASYON YOK - SADECE HARİTALARDA GÖR BUTONU (Tam Kavisli)
            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($restaurantName)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
            ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(strings.aiSeeOnMapBtn, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // === 3. KISIM: MEKAN YORUM ALANI ===
        RestaurantReviewSection(restaurantName = restaurantName, strings = strings)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantReviewSection(restaurantName: String,strings: AppStrings) {
    var userRating by remember { mutableStateOf(0) }
    var userComment by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Misafir"

    val reviewsList = remember { mutableStateListOf<UserReview>() }

    // 🔄 BULUTTAN (restaurant_reviews) CANLI VERİ ÇEKME MOTORU
    LaunchedEffect(restaurantName) {
        db.collection("restaurant_reviews")
            .whereEqualTo("restaurantName", restaurantName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    reviewsList.clear()
                    val fetchedReviews = snapshot.documents.mapNotNull { doc ->
                        val uName = doc.getString("userName") ?: "Misafir"
                        val rate = doc.getLong("rating")?.toInt() ?: 0
                        val comm = doc.getString("comment") ?: ""
                        val time = doc.getLong("timestamp") ?: 0L
                        UserReview(uName, rate, comm, time)
                    }.sortedByDescending { it.timestamp }
                    reviewsList.addAll(fetchedReviews)
                }
            }
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = strings.reviewsTitle, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))
        Spacer(modifier = Modifier.height(16.dp))

        // YORUM YAZMA ALANI
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(strings.giveRatingText.replace("%s", currentUserName), fontWeight = FontWeight.Bold, color = Color.Gray)

                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { userRating = i }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i <= userRating) Color(0xFFFFE032) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = userComment,
                    onValueChange = { userComment = it },
                    placeholder = { Text(strings.reviewPlaceholder) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38A3A5),
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (userRating > 0 && userComment.isNotBlank()) {
                            val reviewData = hashMapOf(
                                "restaurantName" to restaurantName,
                                "userName" to currentUserName,
                                "rating" to userRating,
                                "comment" to userComment,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("restaurant_reviews").add(reviewData).addOnSuccessListener {
                                userComment = ""
                                userRating = 0
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A3A5))
                ) {
                    Text(strings.submitReviewBtn, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // YORUMLARIN LİSTELENMESİ
        if (reviewsList.isEmpty()) {
            Text(text = strings.noReviewsYet, color = Color.Gray, fontSize = 14.sp)
        } else {
            reviewsList.forEach { review ->
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(Color(0xFF38A3A5), shape = RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = review.userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = if (i <= review.rating) Color(0xFFFFE032) else Color.LightGray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.comment, fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                }
            }
        }

        // 🎯 ALT MENÜ ÇAKIŞMASINI ÖNLEYEN YASTIK (DOĞRU YERDE!)
        Spacer(modifier = Modifier.height(100.dp))
    }
}
@Composable
fun MainAppContent(currentUser: User, strings: AppStrings, currentLanguage: AppLanguage, onLanguageChange: (AppLanguage) -> Unit, onLogout: () -> Unit, tts: TextToSpeech?, places: List<PlaceData>, city: String, onCityChange: (String) -> Unit, onClearPlaces: () -> Unit) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<PlaceData?>(null) }
    val selectedRoute = remember { mutableStateListOf<PlaceData>() }
    var isFriendsOpen by remember { mutableStateOf(false) }
    var isSharedRoutesOpen by remember { mutableStateOf(false) }
    var isNotificationsOpen by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }
    // 🔥 FCM TOKEN ALMA VE VERİTABANINA YAZMA İŞLEMİ (GÜNCELLENDİ)
    val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    androidx.compose.runtime.LaunchedEffect(currentUid) {
        if (currentUid != null) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result

                    // 🔥 update yerine set ve merge kullanıyoruz ki kesinlikle yazsın
                    val tokenData = mapOf("fcmToken" to token)
                    db.collection("users").document(currentUid)
                        .set(tokenData, com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener {
                            println("TripBuddy: FCM Token Başarıyla Kaydedildi! -> $token")
                        }
                }
            }
        }
    }

    // 🔥 TELEFONUN GERİ TUŞUNU YÖNETEN KOD BURASI 🔥
    // Sadece kapatılacak bir sayfa açıksa veya Ana Sayfada değilsek bu tuşu devralıyoruz.
    val isBackHandlingEnabled = selectedPlace != null || isSettingsOpen || isFriendsOpen || isSharedRoutesOpen || currentDestination != AppDestinations.HOME

    BackHandler(enabled = isBackHandlingEnabled) {
        when {
            // 1. Eğer mekan detayı açıksa, onu kapat
            selectedPlace != null -> {
                selectedPlace = null
                tts?.stop() // Varsa sesli okumayı da durdur
            }
            // 2. Ayarlar sayfası açıksa, onu kapat
            isSettingsOpen -> isSettingsOpen = false
            // 3. Arkadaşlar sayfası açıksa, onu kapat
            isFriendsOpen -> isFriendsOpen = false
            // 4. Gelen Rotalar sayfası açıksa, onu kapat
            isSharedRoutesOpen -> isSharedRoutesOpen = false
            // 5. Alt menüden (Navbar) Harita veya Profile girilmişse, Ana Sayfaya dön
            currentDestination != AppDestinations.HOME -> currentDestination = AppDestinations.HOME
        }
    }

    if (selectedPlace != null) {
        PlaceDetailScreen(
            place = selectedPlace!!,
            cityName = city,
            strings = strings,
            currentLanguage = currentLanguage,
            tts = tts,
            onBack = {
                selectedPlace = null
                tts?.stop()
            }
        )
    } else if (isSettingsOpen) {
        SettingsScreen(
            strings = strings,
            currentLanguage = currentLanguage,
            onLanguageChange = onLanguageChange,
            onFriendsClick = { },
            onBack = { isSettingsOpen = false }
        )
    } else if (isFriendsOpen) {
        // 🎯 İŞTE BURAYA EKLENDİ
        FriendsScreen(strings = strings, onBack = { isFriendsOpen = false })
    } else if (isSharedRoutesOpen) {
        // 🎯 VE BURAYA EKLENDİ
        SharedRoutesScreen(
            strings = strings,
            onBack = { isSharedRoutesOpen = false },
            onRouteClick = { gelenRotaMekanlari ->
                selectedRoute.clear()
                selectedRoute.addAll(gelenRotaMekanlari)
                isSharedRoutesOpen = false
                currentDestination = AppDestinations.MAP
            }
        )
    } else {
        // Scaffold(...) kodların aynen devam ediyor...
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            // Dıştaki iskeletin arka plan rengini ana iskeletimize aldık
            containerColor = Color(0xFFE6F3F2),

            // ====================================================================
            // 🌊 YÜZEN CAM EFEKTLİ ALT MENÜ (GLASSMORPHISM BOTTOM BAR)
            // ====================================================================
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 🏠 1. ANA SAYFA BUTONU
                            IconButton(onClick = { currentDestination = AppDestinations.HOME }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Home,
                                    contentDescription = "Ana Sayfa",
                                    tint = if (currentDestination == AppDestinations.HOME) BaliCanliTurkuaz else Color.Gray.copy(alpha = 0.4f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // 🗺️ 2. HARİTA BUTONU
                            IconButton(onClick = { currentDestination = AppDestinations.MAP }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Map,
                                    contentDescription = "Harita",
                                    tint = if (currentDestination == AppDestinations.MAP) BaliCanliTurkuaz else Color.Gray.copy(alpha = 0.4f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // 👤 3. PROFİL BUTONU
                            IconButton(onClick = { currentDestination = AppDestinations.PROFILE }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                                    contentDescription = "Profil",
                                    tint = if (currentDestination == AppDestinations.PROFILE) BaliCanliTurkuaz else Color.Gray.copy(alpha = 0.4f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            // ====================================================================
            // 📱 SAYFA GEÇİŞLERİ (Senin kodun tamamen korundu)
            // ====================================================================
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(
                    strings = strings,
                    currentLanguage = currentLanguage,
                    places = places,
                    onPlaceClick = { selectedPlace = it },
                    selectedRoute = selectedRoute,
                    onToggleRoute = { place ->
                        if (selectedRoute.contains(place)) selectedRoute.remove(place) else selectedRoute.add(place)
                    },
                    onPlanClick = { currentDestination = AppDestinations.MAP },
                    modifier = Modifier.padding(innerPadding),
                    city = city,
                    onCityChange = onCityChange,
                    onClearPlaces = onClearPlaces,
                    onNotificationClick = { isNotificationsOpen = true }
                )
                AppDestinations.MAP -> MapScreen(
                    strings,
                    places,
                    selectedRoute,
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.PROFILE -> ProfileScreen(
                    user = currentUser,
                    strings = strings,
                    currentLanguage = currentLanguage, // 🚨 Dil bilgisini buraya ekledik
                    onLanguageChange = onLanguageChange, // 🚨 Dil değiştirme yetkisini buraya ekledik
                    onOpenSettings = { isSettingsOpen = true },
                    onFriendsClick = { isFriendsOpen = true },
                    onSharedRoutesClick = { isSharedRoutesOpen = true },
                    onLogout = onLogout
                )
            }
        }

    }
    // BİLDİRİMLER EKRANINI ÇAĞIRMA
    if (isNotificationsOpen) {
        NotificationsScreen(
            strings = strings,
            onBack = { isNotificationsOpen = false },
            onSeeRouteClick = { gelenMekanlar ->
                isNotificationsOpen = false // Bildirim ekranını kapat

                // Haritadaki mevcut seçili rotayı temizle ve yenilerini ekle
                if (selectedRoute is MutableList<*>) {
                    (selectedRoute as MutableList<PlaceData>).apply {
                        clear()
                        addAll(gelenMekanlar)
                    }
                }

                // Harita sayfasına git!
                currentDestination = AppDestinations.MAP
            }
        )
    }
}

enum class AppDestinations(val icon: androidx.compose.ui.graphics.vector.ImageVector) { HOME(androidx.compose.material.icons.Icons.Default.Home), MAP(androidx.compose.material.icons.Icons.Default.LocationOn), PROFILE(androidx.compose.material.icons.Icons.Default.AccountBox) }

// --- EKRAN 1: ANA SAYFA (PREMIUM TURKUAZ İKİ RENKLİ GRİD TASARIMI - İSTEDİĞİN ORİJİNAL HALİ) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    strings: AppStrings,
    currentLanguage: AppLanguage,
    places: List<PlaceData>,
    onPlaceClick: (PlaceData) -> Unit,
    selectedRoute: MutableList<PlaceData>,
    onToggleRoute: (PlaceData) -> Unit,
    onPlanClick: () -> Unit,
    modifier: Modifier = Modifier,
    city: String,
    onCityChange: (String) -> Unit,
    onClearPlaces: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showNearbyPlaces by remember { mutableStateOf(false) }
    var showNearbyHotels by remember { mutableStateOf(false) }
    var showHotelDetail by remember { mutableStateOf(false) }
    var showRestaurantDetail by remember { mutableStateOf(false) }
    var clickedRestaurant by remember { mutableStateOf<RestaurantData?>(null) } // Senin mekan veri sınıfının adı RestaurantData'ydı
    var clickedHotel by remember { mutableStateOf<RestaurantData?>(null) }
    val googleApiKey = "api"
    val geminiApiKey = "api"
    var nearbyRestaurants by remember { mutableStateOf<List<RestaurantData>>(emptyList()) }
    var isPlacesLoading by remember { mutableStateOf(true) } //apikeyyok
    var nearbyHotels by remember { mutableStateOf<List<RestaurantData>>(emptyList()) }
    var isHotelsLoading by remember { mutableStateOf(false) }

    // Sihirli rota diyalog penceresini açıp kapatmak için
    var showMagicRouteDialog by remember { mutableStateOf(false) }
// Kullanıcının seçeceği mekan sayısı (Varsayılan 4)
    var routePlaceCount by remember { mutableIntStateOf(4) }
    // Yapay zeka haritaya yeni mekan eklediğinde "Haritada Gör" butonunu çıkarmak için
    var aiFoundPlaces by remember { mutableStateOf(false) }

    // AI Asistan Penceresi Kontrolleri
    var showAiAssistantDialog by remember { mutableStateOf(false) }
    var aiChatText by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            try { fusedLocationClient.lastLocation.addOnSuccessListener { loc -> userLocation = if (loc != null) Pair(loc.latitude, loc.longitude) else Pair(41.0082, 28.9784) } } catch (e: SecurityException) { }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try { fusedLocationClient.lastLocation.addOnSuccessListener { loc -> userLocation = if (loc != null) Pair(loc.latitude, loc.longitude) else Pair(41.0082, 28.9784) } } catch (e: SecurityException) { }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            isPlacesLoading = true; isHotelsLoading = true
            nearbyRestaurants = fetchNearbyPlacesFromGoogle(googleApiKey, loc.first, loc.second, "cafe+OR+restaurant")
            nearbyHotels = fetchNearbyPlacesFromGoogle(googleApiKey, loc.first, loc.second, "hotel+OR+lodging+OR+pansiyon")
            isPlacesLoading = false; isHotelsLoading = false
        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Info,
                            contentDescription = "Bilgi",
                            tint = BaliCanliTurkuaz, // İkon rengini de turkuaz yaptık ki uyumlu olsun!
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = snackbarData.visuals.message,
                            color = androidx.compose.ui.graphics.Color.DarkGray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = BaliCanliTurkuaz // 🚨 Senin o asil Turkuaz rengini burada koruduk!
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ==========================================
                // ÜST BÖLGE: TURKUAZ ALAN (Yazılar parlayan beyaz)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val ilkIsim = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Burçin"
                            Text(
                                text = "${strings.hello} $ilkIsim 👋",
                                style = MaterialTheme.typography.titleLarge,
                                color = BaliAcikYazi,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = strings.whereTo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = BaliAcikYazi.copy(alpha = 0.8f)
                            )
                        }

                        var isDropdownExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                        val turkiyeIlleri = listOf(
                            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin",
                            "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur",
                            "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan",
                            "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul",
                            "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir",
                            "Kilis", "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla", "Muş",
                            "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas",
                            "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak"
                        )

                        // 🔥 YENİ EKLENEN KISIM: Şehir seçici ve Zil İkonunu yan yana tutan Row
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            // 1. ŞEHİR SEÇİCİ KAPSÜL
                            androidx.compose.material3.Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clickable { isDropdownExpanded = true }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                    Icon(androidx.compose.material.icons.Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(city, fontSize = 14.sp, fontWeight = FontWeight.Black, color = BaliAcikYazi)
                                    Spacer(Modifier.width(4.dp))
                                    Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowDown, null, tint = BaliAcikYazi.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                                }
                                DropdownMenu(
                                    expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.45f).heightIn(max = 250.dp).background(Color.White)
                                ) {
                                    turkiyeIlleri.forEach { secilenIl ->
                                        DropdownMenuItem(text = { Text(secilenIl, color = BaliKoyuYazi) }, onClick = { onCityChange(secilenIl); selectedRoute.clear(); isDropdownExpanded = false; onClearPlaces() })
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp)) // Araya hafif boşluk

                            // 2. BİLDİRİM ZİLİ
                            val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                            NotificationBell(
                                currentUid = currentUid,
                                strings = strings,
                                onBellClick = {
                                    onNotificationClick()
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // KATEGORİ BUTONLARI (Turkuaz zeminle uyumlu şık geçiş)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { showNearbyPlaces = !showNearbyPlaces },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showNearbyPlaces) Color.White else Color.White.copy(alpha = 0.2f),
                                contentColor = if (showNearbyPlaces) BaliCanliTurkuaz else Color.White
                            )
                        ) {
                            Icon(androidx.compose.material.icons.Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.placesBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showNearbyHotels = !showNearbyHotels },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showNearbyHotels) Color.White else Color.White.copy(alpha = 0.2f),
                                contentColor = if (showNearbyHotels) BaliCanliTurkuaz else Color.White
                            )
                        ) {
                            Icon(androidx.compose.material.icons.Icons.Default.Home, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.hotelsBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            
                        }
                    }
                }

                // 🚨 ÇÖZÜM: MEKANLAR VE OTELLER İÇİN AYRI AYRI SATIRLAR YAPIYORUZ!
                // İkisine birden basılırsa alt alta iki satır halinde sorunsuz açılacak.

                // --- MEKANLAR LİSTESİ ---
                AnimatedVisibility(visible = showNearbyPlaces) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = if (showNearbyHotels) 8.dp else 16.dp)) {
                        if (isPlacesLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                        else if (nearbyRestaurants.isNotEmpty()) {
                            val contextLocal = androidx.compose.ui.platform.LocalContext.current
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(nearbyRestaurants.size) { index ->
                                    val restaurant = nearbyRestaurants[index]
                                    val safeRestaurantName = restaurant.name ?: "Bilinmeyen Mekan"

                                    // 🔥 YENİ: Bu restoran zaten rotaya eklenmiş mi kontrol et
                                    val isSelected = selectedRoute.any { it.name == safeRestaurantName }

                                    Card(
                                        modifier = Modifier
                                            .width(280.dp) // Buton sığsın diye genişliği 260'tan 280 yaptık
                                            .height(80.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Tıklayınca yine haritayı açma özelliğini koruyoruz (Görsel ve metne tıklayınca)
                                            Row(
                                                modifier = Modifier.weight(1f).clickable {
                                                    clickedRestaurant = restaurant // ya da döngüdeki değişken adın neyse (örn: hotel yerine restaurant)
                                                    showRestaurantDetail = true
                                                },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = restaurant.imageUrl,
                                                    contentDescription = restaurant.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                                                )

                                                Spacer(Modifier.width(12.dp))

                                                Column(verticalArrangement = Arrangement.Center) {
                                                    Text(text = restaurant.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(text = "⭐ ${restaurant.rating}", fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }

                                            // ➕ YENİ: ROTAYA EKLE / ÇIKAR BUTONU
                                            IconButton(
                                                onClick = {
                                                    val newPlace = PlaceData(
                                                        name = safeRestaurantName,
                                                        lat = restaurant.lat, // 🔥 ARTIK SAHTE KONUM DEĞİL, KENDİ KONUMU
                                                        lng = restaurant.lng, // 🔥 ARTIK SAHTE KONUM DEĞİL, KENDİ KONUMU
                                                        rating = restaurant.rating ?: "0.0",
                                                        location = "Yakın Restoran/Kafe",
                                                        imageUrl = restaurant.imageUrl ?: "",
                                                        description = "Canlı konumunuza yakın popüler mekan."
                                                    )

                                                    if (isSelected) {
                                                        selectedRoute.removeAll { it.name == safeRestaurantName } // 🔥 Düzeldi
                                                    } else {
                                                        selectedRoute.add(newPlace)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                                                    contentDescription = "Rotaya Ekle",
                                                    tint = if (isSelected) TripBuddyBlue else Color.Gray,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- OTELLER LİSTESİ ---
                AnimatedVisibility(visible = showNearbyHotels) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 16.dp)) {
                        if (isHotelsLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                        else if (nearbyHotels.isNotEmpty()) {
                            val contextLocal = androidx.compose.ui.platform.LocalContext.current
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(nearbyHotels.size) { index ->
                                    val hotel = nearbyHotels[index]

                                    // 🔥 YENİ: Bu otel zaten rotaya eklenmiş mi kontrol et
                                    val safeHotelName = hotel.name ?: "Bilinmeyen Otel"
                                    val isSelected = selectedRoute.any { it.name == safeHotelName }

                                    Card(
                                        modifier = Modifier
                                            .width(280.dp)
                                            .height(80.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f).clickable {
                                                    clickedHotel = hotel
                                                    showHotelDetail = true
                                                },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // ... AsyncImage ve Text kodların aynı kalacak ...
                                                AsyncImage(
                                                    model = hotel.imageUrl,
                                                    contentDescription = hotel.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                                                )

                                                Spacer(Modifier.width(12.dp))

                                                Column(verticalArrangement = Arrangement.Center) {
                                                    Text(text = hotel.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(text = "⭐ ${hotel.rating}", fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }

                                            // ➕ YENİ: OTELİ ROTAYA EKLE / ÇIKAR BUTONU
                                            IconButton(
                                                onClick = {
                                                    val newHotelPlace = PlaceData(
                                                        name = safeHotelName,
                                                        lat = hotel.lat, // 🔥 Kendi gerçek enlemi
                                                        lng = hotel.lng, // 🔥 Kendi gerçek boylamı
                                                        rating = hotel.rating ?: "0.0",
                                                        location = "Yakın Konaklama",
                                                        imageUrl = hotel.imageUrl ?: "",
                                                        description = "Canlı konumunuza yakın otel/pansiyon."
                                                    )

                                                    if (isSelected) {
                                                        selectedRoute.removeAll { it.name == safeHotelName } // 🔥 Düzeldi
                                                    } else {
                                                        selectedRoute.add(newHotelPlace)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                                                    contentDescription = "Rotaya Ekle",
                                                    tint = if (isSelected) TripBuddyBlue else Color.Gray,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // BEMBEYAZ MODERN ESNEK SHEET GÖVDESİ (ESKİ SADELİKTE)
                // ==========================================
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 140.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = strings.popularPlaces,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BaliKoyuYazi,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1, // Başlık sığmazsa alt satıra geçmek yerine sonuna "..." koysun
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (selectedRoute.isNotEmpty()) {
                                    // YENİ:
                                    Text(strings.clearBtn, color = Color(0xFFD32F2F), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { selectedRoute.clear(); scope.launch { snackbarHostState.showSnackbar(strings.routeClearedMsg) } })
                                }
                            }

                            // Sihirli Rota Butonu
                            Button(
                                onClick = { showMagicRouteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BaliCanliTurkuaz.copy(alpha = 0.1f), contentColor = BaliCanliTurkuaz),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = strings.magicRoute,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1 // 2. KALKAN: Yazıyı kesinlikle tek satırda tut!
                                )
                            }
                        }

                        // İKİLİ MODERN GRİDLER
                        if (places.isEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = BaliCanliTurkuaz)
                                Spacer(Modifier.height(12.dp))
                                Text(text = "Mekanlar hazırlanıyor...", color = Color.Gray, fontSize = 13.sp)
                            }
                        } else {
                            places.chunked(2).forEach { satirdakiMekanlar ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    satirdakiMekanlar.forEach { place ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            PlaceCard(
                                                place = place,
                                                strings = strings,
                                                isSelected = selectedRoute.contains(place),
                                                onToggleSelect = { onToggleRoute(place) },
                                                onDetailClick = { onPlaceClick(place) }
                                            )
                                        }
                                    }
                                    if (satirdakiMekanlar.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            } // Ana dikey Column bitti
        }
    }
    // 🌟 KULLANICIYA KAÇ MEKAN İSTEDİĞİNİ SORAN PENCERE
    if (showMagicRouteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showMagicRouteDialog = false },
            containerColor = Color.White,
            title = {
                Text("${strings.magicRoute} 🌟", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(strings.magicRouteDesc, color = Color.Gray, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))

                    // + ve - Butonlarıyla Sayı Seçimi
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (routePlaceCount > 1) routePlaceCount-- },
                            modifier = Modifier.background(BaliCanliTurkuaz.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(androidx.compose.material.icons.Icons.Default.Remove, null, tint = BaliCanliTurkuaz)
                        }

                        Text(
                            text = "$routePlaceCount",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = BaliCanliTurkuaz
                        )

                        IconButton(
                            onClick = {
                                // Olan mekan sayısından fazlasını seçemesin
                                if (routePlaceCount < places.size) routePlaceCount++
                            },
                            modifier = Modifier.background(BaliCanliTurkuaz.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(androidx.compose.material.icons.Icons.Default.Add, null, tint = BaliCanliTurkuaz)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMagicRouteDialog = false
                        selectedRoute.clear()

                        // 🔥 ARTIK 4 DEĞİL, KULLANICININ SEÇTİĞİ SAYI (routePlaceCount) KADAR ALIYOR
                        val smartPlaces = places.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }.take(routePlaceCount).toMutableList()

                        if (userLocation != null && smartPlaces.isNotEmpty()) {
                            val optimizedRoute = mutableListOf<PlaceData>()
                            var currentLocation = userLocation!!

                            while (smartPlaces.isNotEmpty()) {
                                val closestPlace = smartPlaces.minByOrNull { place ->
                                    val results = FloatArray(1)
                                    android.location.Location.distanceBetween(
                                        currentLocation.first, currentLocation.second,
                                        place.lat, place.lng,
                                        results
                                    )
                                    results[0]
                                }

                                if (closestPlace != null) {
                                    optimizedRoute.add(closestPlace)
                                    smartPlaces.remove(closestPlace)
                                    currentLocation = Pair(closestPlace.lat, closestPlace.lng)
                                }
                            }
                            selectedRoute.addAll(optimizedRoute)
                        } else {
                            selectedRoute.addAll(smartPlaces)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                kotlinx.coroutines.withTimeoutOrNull(1500L) {
                                    snackbarHostState.showSnackbar("En Popüler $routePlaceCount Yer Seçildi! ⭐")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BaliCanliTurkuaz)
                ) {
                    Text(strings.magicRouteDrawBtn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 1. Standart Kapat Butonu
                    TextButton(onClick = { showAiAssistantDialog = false }) {
                        Text(strings.aiDialogCancel, color = Color.Gray)
                    }

                    // 2. Yapay Zeka Mekan Bulduğunda Çıkacak Olan Dinamik Buton
                    if (aiFoundPlaces) {
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showAiAssistantDialog = false // Pencereyi kapat
                                onPlanClick() // Harita sayfasına geçişi tetikle
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                        ) {
                            Text(text = "Haritada Gör 🗺️", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        )

    }
// ==========================================
    // YENİ: YÜZEN BUTON GRUBU (Yan Yana Tasarım)
    // ==========================================
    Box(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp) // 👈 1. Padding
                .padding(bottom = 100.dp),   // 👈 2. Padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        )  {

            // 🗺️ 1. ROTAYI HARİTADA GÖR BUTONU (Solda, dinamik genişler)
            if (selectedRoute.isNotEmpty()) {
                androidx.compose.material3.Surface(
                    onClick = onPlanClick,
                    modifier = Modifier
                        .weight(1f) // Kalan tüm sol boşluğu esnek bir şekilde doldurur!
                        .height(55.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BaliCanliTurkuaz,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = strings.rotayiHaritadaGor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BaliKoyuYazi,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                // İki buton arasına biraz nefes alma boşluğu ekleyelim
                Spacer(modifier = Modifier.width(16.dp))
            }

            // 🤖 2. YAPAY ZEKA REHBER BUTONU (Sağda, her zaman sabit)
            FloatingActionButton(
                onClick = { showAiAssistantDialog = true },
                containerColor = BaliCanliTurkuaz,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome, contentDescription = "AI Rehber", modifier = Modifier.size(32.dp))
            }
        }
    }
    // ====================================================================
// 🤖 YAPAY ZEKA TUR REHBERİ PENCERESİ (TAMAMI YENİLENDİ)
// ====================================================================
    if (showAiAssistantDialog) {
        // 🔥 BUTONUN GÖRÜNMESİNİ GARANTİLEMEK İÇİN STATE'İ DİYALOG İÇİNE ALDIK
        var aiFoundPlacesInsideDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAiAssistantDialog = false },
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(androidx.compose.material.icons.Icons.Default.AutoAwesome, null, tint = BaliCanliTurkuaz, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.aiDialogTitle, fontWeight = FontWeight.Bold, color = BaliCanliTurkuaz)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Yapay Zekanın Cevap Kutusu
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 250.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = aiResponse.ifEmpty { strings.aiRehberInit },
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Kullanıcının Soru Yazdığı Yer
                    OutlinedTextField(
                        value = aiChatText,
                        onValueChange = { aiChatText = it },
                        placeholder = { Text(strings.aiDialogHint, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BaliCanliTurkuaz,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (aiChatText.isNotBlank()) {
                            val sorulanSoru = aiChatText
                            aiResponse = strings.aiDialogLoading
                            aiChatText = ""
                            aiFoundPlacesInsideDialog = false // Yeni soruda butonu sıfırla

                            scope.launch {
                                try {
                                    val model = com.google.ai.client.generativeai.GenerativeModel(
                                        modelName = "gemini-2.5-flash",
                                        apiKey = geminiApiKey
                                    )

                                    val targetLanguage = currentLanguage.displayName

                                    val prompt = """
Sen 'TripBuddy' seyahat uygulamasının akıllı tur rehberisin. 
Kullanıcının sorusu: "$sorulanSoru"

GÖREVLERİN:
1) Kullanıcıya KESİNLİKLE $targetLanguage dilinde samimi, kısa ve net bir cevap ver. Cevabında asla yıldız (*), kalınlaştırma veya markdown işaretleri kullanma.
2) Eğer spesifik turistik yerler veya restoranlardan bahsediyorsan, bunları cevabın en sonuna şu formatta ekle. HER MEKANI SADECE 1 KERE YAZ VE BAŞINA/SONUNA BAŞKA BİR ŞEY EKLEME:
<places>[{"name":"Mekan Adı","lat":41.45,"lng":31.79,"type":"Yakın Restoran/Kafe"}]</places>
""".trimIndent()

                                    val response = model.generateContent(prompt)
                                    val rawText = response.text ?: ""

                                    val placesRegex = "<places>(.*?)</places>".toRegex(RegexOption.DOT_MATCHES_ALL)
                                    val matchResult = placesRegex.find(rawText)

                                    if (matchResult != null) {
                                        var jsonString = matchResult.groups[1]?.value?.trim() ?: ""
                                        aiResponse = rawText.replace(placesRegex, "").trim()

                                        // 🛡️ GEMINI MARKDOWN TEMİZLEME KALKANI (Eğer ```json eklerse temizler)
                                        if (jsonString.startsWith("```json")) {
                                            jsonString = jsonString.removePrefix("```json")
                                        }
                                        if (jsonString.endsWith("```")) {
                                            jsonString = jsonString.removeSuffix("```")
                                        }
                                        jsonString = jsonString.trim()

                                        if (jsonString.isNotBlank() && jsonString != "[]") {
                                            try {
                                                val jsonArray = org.json.JSONArray(jsonString)
                                                val newDynamicPlaces = mutableListOf<PlaceData>()

                                                for (i in 0 until jsonArray.length()) {
                                                    val obj = jsonArray.getJSONObject(i)
                                                    val mekanAdi = obj.getString("name")

                                                    val zatenRotadaVarMi = selectedRoute.any { it.name.equals(mekanAdi, ignoreCase = true) }
                                                    val suAnkiListeyeEklendiMi = newDynamicPlaces.any { it.name.equals(mekanAdi, ignoreCase = true) }

                                                    if (!zatenRotadaVarMi && !suAnkiListeyeEklendiMi) {
                                                        newDynamicPlaces.add(
                                                            PlaceData(
                                                                name = mekanAdi,
                                                                lat = obj.getDouble("lat"),
                                                                lng = obj.getDouble("lng"),
                                                                rating = "4.5",
                                                                location = obj.optString("type", "Genel"),
                                                                imageUrl = "",
                                                                description = "Yapay Zeka Önerisi"
                                                            )
                                                        )
                                                    }
                                                }

                                                if (newDynamicPlaces.isNotEmpty()) {
                                                    (selectedRoute as? MutableList<PlaceData>)?.addAll(newDynamicPlaces)
                                                    // 🔥 BAŞARILI EKLEMEDE DİYALOG STATE'İNİ TRUE YAPIYORUZ
                                                    aiFoundPlacesInsideDialog = true
                                                }

                                            } catch (jsonEx: Exception) {
                                                android.util.Log.e("AI_JSON", "Hata: ${jsonEx.message}")
                                            }
                                        }
                                    } else {
                                        aiResponse = rawText
                                    }

                                } catch (e: Exception) {
                                    aiResponse = "Bağlantı hatası: ${e.localizedMessage}"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BaliCanliTurkuaz)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.aiDialogAsk, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { showAiAssistantDialog = false }) {
                        Text(strings.aiDialogCancel, color = Color.Gray)
                    }

                    // 🔥 ARTIK DİYALOG İÇİNDEKİ DEĞİŞKENİ KONTROL EDİYORUZ (KESİN ÇALIŞIR)
                    if (aiFoundPlacesInsideDialog) {
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showAiAssistantDialog = false
                                onPlanClick() // Haritaya ışınlanma tetikleyicisi
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                        ) {
                            Text(text = "Haritada Gör 🗺️", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        )
    }
// === OTEL DETAY SAYFASI GÖSTERİMİ ===
    // Otel Detayı Çağrısı
    if (showHotelDetail && clickedHotel != null) {
        val aramaKelimesi = android.net.Uri.encode("${clickedHotel!!.name} Hotel")
        HotelDetailScreen(
            hotelName = clickedHotel!!.name ?: "Bilinmeyen Otel",
            imageUrl = clickedHotel!!.imageUrl ?: "",
            rating = clickedHotel!!.rating?.toDoubleOrNull() ?: 0.0,
            bookingUrl = "https://www.tripadvisor.com.tr/Search?q=$aramaKelimesi",
            lat = clickedHotel!!.lat,
            lng = clickedHotel!!.lng,
            strings = strings, // 👈 BUNU EKLEDİK
            onBackClick = { showHotelDetail = false }
        )
    }

    // Restoran Detayı Çağrısı
    if (showRestaurantDetail && clickedRestaurant != null) {
        RestaurantDetailScreen(
            restaurantName = clickedRestaurant!!.name ?: "Bilinmeyen Mekan",
            imageUrl = clickedRestaurant!!.imageUrl ?: "",
            rating = clickedRestaurant!!.rating?.toDoubleOrNull() ?: 0.0,
            lat = clickedRestaurant!!.lat,
            lng = clickedRestaurant!!.lng,
            strings = strings, // 👈 BUNU EKLEDİK
            onBackClick = { showRestaurantDetail = false }
        )
    }
// ===============================================
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBell(
    currentUid: String?,
    strings: AppStrings, // 🔥 ÇOKLU DİL DESTEĞİ BURADAN GELİYOR
    onBellClick: () -> Unit
) {
    val tripBuddyBlue = Color(0xFF38A3A5)
    var unreadCount by remember { mutableIntStateOf(0) }

    // Bildirimleri Firestore'dan canlı dinliyoruz
    LaunchedEffect(currentUid) {
        if (currentUid != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("users").document(currentUid)
                .collection("notifications")
                .whereEqualTo("isRead", false)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        unreadCount = snapshot.documents.size
                    }
                }
        }
    }

    IconButton(onClick = onBellClick) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text(text = unreadCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                // 🔥 Ekran okuyucu (TalkBack) için dili strings dosyasından alıyoruz
                contentDescription = strings.notificationsTitle ?: "Bildirimler",
                tint = Color.White
            )
        }
    }
}

// =======================================================
// SEÇKİN KARE MEKAN KARTI
// =======================================================
@Composable
fun PlaceCard(
    place: PlaceData,
    strings: AppStrings,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onDetailClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var imageModel by remember(place.name) { mutableStateOf<Any?>(android.R.drawable.ic_menu_gallery) }

    LaunchedEffect(place.name) {
        val placesClient = com.google.android.libraries.places.api.Places.createClient(context)
        val token = com.google.android.libraries.places.api.model.AutocompleteSessionToken.newInstance()
        val searchRequest = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery("${place.name}, ${place.location}")
            .build()

        placesClient.findAutocompletePredictions(searchRequest)
            .addOnSuccessListener { searchResponse ->
                val prediction = searchResponse.autocompletePredictions.firstOrNull()

                if (prediction != null) {
                    val fields = listOf(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS)
                    val detailRequest = com.google.android.libraries.places.api.net.FetchPlaceRequest.newInstance(prediction.placeId, fields)

                    // 2. ADIM: Mekan Detaylarını Alma
                    placesClient.fetchPlace(detailRequest).addOnSuccessListener { detailResponse ->
                        val photoMetadata = detailResponse.place.photoMetadatas?.firstOrNull()

                        if (photoMetadata != null) {
                            val photoRequest = com.google.android.libraries.places.api.net.FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(500)
                                .setMaxHeight(400)
                                .build()

                            // 3. ADIM: Fotoğrafı Çekme
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photoResponse ->
                                imageModel = photoResponse.bitmap
                            }.addOnFailureListener { exception ->
                                // 🔥 EKSİK OLAN KISIM 1: Fotoğraf çekilirken tıkanırsa
                                android.util.Log.e("PLACES_API_HATA", "fetchPhoto Hatası: ${exception.message}")
                                imageModel = "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=400"
                            }
                        } else {
                            // 🔥 EKSİK OLAN KISIM 2: Mekanın Google'da hiç fotoğrafı yoksa
                            imageModel = "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=400"
                        }
                    }.addOnFailureListener { exception ->
                        // 🔥 EKSİK OLAN KISIM 3: Mekan detayları alınırken tıkanırsa
                        android.util.Log.e("PLACES_API_HATA", "fetchPlace Hatası: ${exception.message}")
                        imageModel = "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=400"
                    }
                } else {
                    // 🔥 EKSİK OLAN KISIM 4: Arama sonucunda mekan bulunamazsa
                    imageModel = "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=400"
                }
            }
            .addOnFailureListener { exception ->
                // Bu zaten sende vardı, ana arama tıkanırsa
                android.util.Log.e("PLACES_API_HATA", "Arama yapılamadı: ${exception.message}")
                imageModel = "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=400"
            }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BaliCanliTurkuaz) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 2.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(115.dp)) {
                coil.compose.AsyncImage(
                    model = coil.request.ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)),
                    // 🔥 SİGORTA: Eğer internet yoksa veya resim gelmezse,
                    // sistem otomatik olarak senin belirlediğin varsayılan resmi gösterir.
                    fallback = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                )

                IconButton(
                    onClick = { onToggleSelect() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(if (isSelected) BaliCanliTurkuaz else Color.White.copy(alpha = 0.9f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSelected) androidx.compose.material.icons.Icons.Default.Check else androidx.compose.material.icons.Icons.Default.Add,
                        contentDescription = "Seç",
                        tint = if (isSelected) Color.White else BaliCanliTurkuaz,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(text = place.name, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = BaliKoyuYazi, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(androidx.compose.material.icons.Icons.Default.Place, null, tint = BaliCanliTurkuaz, modifier = Modifier.size(11.dp))
                Spacer(Modifier.width(3.dp))
                Text(text = place.location, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onDetailClick,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) BaliCanliTurkuaz else Color(0xFFE6F3F2),
                    contentColor = if (isSelected) Color.White else BaliCanliTurkuaz
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(strings.detailBtn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ====================================================================
// 🗺️ GOOGLE MAPS'E SIRALI ROTA GÖNDEREN YARDIMCI FONKSİYON
// ====================================================================
fun openGoogleMapsSequentialRoute(context: Context, route: List<PlaceData>) {
    if (route.isEmpty()) return

    // Eğer tek mekan varsa direkt oraya git
    if (route.size == 1) {
        val dest = "${route.first().lat},${route.first().lng}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$dest")).apply {
            setPackage("com.google.android.apps.maps")
        }
        try { context.startActivity(mapIntent) } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$dest"))) }
        return
    }

    // Başlangıç ve Bitiş noktaları
    val origin = "${route.first().lat},${route.first().lng}"
    val destination = "${route.last().lat},${route.last().lng}"

    // Aradaki duraklar (Waypoints) - İlk ve son eleman hariç
    val intermediateWaypoints = route.drop(1).dropLast(1)
    val waypointsParam = if (intermediateWaypoints.isNotEmpty()) {
        "&waypoints=" + intermediateWaypoints.joinToString("|") { "${it.lat},${it.lng}" }
    } else ""

    // Resmi Google Maps Directions API URL'si
    val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination$waypointsParam&travelmode=driving"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        setPackage("com.google.android.apps.maps")
    }
    try { context.startActivity(intent) } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(strings: AppStrings, places: List<PlaceData>, selectedRoute: List<PlaceData>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Koordinatları geçerli (0.0 olmayan) olanları filtrele
    val validPlaces = places.filter { it.lat != 0.0 && it.lng != 0.0 }
    val validSelectedRoute = selectedRoute.filter { it.lat != 0.0 && it.lng != 0.0 }

    val focusPlace = validSelectedRoute.firstOrNull() ?: validPlaces.firstOrNull()

    val cameraPositionState = rememberCameraPositionState {
        val initLoc = focusPlace?.let { LatLng(it.lat, it.lng) } ?: LatLng(39.920770, 32.854110)
        position = CameraPosition.fromLatLngZoom(initLoc, if(focusPlace != null) 12f else 5f)
    }

    LaunchedEffect(validPlaces, validSelectedRoute) {
        val target = validSelectedRoute.firstOrNull() ?: validPlaces.firstOrNull()
        target?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), 13f)
            )
        }
    }

    val placesToShow = if (validSelectedRoute.isNotEmpty()) validSelectedRoute else validPlaces
    val infoText = if (validSelectedRoute.isNotEmpty()) {
        strings.mapRouteSelected.replace("%d", validSelectedRoute.size.toString())
    } else {
        strings.mapPlacesFound.replace("%d", validPlaces.size.toString())
    }
    // ====================================================================
    // 🔥 PAYLAŞIM VE FİREBASE SİSTEMİ (Senin Kodun - Dokunulmadı)
    // ====================================================================
    val db = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    var showShareDialog by remember { mutableStateOf(false) }
    var myFriendsList by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var isSharing by remember { mutableStateOf(false) }

    LaunchedEffect(showShareDialog) {
        if (showShareDialog && currentUid != null) {
            db.collection("users").document(currentUid).get().addOnSuccessListener { doc ->
                val friendUids = doc.get("friends") as? List<String> ?: emptyList()
                if (friendUids.isNotEmpty()) {
                    db.collection("users").whereIn(com.google.firebase.firestore.FieldPath.documentId(), friendUids)
                        .get().addOnSuccessListener { friendsDocs ->
                            myFriendsList = friendsDocs.map {
                                mapOf("uid" to it.id, "username" to (it.getString("username") ?: "İsimsiz"))
                            }
                        }
                }
            }
        }
    }

    // ====================================================================
    // UI (ARAYÜZ) BAŞLIYOR
    // ====================================================================
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // 1. DİNAMİK MARKERLAR
            placesToShow.forEach { tekilMekan ->
                val konum = com.google.android.gms.maps.model.LatLng(tekilMekan.lat, tekilMekan.lng)

                val markerRengi = when (tekilMekan.location) {
                    "Yakın Restoran/Kafe" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
                    "Yakın Konaklama" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
                    else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                }

                Marker(
                    state = com.google.maps.android.compose.rememberMarkerState(position = konum),
                    title = tekilMekan.name,
                    snippet = "⭐ ${tekilMekan.rating}",
                    icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(markerRengi)
                )
            }

            // 2. ROTA ÇİZGİSİ
            if (validSelectedRoute.isNotEmpty()) {
                val rotaKoordinatlari = validSelectedRoute.map {
                    com.google.android.gms.maps.model.LatLng(it.lat, it.lng)
                }

                Polyline(
                    points = rotaKoordinatlari,
                    color = Color(0xFF1976D2), // TripBuddyBlue
                    width = 8f
                )
            }
        }

        // 3. BİLGİ ETİKETİ (Haritanın tepesinde duran yazı)
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                text = infoText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        // --- 🤝 ROTAYI PAYLAŞ BUTONU (Sağ Ortada Yüzen Buton) ---
        if (validSelectedRoute.isNotEmpty()) {
            FloatingActionButton(
                onClick = { showShareDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp),
                containerColor = Color(0xFFE91E63), // TripBuddyPink
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Share, contentDescription = "Paylaş")
            }
        }

        // ====================================================================
        // 🔥 YENİ: DİNAMİK ROTA SIRALAMA PANELİ VE NAVİGASYON BAŞLATMA
        // ====================================================================
        if (validSelectedRoute.isNotEmpty()) {
            var isPanelExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .animateContentSize(), // Yumuşak açılma animasyonu
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Üst Başlık ve Aç/Kapat
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { isPanelExpanded = !isPanelExpanded }.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.mapRouteOrder.replace("%d", validSelectedRoute.size.toString()),
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Icon(
                            imageVector = if (isPanelExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Detay",
                            tint = Color.Gray
                        )
                    }

                    // Sıralama Listesi (Sadece Panel Açıksa Görünür)
                    if (isPanelExpanded) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(validSelectedRoute.size) { index ->
                                val durak = validSelectedRoute[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${index + 1}. ${durak.name}",
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                        color = Color.Black
                                    )

                                    // Yukarı - Aşağı Taşıma Okları
                                    Row {
                                        IconButton(
                                            onClick = {
                                                // Asıl kaynak olan selectedRoute listesini güncelliyoruz
                                                val gercekIndex = selectedRoute.indexOf(durak)
                                                if (gercekIndex > 0 && selectedRoute is MutableList<*>) {
                                                    val mutableList = selectedRoute.toMutableList()
                                                    java.util.Collections.swap(mutableList, gercekIndex, gercekIndex - 1)
                                                    (selectedRoute as? MutableList<PlaceData>)?.apply {
                                                        clear()
                                                        addAll(mutableList)
                                                    }
                                                }
                                            },
                                            enabled = index > 0,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, null, tint = if (index > 0) Color(0xFF1976D2) else Color.LightGray, modifier = Modifier.size(16.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                val gercekIndex = selectedRoute.indexOf(durak)
                                                if (gercekIndex < selectedRoute.size - 1 && selectedRoute is MutableList<*>) {
                                                    val mutableList = selectedRoute.toMutableList()
                                                    java.util.Collections.swap(mutableList, gercekIndex, gercekIndex + 1)
                                                    (selectedRoute as? MutableList<PlaceData>)?.apply {
                                                        clear()
                                                        addAll(mutableList)
                                                    }
                                                }
                                            },
                                            enabled = index < validSelectedRoute.size - 1,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, null, tint = if (index < validSelectedRoute.size - 1) Color(0xFF1976D2) else Color.LightGray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    // 🚀 NAVİGASYONU BAŞLAT BUTONU (Bizim yazdığımız özel fonksiyonu tetikler)
                    Button(
                        onClick = { openGoogleMapsSequentialRoute(context, validSelectedRoute) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.startNavBtn, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // ====================================================================
    // 📩 PAYLAŞIM PENCERESİ (DİALOG) (Senin Kodun - Dokunulmadı)
    // ====================================================================
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text(strings.shareRouteTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)) },
            text = {
                if (myFriendsList.isEmpty()) {
                    Text("Arkadaş listesi yükleniyor veya henüz arkadaşın yok.", color = Color.Gray)
                } else {
                    androidx.compose.foundation.lazy.LazyColumn {
                        items(myFriendsList.size) { index ->
                            val friend = myFriendsList[index]
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(friend["username"] ?: "", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    enabled = !isSharing,
                                    onClick = {
                                        isSharing = true
                                        if (currentUid != null) {
                                            val routeData = hashMapOf(
                                                "creatorUid" to currentUid,
                                                "sharedWith" to listOf(friend["uid"] ?: ""),
                                                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                                                "places" to validSelectedRoute.map {
                                                    hashMapOf("name" to it.name, "lat" to it.lat, "lng" to it.lng, "rating" to it.rating)
                                                }
                                            )
                                            db.collection("shared_routes").add(routeData)
                                                .addOnSuccessListener {

                                                    // ==================================================
                                                    // 🔥 YENİ: BİLDİRİMİ (NOTIFICATION) VERİTABANINA YAZMA
                                                    // ==================================================
                                                    val senderName = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName ?: "Bir arkadaşın"
                                                    val friendUid = friend["uid"] ?: ""

                                                    if (friendUid.isNotEmpty()) {
                                                        val notificationData = hashMapOf(
                                                            "senderName" to senderName,
                                                            "routeName" to "${validSelectedRoute.size} duraklık rota",
                                                            "timestamp" to System.currentTimeMillis(),
                                                            "isRead" to false,
                                                            // 🔥 YENİ EKLENEN SATIR: Haritanın çizilmesi için mekanları bildirime de ekliyoruz
                                                            "places" to validSelectedRoute.map {
                                                                hashMapOf("name" to it.name, "lat" to it.lat, "lng" to it.lng, "rating" to it.rating)
                                                            }
                                                        )

                                                        db.collection("users").document(friendUid)
                                                            .collection("notifications")
                                                            .add(notificationData)
                                                    }
                                                    // ==================================================

                                                    isSharing = false
                                                    showShareDialog = false
                                                    Toast.makeText(context, "Rota ${friend["username"]} ile paylaşıldı!", Toast.LENGTH_LONG).show()
                                                }
                                                .addOnFailureListener {
                                                    isSharing = false
                                                    Toast.makeText(context, "Gönderilemedi!", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                                ) {
                                    Text( text = if (isSharing) "..." else strings.sendRouteBtn)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareDialog = false }) { Text(strings.aiDialogCancel, color = Color.Gray) }
            }
        )
    }
}

// --- DETAY VE DİĞER EKRANLAR (Aynı) ---
@Composable
fun PlaceDetailScreen(
    place: PlaceData,
    cityName: String,
    strings: AppStrings,
    currentLanguage: AppLanguage,
    tts: TextToSpeech?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    // --- 📸 GOOGLE PLACES API: OTOMATİK GERÇEK RESİM ÇEKİCİ ---
    var detailImageModel by remember(place.name) { mutableStateOf<Any?>(android.R.drawable.ic_menu_gallery) }

    LaunchedEffect(place.name) {
        val placesClient = com.google.android.libraries.places.api.Places.createClient(context)
        val token = com.google.android.libraries.places.api.model.AutocompleteSessionToken.newInstance()
        val searchRequest = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery("${place.name}, ${cityName}") // Şehir adını da ekledik ki tam isabet olsun
            .build()

        placesClient.findAutocompletePredictions(searchRequest)
            .addOnSuccessListener { searchResponse ->
                val prediction = searchResponse.autocompletePredictions.firstOrNull()
                if (prediction != null) {
                    val fields = listOf(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS)
                    val detailRequest = com.google.android.libraries.places.api.net.FetchPlaceRequest.newInstance(prediction.placeId, fields)

                    placesClient.fetchPlace(detailRequest).addOnSuccessListener { detailResponse ->
                        val photoMetadata = detailResponse.place.photoMetadatas?.firstOrNull()
                        if (photoMetadata != null) {
                            val photoRequest = com.google.android.libraries.places.api.net.FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(1000) // Detay ekranı için büyük ve net resim
                                .setMaxHeight(800)
                                .build()
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photoResponse ->
                                detailImageModel = photoResponse.bitmap
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Hata olursa varsayılan ikon kalır
            }
    }

    // --- 🤖 YAPAY ZEKA DETAYLARI İÇİN DEĞİŞKENLER ---
    val scope = rememberCoroutineScope()
    var detailedText by remember { mutableStateOf<String?>(null) }
    var isDetailLoading by remember { mutableStateOf(false) }

    // 🚨 DİKKAT: API anahtarını en üste aldık ki alttaki çeviri fonksiyonu onu görebilsin!
    val geminiApiKey = "api" // BURAYA KENDİ ANAHTARINI GİRMEYİ UNUTMA

    // ==========================================================
    // 🌐 2. ADIM: OTONOM ÇEVİRİ TETİKLEYİCİSİ VE YENİ DEĞİŞKENLER
    // ==========================================================

    // Ekranda görünecek dinamik açıklama
    var displayDescription by remember { mutableStateOf("Açıklama yükleniyor...") }
    var isTranslating by remember { mutableStateOf(false) }

    // Sayfa ilk açıldığında veya dil değiştiğinde burası otomatik tetiklenir!
    // ✅ YERİNE BU YENİ AKILLI BLOĞU YAPIŞTIR:
    LaunchedEffect(currentLanguage, place.name) {
        // 1. Önce yerel nesneye (place) bakalım
        val currentDesc = place.getDescription(currentLanguage)

        // 2. Eğer yerelde yoksa ve dil Türkçe değilse, Firebase'den taze veriyi kontrol et
        if (currentDesc.isNullOrBlank() && currentLanguage != AppLanguage.TR) {
            isTranslating = true
            displayDescription = "🌐 ${currentLanguage.displayName}..."

            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val dbKey = "description_${currentLanguage.name.lowercase()}"

            try {
                // Firebase'den bu mekana ait daha önce kaydedilmiş çeviri var mı diye zorla çekiyoruz
                val snapshot = db.collection("city_guides").document(cityName)
                    .collection("places").document(place.name).get().await()

                val firebaseDesc = snapshot.getString(dbKey)

                if (!firebaseDesc.isNullOrBlank()) {
                    // 🔥 Firebase'de zaten varmış! Gemini'ye gitmeden doğrudan hafızadan alıyoruz
                    displayDescription = firebaseDesc
                    isTranslating = false
                } else {
                    // 3. Firebase'de de yoksa, o zaman ilk defa Gemini'ye çevirtip kaydediyoruz
                    val translated = translateAndSaveDescription(
                        city = cityName,
                        placeName = place.name,
                        originalText = place.description,
                        languageName = currentLanguage.displayName,
                        languageCode = currentLanguage.name.lowercase(),
                        apiKey = geminiApiKey
                    )
                    displayDescription = translated
                    isTranslating = false
                }
            } catch (e: Exception) {
                // Bir hata olursa ekran boş kalmasın diye Türkçe metne geri dön (Fallback)
                displayDescription = place.description
                isTranslating = false
            }
        } else {
            // Zaten Türkçe veya yerel veri mevcutsa direkt göster
            displayDescription = if (currentDesc.isNullOrBlank()) place.description else currentDesc
        }
    }

    // Saatler için de geçici bir güvenlik önlemi (İngilizce ise beyaz ekran kalmasın diye)
    val rawHours = place.getHours(currentLanguage)
    val hours = if (rawHours.isNullOrBlank()) "09:00 - 18:00" else rawHours
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TripBuddyOffWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isPlaying) { tts?.stop(); isPlaying = false } else {
                        val result = tts?.setLanguage(currentLanguage.locale)
                        if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                            tts?.speak(displayDescription, TextToSpeech.QUEUE_FLUSH, null, place.name)
                            isPlaying = true
                        }
                    }
                },
                containerColor = if (isPlaying) Color.Red else TripBuddyBlue,
                contentColor = Color.White
            ) { Icon(if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {

            // --- 1. ÜST KISIM (GÜNCELLENEN BÜYÜK RESİM) ---
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(detailImageModel) // 🔥 İŞTE SADECE BURASI DEĞİŞTİ!
                        .setHeader("User-Agent", "Mozilla/5.0")
                        .crossfade(true)
                        .build(),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop, // Resmi ekrana yay
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    // Yüklenemezse varsayılan gri ikon
                    error = rememberAsyncImagePainter(model = android.R.drawable.ic_menu_report_image)
                )

                // Geri butonu
                IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.7f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
                }
            }

            // --- 2. DETAYLAR (BAŞLIK, PUAN, SAATLER, AÇIKLAMA) ---
            Column(modifier = Modifier.padding(24.dp)) {

                // Başlık ve Puan
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(place.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.background(Color(0xFFFFEB3B), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("★ ${place.rating}", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Konum
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(place.location, color = Color.Gray)
                }

                Spacer(Modifier.height(24.dp))

                // Street View Butonu
                Button(
                    onClick = {
                        // Direkt 360 derece sokak görünümü komutu
                        val gmmIntentUri = Uri.parse("google.streetview:cbll=${place.lat},${place.lng}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // Google Haritalar yüklü değilse hata vermesin
                            android.widget.Toast.makeText(context, "Google Haritalar yüklü değil", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Place, null)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.streetView)
                }

                // Ziyaret Saatleri Kartı
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(strings.visitHours, fontWeight = FontWeight.Bold, color = TripBuddyBlue)
                        Spacer(Modifier.height(4.dp))
                        Text(hours, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Tarihçe / Açıklama Metni
                // Tarihçe / Açıklama Metni
                Text(text = strings.historyTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(height = 8.dp))

// 🔥 ARTIK DİNAMİK DEĞİŞKENİMİZİ KULLANIYORUZ (HATA GİDECEK)
                Text(
                    text = displayDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = if (isTranslating) Color.Gray else Color.DarkGray,
                    fontStyle = if (isTranslating) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                )

                Spacer(Modifier.height(24.dp))

                // ==========================================================
                // 🤖 3. KISIM: YAPAY ZEKA İLE DAHA FAZLA BİLGİ (YENİ EKLENDİ)
                // ==========================================================

                // Eğer bilgi henüz çekilmediyse ve yüklenmiyorsa butonu göster
                if (detailedText == null && !isDetailLoading) {
                    Button(
                        onClick = {
                            scope.launch {
                                isDetailLoading = true

                                try {
                                    // 🔥 FONKSİYONA DİL BİLGİLERİNİ GÖNDERİYORUZ
                                    val aiDetailedResponse = getDetailedInfoFromGemini(
                                        city = cityName,
                                        placeName = place.name,
                                        apiKey = geminiApiKey,
                                        currentLanguage = currentLanguage // 👈 Sadece bunu gönderiyoruz!
                                    )

                                    detailedText = aiDetailedResponse

                                } catch (e: Exception) {
                                    android.util.Log.e("AKILLI_SİSTEM", "Hata: ${e.message}")
                                    detailedText = strings.errorLoading // 🔥 Çeviri eklendi
                                } finally {
                                    isDetailLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TripBuddyBlue.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TripBuddyBlue)
                        Spacer(Modifier.width(8.dp))
                        // 🔥 Çeviri eklendi
                        Text(strings.aiMoreInfo, color = TripBuddyBlue, fontWeight = FontWeight.Bold)
                    }
                }

                if (isDetailLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TripBuddyBlue)
                    }
                }

                // Butona basılınca dönen yükleme tekerleği
                if (isDetailLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TripBuddyBlue)
                    }
                }

                // Yapay zekadan cevap geldiğinde şık bir kart içinde göster
                detailedText?.let { text ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TripBuddyBlue.copy(alpha = 0.05f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TripBuddyBlue.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TripBuddyBlue)
                                Spacer(Modifier.width(8.dp))
                                Text("TripBuddy Asistan", fontWeight = FontWeight.Bold, color = TripBuddyBlue, fontSize = 16.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(text, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Seslendirme butonu(FAB) yazıyı kapatmasın diye boşluk bırakıyoruz
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    strings: AppStrings,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onFriendsClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
    val currentUser = auth.currentUser

    val TripBuddyBlue = Color(0xFF38A3A5)

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var originalImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isSavingInfo by remember { mutableStateOf(false) }
    var isSavingPass by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    firstName = doc.getString("firstName") ?: ""
                    lastName = doc.getString("lastName") ?: ""
                    phoneNumber = doc.getString("phone") ?: ""
                }
            }
        }
    }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val extras = result.data?.extras
            val bitmap = extras?.getParcelable<android.graphics.Bitmap>("data")

            if (bitmap != null) {
                val file = java.io.File(context.cacheDir, "cropped_profile.jpg")
                val outStream = java.io.FileOutputStream(file)
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.flush()
                outStream.close()
                profileImageUri = android.net.Uri.fromFile(file)
            } else {
                result.data?.data?.let { croppedUri -> profileImageUri = croppedUri }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            originalImageUri = uri
            val cropIntent = android.content.Intent("com.android.camera.action.CROP").apply {
                setDataAndType(uri, "image/*")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putExtra("crop", "true")
                putExtra("aspectX", 1)
                putExtra("aspectY", 1)
                putExtra("outputX", 500)
                putExtra("outputY", 500)
                putExtra("return-data", true)
            }

            try {
                val resInfoList = context.packageManager.queryIntentActivities(cropIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(packageName, uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                cropLauncher.launch(cropIntent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Kırpma açılamadı.", android.widget.Toast.LENGTH_SHORT).show()
                profileImageUri = uri
            }
        }
    }

    Scaffold(containerColor = Color(0xFFF8FAFC)) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(44.dp).shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)).background(color = Color.White, shape = RoundedCornerShape(14.dp))
                ) {
                    Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TripBuddyBlue)
                }
                Spacer(Modifier.width(16.dp))
                Text(text = strings.profileEditTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF2C3E44))
            }

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                Box(modifier = Modifier.size(130.dp).background(Color.White, CircleShape).border(4.dp, Color.White, CircleShape).shadow(8.dp, CircleShape), contentAlignment = Alignment.Center) {
                    if (profileImageUri != null) {
                        coil.compose.AsyncImage(model = profileImageUri, contentDescription = "Profil Resmi", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.LightGray))
                    } else {
                        Icon(androidx.compose.material.icons.Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color.LightGray)
                    }
                }
                IconButton(
                    onClick = { photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.size(42.dp).offset(x = (-4).dp, y = (-4).dp).background(TripBuddyBlue, CircleShape).border(3.dp, Color(0xFFF8FAFC), CircleShape)
                ) { Icon(androidx.compose.material.icons.Icons.Default.Edit, contentDescription = "Düzenle", tint = Color.White, modifier = Modifier.size(20.dp)) }
            }

            androidx.compose.animation.AnimatedVisibility(visible = profileImageUri != null) {
                Button(
                    onClick = { /* Resim kaydetme işlemi */ },
                    modifier = Modifier.height(40.dp).padding(bottom = 24.dp), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = TripBuddyBlue.copy(alpha = 0.15f), contentColor = TripBuddyBlue), elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isUploadingImage) "..." else "Fotoğrafı Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = strings.personalInfo, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44), fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, placeholder = { Text(strings.nameHint) }, leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Person, null, tint = TripBuddyBlue) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = TripBuddyBlue, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC)))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, placeholder = { Text(strings.surnameHint) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = TripBuddyBlue, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC)))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, placeholder = { Text(strings.phoneHint) }, leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Phone, null, tint = TripBuddyBlue) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = TripBuddyBlue, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC)))
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { isSavingInfo = true }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TripBuddyBlue), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                            Text(if (isSavingInfo) "..." else strings.saveInfoBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(text = strings.securityTitle, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44), fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, placeholder = { Text(strings.currentPassHint) }, leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Lock, null, tint = TripBuddyBlue) }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = TripBuddyBlue, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC)))
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, placeholder = { Text(strings.newPassHint) }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = TripBuddyBlue, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color(0xFFF8FAFC), unfocusedContainerColor = Color(0xFFF8FAFC)))
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { isSavingPass = true }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TripBuddyBlue), elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)) {
                            Text(if (isSavingPass) "..." else strings.updatePassBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

// --- RESİM YARDIMCISI ---
// Eğer Firebase'de resim yoksa, isme göre otomatik resim seçer
// --- RESİM YARDIMCISI (GÜNCELLENDİ) ---
fun getPlaceImage(place: PlaceData): String {
    // 1. İsimleri küçük harfe çevirip temizliyoruz
    val cleanName = place.name.trim().lowercase(java.util.Locale("tr", "TR"))

    // ====================================================================
    // A. ÖZEL LİSTE (En bilindik mekanlar için nokta atışı görseller)
    // ====================================================================
    val exactMatches = mapOf(
        "ayasofya" to "https://images.unsplash.com/photo-1545459720-aac8509eb02c?w=600&q=80",
        "galata" to "https://images.unsplash.com/photo-1541426062085-70dc1981ee8b?w=600&q=80",
        "kız kulesi" to "https://images.unsplash.com/photo-1527838832700-5059252407fa?w=600&q=80",
        "topkapı" to "https://images.unsplash.com/photo-1578121360211-137a1c5dca97?w=600&q=80",
        "sultanahmet" to "https://images.unsplash.com/photo-1533903345306-15d1c30952de?w=600&q=80",
        "dolmabahçe" to "https://images.unsplash.com/photo-1622083547843-ea7eece9c4e0?w=600&q=80",
        "kapalı" to "https://images.unsplash.com/photo-1587391993427-18331d279e8d?w=600&q=80",
        "yerebatan" to "https://images.unsplash.com/photo-1638865814545-2f94a81baec8?w=600&q=80",
        "boğaz" to "https://images.unsplash.com/photo-1559828551-38cbba5863c0?w=600&q=80",
        "taksim" to "https://images.unsplash.com/photo-1610014760814-1e5b15b3b64c?w=600&q=80",
        "gölyazı" to "https://images.unsplash.com/photo-1616428751421-2a14ae5b706f?w=600&q=80",
        "koza" to "https://images.unsplash.com/photo-1596395819057-e37f55a8516b?w=600&q=80",
        "muradiye" to "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?w=600&q=80"
    )

    // Önce kelime içinde arama yapıyoruz
    for ((key, url) in exactMatches) {
        if (cleanName.contains(key)) {
            return url
        }
    }

    // ====================================================================
    // B. GÖRSEL HAVUZU (Listede olmayan onlarca mekan için)
    // ====================================================================
    // Bunlar doğrudan fotoğraf dosyasıdır (Asla çökmez, kapanmaz)
    val fallbackImages = listOf(
        "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?w=600&q=80", // Cami/Tarihi
        "https://images.unsplash.com/photo-1549880181-5813ce828821?w=600&q=80", // Eski Taş Sokak
        "https://images.unsplash.com/photo-1513622470522-26c31154c1ba?w=600&q=80", // Mimari Yapı
        "https://images.unsplash.com/photo-1507608616759-54f48f0af0ee?w=600&q=80", // Dar Sokak
        "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=600&q=80", // Doğa/Göl
        "https://images.unsplash.com/photo-1534008897995-27a23e859048?w=600&q=80", // Otantik Çarşı
        "https://images.unsplash.com/photo-1527838832700-5059252407fa?w=600&q=80", // Deniz/Manzara
        "https://images.unsplash.com/photo-1587391993427-18331d279e8d?w=600&q=80", // Baharat/Kültür
        "https://images.unsplash.com/photo-1622083547843-ea7eece9c4e0?w=600&q=80"  // Saray/Lüks
    )

    // Mekan ismindeki harfleri matematiğe döküyoruz
    var charSum = 0
    for (char in cleanName) {
        charSum += char.code
    }

    // Çıkan sayıyı yedek resim sayısına bölüp kalanı alıyoruz.
    // Böylece mekan ismine özel, 100% sabit bir resim seçmiş oluyoruz.
    val index = charSum % fallbackImages.size
    return fallbackImages[index]
}

@Composable
fun ProfileScreen(
    user: User,
    strings: AppStrings,
    currentLanguage: AppLanguage, // 🚨 İŞTE BURASI! O kırmızılıkları anında yok edecek!
    onLanguageChange: (AppLanguage) -> Unit, // 🚨 VE BURASI!
    onOpenSettings: () -> Unit,
    onFriendsClick: () -> Unit,
    onSharedRoutesClick: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F3F2)) // Ferah, açık yeşil/mavi arka plan
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ==========================================
        // 1. ÜST BÖLGE (KAVİSLİ TURKUAZ ARKA PLAN)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = TripBuddyBlue,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 50.dp, bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profil Fotoğrafı (Beyaz Çerçeveli)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                        .padding(4.dp) // Çerçeve kalınlığı
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.White)
                }

                Spacer(Modifier.height(16.dp))

                val ad = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName ?: "Gezgin"
                Text(text = "${strings.hello}, $ad! 👋", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(text = user.email, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        // ==========================================
        // 2. UYGULAMA AYARLARI KARTI
        // ==========================================
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(strings.appSettings, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    ProfileMenuButton(title = strings.settingsMenu, icon = androidx.compose.material.icons.Icons.Default.Settings, onClick = onOpenSettings)
                    androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))

                    ProfileMenuButton(title = strings.myFriends, icon = androidx.compose.material.icons.Icons.Default.Person, onClick = onFriendsClick)
                    androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))

                    ProfileMenuButton(title = strings.sharedRoutes, icon = androidx.compose.material.icons.Icons.Default.Email, onClick = onSharedRoutesClick)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ==========================================
            // 3. DİL SEÇİMİ KARTI (YATAY KAYDIRILABİLİR PREMIUM TASARIM)
            // ==========================================
            Text(strings.language, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                // 🚨 SİHİRLİ DOKUNUŞ: horizontalScroll ile sağa sola kaydırılabilir yatay liste!

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()) // 🚨 Açık adresi sildik, sade haline döndürdük
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = lang == currentLanguage
                        Button(
                            onClick = {
                                // 1. Senin fonksiyonun (Değişkeni günceller)
                                onLanguageChange(lang)

                                // 2. 🎯 İŞTE EKSİK OLAN SİHİRLİ EMİR! (Sistemi zorla değiştir ve yenile)
                                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                                    androidx.core.os.LocaleListCompat.forLanguageTags(lang.code)
                                )
                            },
                            modifier = Modifier
                                .width(105.dp) // 🚨 Butonları sıkıştırmak yerine 105.dp sabit genişlik verdik
                                .height(65.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TripBuddyBlue else Color(0xFFF1F5F9),
                                contentColor = if (isSelected) Color.White else Color.DarkGray
                            ),
                            elevation = ButtonDefaults.buttonElevation(if (isSelected) 4.dp else 0.dp),
                            contentPadding = PaddingValues(0.dp) // İç boşlukları sıfırladık
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(text = lang.flagEmoji, fontSize = 20.sp)
                                Spacer(Modifier.height(2.dp))
                                // Artık "T", "E" yerine dillerin tam adı yazacak
                                Text(text = lang.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ==========================================
            // 4. ÇIKIŞ YAP BUTONU
            // ==========================================
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Parlak, şık bir kırmızı
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text(strings.logout, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(100.dp)) // Alt menü (Navbar) üstüne binmesin diye ferah bir boşluk
        }
    }
}

// --- KART İÇİ İNCE MENÜ BUTONU TASARIMI ---
@Composable
fun ProfileMenuButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C3E44)
        )
        Icon(imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

// Eğer RestaurantData zaten projende tanımlıysa bu 6 satırı silebilirsin. Yoksa kalsın.
data class RestaurantData(
    val name: String,
    val rating: String,
    val searchQuery: String,
    val placeName: String,
    val imageUrl: String? = null,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
data class AiPlaceData(
    val name: String,
    val description: String,
    val imageUrl: String = "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=400&q=80" // Şimdilik şık bir gezi çantası resmi
)

// Google API'den mekanları çeken motor
suspend fun fetchNearbyPlacesFromGoogle(
    apiKey: String,
    lat: Double,
    lng: Double,
    keyword: String
): List<RestaurantData> {
    return withContext(Dispatchers.IO) {

        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=2500&keyword=$keyword&key=$apiKey"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        val fetchedList = mutableListOf<RestaurantData>()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            if (responseData != null) {
                val jsonObject = JSONObject(responseData)
                val results = jsonObject.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val place = results.getJSONObject(i)
                    val name = place.optString("name", "Bilinmeyen Mekan")
                    val rating = place.optDouble("rating", 0.0).toString()

                    // 🔥 2. ADIM BURADA BAŞLIYOR: Gerçek koordinatları okuyoruz
                    val geometry = place.optJSONObject("geometry")
                    val location = geometry?.optJSONObject("location")
                    val gercekLat = location?.optDouble("lat", 0.0) ?: 0.0
                    val gercekLng = location?.optDouble("lng", 0.0) ?: 0.0

                    // 👇 FOTOĞRAF KODUNU ALALIM 👇
                    val photos = place.optJSONArray("photos")
                    val photoUrl = if (photos != null && photos.length() > 0) {
                        val photoReference = photos.getJSONObject(0).getString("photo_reference")
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$photoReference&key=$apiKey"
                    } else {
                        "https://via.placeholder.com/150"
                    }

                    // 🔥 Listeye eklerken artık o mekanın kendi koordinatlarını da veriyoruz
                    fetchedList.add(RestaurantData(
                        name = name,
                        rating = rating,
                        searchQuery = name,
                        placeName = "Canlı Konum",
                        imageUrl = photoUrl,
                        lat = gercekLat, // 👈 Gerçek Enlem Eklendi
                        lng = gercekLng  // 👈 Gerçek Boylam Eklendi
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("GOOGLE_API", "Hata: ${e.message}")
        }

        fetchedList
            .filter { it.imageUrl != "https://via.placeholder.com/150" }
            .distinctBy { it.name }
    }
}

// Google Haritaları açan yardımcı fonksiyon
fun openGoogleMaps(context: Context, restaurantName: String) {
    // İsmi URL formatına çeviriyoruz (Boşluklar yerine + koyar)
    val query = Uri.encode(restaurantName)
    val gmmIntentUri = Uri.parse("geo:0,0?q=$query")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

    // Uygulama paketini Google Haritalar olarak kısıtlıyoruz
    mapIntent.setPackage("com.google.android.apps.maps")

    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        // Eğer telefonda Google Haritalar yüklü değilse tarayıcıyı açar
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$query"))
        context.startActivity(webIntent)
    }
}

suspend fun getPlacesGuideFromGemini(city: String, apiKey: String): String {
    return try {
        val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )

        // İŞTE SİHİR BURADA: JSON formatına lat ve lng eklendi.
        val prompt = "Bana $city şehrinde gezilecek en popüler 15 turistik mekanı tam olarak şu JSON formatında ver:\n" +
                "[\n" +
                "  {\n" +
                "    \"name\": \"Mekan Adı\",\n" +
                "    \"description\": \"Mekanın tarihi önemi (en az 3 cümle)\",\n" +
                "    \"imageUrl\": \"https://loremflickr.com/800/600/turkey,landmark\",\n" +
                "    \"location\": \"Açık Adres veya İlçe\",\n" +
                "    \"rating\": \"4.8\",\n" +
                "    \"lat\": 41.0082,\n" +
                "    \"lng\": 28.9784\n" +
                "  }\n" +
                "]\n" +
                "KRİTİK KURALLAR:\n" +
                "1. 'lat' ve 'lng' değerleri KESİNLİKLE DOUBLE (ondalıklı sayı) formatında GERÇEK dünya koordinatları olmalıdır.\n" +
                "2. Sadece saf JSON listesi döndür, markdown veya başka açıklama kullanma."

        val response = generativeModel.generateContent(prompt)
        val text = response.text ?: "[]"
        text.replace("```json", "").replace("```", "").trim()

    } catch (e: Exception) {
        android.util.Log.e("GEMINI_ERROR", "Yapay zeka hatası: ${e.message}")
        "[]"
    }
}

// --- YENİ EKLENEN: YAPAY ZEKADAN DETAYLI MEKAN BİLGİSİ ÇEKEN MOTOR ---
suspend fun getDetailedInfoFromGemini(
    city: String,
    placeName: String,
    apiKey: String,
    currentLanguage: AppLanguage
): String {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val placeRef = db.collection("city_guides").document(city)
            .collection("places").document(placeName)

        try {
            // 1. Firebase'de hangi "detay" kutusuna bakacağımızı seçiyoruz
            val dbFieldDetailed = when (currentLanguage) {
                AppLanguage.TR -> "detailedDescription"
                AppLanguage.EN -> "detailedDescriptionEn"
                AppLanguage.DE -> "detailedDescriptionDe"
                AppLanguage.FR -> "detailedDescriptionFr"
                AppLanguage.ES -> "detailedDescriptionEs"
            }

            // 2. Önce veritabanını kontrol et, eğer bu dilde detay zaten varsa API harcama, direkt getir!
            val documentSnapshot = placeRef.get().await()
            val existingDetailed = documentSnapshot.getString(dbFieldDetailed)
            if (!existingDetailed.isNullOrBlank()) {
                return@withContext existingDetailed
            }

            // 3. Veritabanında yoksa Gemini'ye soralım
            val promptLanguage = when (currentLanguage) {
                AppLanguage.EN -> "English"
                AppLanguage.DE -> "German"
                AppLanguage.FR -> "French"
                AppLanguage.ES -> "Spanish"
                else -> "Turkish"
            }

            val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey
            )

            val prompt = "Sen profesyonel bir turist rehberisin. Bana '$placeName' hakkında derinlemesine, tarihi ve turistik detayları içeren en az 2 paragraflık bir tarihçe yaz. Yazacağın metin KESİNLİKLE $promptLanguage dilinde olmalıdır."

            val response = generativeModel.generateContent(prompt)
            val aiDetailedResult = response.text?.trim() ?: "Bilgi alınamadı."

            // 4. İŞTE SİHİRLİ DOKUNUŞ: Gelen veriyi Firebase'de DOĞRU dilin kutusuna kaydediyoruz
            if (aiDetailedResult != "Bilgi alınamadı.") {
                val updateMap = hashMapOf<String, Any>(
                    dbFieldDetailed to aiDetailedResult
                )
                placeRef.set(updateMap, com.google.firebase.firestore.SetOptions.merge()).await()
            }

            return@withContext aiDetailedResult

        } catch (e: Exception) {
            "Hata: ${e.localizedMessage}"
        }
    }
}

fun parseGeminiResponse(jsonString: String): List<AiPlaceData> {
    val list = mutableListOf<AiPlaceData>()
    try {
        val array = org.json.JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                AiPlaceData(
                    name = obj.getString("name"),
                    description = obj.getString("description")
                )
            )
        }
    } catch (e: Exception) {
        android.util.Log.e("JSON_PARSE", "Hata: ${e.message}")
    }
    return list

}

suspend fun translateAndSaveDescription(
    city: String,
    placeName: String,
    originalText: String,
    languageName: String,
    languageCode: String,
    apiKey: String
): String {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val model = com.google.ai.client.generativeai.GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey
            )

            // 🔥 SIKI PROMPT: Gemini'ye sadece metni çevirmesini, başka yorum eklememesini söylüyoruz
            val prompt = """
                Aşağıdaki turistik mekan açıklamasını KESİNLİKLE $languageName diline çevir. 
                Sadece çevrilmiş metni ver, başına veya sonuna "İşte çeviri", "Tabii ki" gibi hiçbir yorum ekleme.
                
                Orijinal Metin:
                "$originalText"
            """.trimIndent()

            val response = model.generateContent(prompt)
            val translatedText = response.text?.trim()

            if (!translatedText.isNullOrBlank()) {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val placeRef = db.collection("city_guides").document(city)
                    .collection("places").document(placeName)

                // 🔥 Çeviriyi Firebase'e kalıcı olarak kaydet (Örn: description_en sütununa)
                val dbKey = "description_$languageCode"
                placeRef.set(mapOf(dbKey to translatedText), com.google.firebase.firestore.SetOptions.merge())

                return@withContext translatedText
            } else {
                return@withContext originalText // Çeviri başarısız olursa orijinali göster
            }
        } catch (e: Exception) {
            android.util.Log.e("CEVIRI_HATA", "Hata: ${e.message}")
            return@withContext originalText // İnternet koparsa vs. orijinal Türkçe metni döndür
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(strings: AppStrings, onBack: () -> Unit) {
    val tripBuddyBlue = Color(0xFF38A3A5)
    val tripBuddyOffWhite = Color(0xFFF8FAFC)
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val currentUid = auth.currentUser?.uid

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var myFriendsList by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    LaunchedEffect(currentUid) {
        if (currentUid != null) {
            db.collection("users").document(currentUid).addSnapshotListener { snapshot, _ ->
                val friendUids = snapshot?.get("friends") as? List<String> ?: emptyList()
                if (friendUids.isEmpty()) {
                    myFriendsList = emptyList()
                } else {
                    db.collection("users").whereIn(com.google.firebase.firestore.FieldPath.documentId(), friendUids).get().addOnSuccessListener { docs ->
                        myFriendsList = docs.map { mapOf("uid" to it.id, "username" to (it.getString("username") ?: "İsimsiz")) }
                    }
                }
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            db.collection("users").orderBy("username").startAt(searchQuery).endAt(searchQuery + "\uf8ff").get().addOnSuccessListener { documents ->
                val results = mutableListOf<Map<String, String>>()
                for (document in documents) {
                    if (document.id != currentUid) results.add(mapOf("uid" to document.id, "username" to (document.getString("username") ?: "")))
                }
                searchResults = results
                isSearching = false
            }
        } else { searchResults = emptyList() }
    }

    Scaffold(containerColor = tripBuddyOffWhite) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp).shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)).background(color = Color.White, shape = RoundedCornerShape(14.dp))) {
                    Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TripBuddyBlue)
                }
                Spacer(Modifier.width(16.dp))
                Text(text = strings.friendsTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF2C3E44))
            }

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                OutlinedTextField(
                    value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text(strings.searchUserHint, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = tripBuddyBlue) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = tripBuddyBlue, unfocusedBorderColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (searchQuery.length >= 3) {
                    Text(strings.searchResultsTitle, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))
                    Spacer(modifier = Modifier.height(12.dp))
                    androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(searchResults.size) { index ->
                            FriendItemRow(searchResults[index], isFriend = false, addText = strings.addBtn) {
                                if (currentUid != null) {
                                    val friendUid = searchResults[index]["uid"] ?: ""
                                    if (friendUid.isNotEmpty()) {
                                        db.collection("users").document(currentUid).set(hashMapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(friendUid)), com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener {
                                            db.collection("users").document(friendUid).set(hashMapOf("friends" to com.google.firebase.firestore.FieldValue.arrayUnion(currentUid)), com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener { searchQuery = "" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(strings.currentFriendsTitle, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2C3E44))
                    Spacer(modifier = Modifier.height(12.dp))
                    if (myFriendsList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(strings.noFriendsDesc, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(myFriendsList.size) { index ->
                                FriendItemRow(myFriendsList[index], isFriend = true, addText = strings.addBtn) { }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendItemRow(userData: Map<String, String>, isFriend: Boolean, addText: String, onClick: () -> Unit) {
    val tripBuddyBlue = Color(0xFF38A3A5)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(46.dp).background(Color(0xFFF1F5F9), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color.LightGray, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(userData["username"] ?: "", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E44))
            }
            if (!isFriend) {
                Button(onClick = onClick, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = tripBuddyBlue.copy(alpha = 0.1f), contentColor = tripBuddyBlue), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), modifier = Modifier.height(36.dp)) { Text(addText, fontWeight = FontWeight.Bold) }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tripBuddyBlue, modifier = Modifier.size(28.dp))
            }
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun NotificationsScreen(strings: AppStrings, onBack: () -> Unit, onSeeRouteClick: (List<PlaceData>) -> Unit) {
    val tripBuddyBlue = androidx.compose.ui.graphics.Color(0xFF38A3A5)
    val tripBuddyOffWhite = androidx.compose.ui.graphics.Color(0xFFF8FAFC)
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    var notifications by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList())
    }

    androidx.compose.runtime.LaunchedEffect(currentUid) {
        if (currentUid != null) {
            db.collection("users").document(currentUid)
                .collection("notifications")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val notifList = mutableListOf<Pair<String, Map<String, Any>>>()
                        val batch = db.batch()
                        var hasUnread = false

                        for (doc in snapshot.documents) {
                            doc.data?.let { data ->
                                notifList.add(Pair(doc.id, data))
                                if (data["isRead"] == false) {
                                    batch.update(doc.reference, "isRead", true)
                                    hasUnread = true
                                }
                            }
                        }
                        notifications = notifList
                        if (hasUnread) batch.commit()
                    }
                }
        }
    }

    androidx.compose.material3.Scaffold(containerColor = tripBuddyOffWhite) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(padding)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    androidx.compose.material3.IconButton(
                        onClick = onBack,
                        modifier = androidx.compose.ui.Modifier
                            .size(44.dp)
                            .shadow(elevation = 2.dp, shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                            .background(color = androidx.compose.ui.graphics.Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = tripBuddyBlue)
                    }
                    androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.width(16.dp))
                    androidx.compose.material3.Text(strings.notificationsTitle ?: "Bildirimler", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, color = androidx.compose.ui.graphics.Color(0xFF2C3E44))
                }

                if (notifications.isNotEmpty()) {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            if (currentUid != null) {
                                val batch = db.batch()
                                notifications.forEach { (id, _) ->
                                    val docRef = db.collection("users").document(currentUid).collection("notifications").document(id)
                                    batch.delete(docRef)
                                }
                                batch.commit()
                            }
                        }
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.DeleteSweep, contentDescription = "Tümünü Sil", tint = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.7f))
                    }
                }
            }

            if (notifications.isEmpty()) {
                androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    androidx.compose.material3.Text("Henüz bir bildirimin yok.", color = androidx.compose.ui.graphics.Color.Gray)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications.size) { index ->
                        val data = notifications[index].second // 🔥 data burada tanımlı!
                        val senderName = data["senderName"] as? String ?: "Bir arkadaşın"
                        val routeName = data["routeName"] as? String ?: "bir rota"
                        val timestamp = data["timestamp"] as? Long ?: 0L

                        val timeString = if (timestamp > 0L) {
                            java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale("tr")).format(java.util.Date(timestamp))
                        } else ""

                        androidx.compose.material3.Card(
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp)
                        ) {
                            androidx.compose.foundation.layout.Row(
                                modifier = androidx.compose.ui.Modifier.padding(vertical = 16.dp, horizontal = 16.dp).fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.layout.Row(
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    modifier = androidx.compose.ui.Modifier.weight(1f)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        modifier = androidx.compose.ui.Modifier
                                            .size(40.dp)
                                            .background(color = tripBuddyBlue.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.CircleShape),
                                                contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = tripBuddyBlue,
                                            modifier = androidx.compose.ui.Modifier.size(20.dp)
                                        )
                                    }
                                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                                    androidx.compose.foundation.layout.Column {
                                        androidx.compose.material3.Text(text = "$senderName sana $routeName gönderdi.", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFF2C3E44))
                                        androidx.compose.material3.Text(text = timeString, color = androidx.compose.ui.graphics.Color.Gray, fontSize = 12.sp)
                                    }
                                }

                                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))

                                androidx.compose.material3.Button(
                                    onClick = {
                                        val rawPlaces = data["places"] as? List<Map<String, Any>> ?: emptyList()
                                        val convertedPlaces = rawPlaces.map { placeMap ->
                                            PlaceData(
                                                name = placeMap["name"] as? String ?: "Bilinmeyen Mekan",
                                                lat = placeMap["lat"] as? Double ?: 0.0,
                                                lng = placeMap["lng"] as? Double ?: 0.0,
                                                rating = placeMap["rating"] as? String ?: "0.0",
                                                location = "", imageUrl = "", description = ""
                                            )
                                        }
                                        onSeeRouteClick(convertedPlaces)
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = tripBuddyBlue.copy(alpha = 0.1f),
                                        contentColor = tripBuddyBlue
                                    ),
                                    elevation = null,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                    modifier = androidx.compose.ui.Modifier.height(36.dp)
                                ) {
                                    androidx.compose.material3.Text("Rotayı Gör", fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun SharedRoutesScreen(strings: AppStrings, onBack: () -> Unit, onRouteClick: (List<PlaceData>) -> Unit) {
    val tripBuddyBlue = androidx.compose.ui.graphics.Color(0xFF38A3A5)
    val tripBuddyOffWhite = androidx.compose.ui.graphics.Color(0xFFF8FAFC)
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    var incomingRoutes by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    var senderNames by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Map<String, String>>(emptyMap()) }

    androidx.compose.runtime.LaunchedEffect(currentUid) {
        if (currentUid != null) {
            db.collection("shared_routes").whereArrayContains("sharedWith", currentUid).addSnapshotListener { snapshot, error ->
                if (error != null) { isLoading = false; return@addSnapshotListener }
                if (snapshot != null) {
                    val routesList = mutableListOf<Map<String, Any>>()
                    val uidsToFetch = mutableSetOf<String>()
                    for (document in snapshot.documents) {
                        val routeData = document.data?.toMutableMap() ?: continue
                        routesList.add(routeData)
                        val creatorUid = routeData["creatorUid"] as? String
                        if (creatorUid != null) uidsToFetch.add(creatorUid)
                    }
                    incomingRoutes = routesList

                    if (uidsToFetch.isNotEmpty()) {
                        db.collection("users").whereIn(com.google.firebase.firestore.FieldPath.documentId(), uidsToFetch.toList()).get().addOnSuccessListener { usersSnap ->
                            val namesMap = mutableMapOf<String, String>()
                            for (uDoc in usersSnap.documents) namesMap[uDoc.id] = uDoc.getString("username") ?: "Bilinmeyen"
                            senderNames = namesMap
                            isLoading = false
                        }.addOnFailureListener { isLoading = false }
                    } else { isLoading = false }
                }
            }
        }
    }

    androidx.compose.material3.Scaffold(containerColor = tripBuddyOffWhite) { padding ->
        androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(padding)) {

            // ÜST BAR BÖLÜMÜ
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.IconButton(
                    onClick = onBack,
                    // 🔥 HATA BURADAYDI: shadow ve background düzeltildi
                    modifier = androidx.compose.ui.Modifier
                        .size(40.dp)
                        .background(androidx.compose.ui.graphics.Color(0xFFFFF0F5), androidx.compose.foundation.shape.CircleShape)
                ) {
                    androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = tripBuddyBlue)
                }
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(16.dp))
                androidx.compose.material3.Text(strings.incomingRoutesTitle ?: "Gelen Rotalar", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, color = androidx.compose.ui.graphics.Color(0xFF2C3E44))
            }

            // LİSTE BÖLÜMÜ
            androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                if (isLoading) {
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(color = tripBuddyBlue)
                    }
                } else if (incomingRoutes.isEmpty()) {
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.Map, contentDescription = null, modifier = androidx.compose.ui.Modifier.size(64.dp), tint = androidx.compose.ui.graphics.Color.LightGray)
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
                            androidx.compose.material3.Text(strings.noRoutesDesc ?: "Rota yok", color = androidx.compose.ui.graphics.Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    androidx.compose.material3.Text(strings.routesFromFriendsTitle ?: "Arkadaşlardan Gelen Rotalar", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, color = androidx.compose.ui.graphics.Color(0xFF2C3E44))
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                    androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                        items(incomingRoutes.size) { index ->
                            val route = incomingRoutes[index]
                            val rawPlaces = route["places"] as? List<Map<String, Any>> ?: emptyList()
                            val creatorUid = route["creatorUid"] as? String
                            val senderName = senderNames[creatorUid] ?: "Bir arkadaşın"

                            androidx.compose.material3.Card(
                                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                                elevation = androidx.compose.material3.CardDefaults.cardElevation(2.dp)
                            ) {
                                androidx.compose.foundation.layout.Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
                                    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {

                                        // 🔥 HATA BURADAYDI: background düzeltildi
                                        androidx.compose.foundation.layout.Box(
                                            modifier = androidx.compose.ui.Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = androidx.compose.ui.graphics.Color(0xFFFFF0F5),
                                                    shape = androidx.compose.foundation.shape.CircleShape
                                                ),
                                            contentAlignment = androidx.compose.ui.Alignment.Center
                                        )  {
                                            androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Default.DirectionsWalk, null, tint = androidx.compose.ui.graphics.Color(0xFFE91E63), modifier = androidx.compose.ui.Modifier.size(20.dp))
                                        }

                                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))
                                        androidx.compose.foundation.layout.Column {
                                            androidx.compose.material3.Text("$senderName ${strings.routeSentText ?: "sana bir rota gönderdi."}", fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, fontSize = 15.sp, color = androidx.compose.ui.graphics.Color(0xFF2C3E44))
                                            androidx.compose.material3.Text("${strings.totalStopsText ?: "Toplam Durak:"} ${rawPlaces.size}", color = androidx.compose.ui.graphics.Color.Gray, fontSize = 13.sp)
                                        }
                                    }
                                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            val convertedPlaces = rawPlaces.map { placeMap -> PlaceData(name = placeMap["name"] as? String ?: "Bilinmeyen Mekan", lat = placeMap["lat"] as? Double ?: 0.0, lng = placeMap["lng"] as? Double ?: 0.0, rating = placeMap["rating"] as? String ?: "0.0", location = "", imageUrl = "", description = "") }
                                            onRouteClick(convertedPlaces)
                                        },
                                        modifier = androidx.compose.ui.Modifier.fillMaxWidth().height(44.dp),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = tripBuddyBlue)
                                    ) {
                                        androidx.compose.material3.Text(strings.inspectRouteBtn ?: "Rotayı İncele", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}