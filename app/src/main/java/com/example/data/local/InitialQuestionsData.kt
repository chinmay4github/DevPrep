package com.example.data.local

import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.QuestionEntity
import com.example.domain.model.Category
import com.example.domain.model.Difficulty
import com.example.domain.model.QuestionType

object InitialQuestionsData {
    fun getInitialCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(
            id = Category.ANDROID_ARCHITECTURE.id,
            name = "Android & Clean Architecture",
            shortName = "Android",
            description = "Jetpack Compose, MVVM, Clean Architecture, StateFlow, Coroutines, and Lifecycle",
            iconName = "android",
            displayOrder = 1
        ),
        CategoryEntity(
            id = Category.ROOM_PRODUCTION.id,
            name = "Room DB & Production Builds",
            shortName = "Room & APK",
            description = "Room migrations, SQLite queries, APK build variants, R8/ProGuard, and WorkManager",
            iconName = "storage",
            displayOrder = 2
        ),
        CategoryEntity(
            id = Category.REACT_NATIVE_SECURITY.id,
            name = "React Native & Token Security",
            shortName = "React Native",
            description = "Network API, Axios interceptors, JWT refresh token flow, Keystore/Keychain, and SSL Pinning",
            iconName = "security",
            displayOrder = 3
        ),
        CategoryEntity(
            id = Category.IOS_SWIFT_CONCURRENCY.id,
            name = "iOS & Swift Concurrency",
            shortName = "iOS",
            description = "Swift Concurrency, Actors, ARC memory management, SwiftUI vs UIKit, and Core Data",
            iconName = "phone_iphone",
            displayOrder = 4
        )
    )

    fun getInitialQuestions(): List<QuestionEntity> = listOf(
        // ==========================================
        // SECTION 1: REACT NATIVE & TOKEN SECURITY
        // ==========================================
        QuestionEntity(
            id = 1,
            category = Category.REACT_NATIVE_SECURITY.id,
            title = "Axios Interceptor Token Refresh Race Condition",
            questionText = "When multiple simultaneous network requests fail with HTTP 401 Unauthorized, how do you prevent multiple refresh token API calls and replay pending requests with the newly minted access token?",
            scenarioContext = "A user opens a dashboard that fires 4 parallel requests (Profile, Feed, Notifications, Config). All 4 receive 401 simultaneously because the access token expired.",
            codeSnippet = """// Axios Response Interceptor with Promise Queue
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) prom.reject(error);
    else prom.resolve(token);
  });
  failedQueue = [];
};

axiosInstance.interceptors.response.use(
  res => res,
  async error => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers['Authorization'] = 'Bearer ' + token;
          return axiosInstance(originalRequest);
        });
      }
      originalRequest._retry = true;
      isRefreshing = true;
      try {
        const { newToken } = await authService.refresh();
        processQueue(null, newToken);
        originalRequest.headers['Authorization'] = 'Bearer ' + newToken;
        return axiosInstance(originalRequest);
      } catch (refreshErr) {
        processQueue(refreshErr, null);
        authService.logout();
        return Promise.reject(refreshErr);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);""",
            optionsJson = "Queue pending requests in a FIFO array until the single refresh completes, then resolve and replay|||Cancel all failed requests and force user to log in again|||Call refresh token endpoint concurrently for each 401 and take the fastest one|||Use setTimeout to delay retrying without token exchange",
            correctOptionIndex = 0,
            answerSummary = "Use a locking boolean flag (`isRefreshing`) and a `failedQueue` array in the Axios response interceptor. The first 401 initiates the refresh call while subsequent 401s return unresolved Promises queued in the array. Once refreshed, iterate the queue, update the Authorization header with the new token, and replay all requests.",
            deepDiveExplanation = "If multiple refresh requests hit the OAuth server concurrently, token rotation policies will revoke the refresh token family as a potential replay attack, forcefully logging the user out. Queueing guarantees exactly-one refresh execution, eliminates race conditions, and provides seamless silent renewal.",
            followUpInterviewerQuestion = "How do you handle a scenario where the refresh token itself fails with a 401 or network timeout while requests are sitting in the queue?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 2,
            category = Category.REACT_NATIVE_SECURITY.id,
            title = "Secure Token Storage: Keystore & Keychain vs AsyncStorage",
            questionText = "Why is storing JWT access and refresh tokens in AsyncStorage or MMKV a high-severity security risk in production mobile applications?",
            scenarioContext = "A penetration test reports vulnerability CWE-312: Cleartext Storage of Sensitive Information found in the app's React Native storage layer.",
            codeSnippet = """// ✅ Secure: react-native-keychain
import * as Keychain from 'react-native-keychain';

await Keychain.setGenericPassword('auth_session', JSON.stringify({ accessToken, refreshToken }), {
  service: 'com.myapp.auth',
  accessible: Keychain.ACCESSIBLE.WHEN_UNLOCKED_THIS_DEVICE_ONLY,
  securityLevel: Keychain.SECURITY_LEVEL.SECURE_HARDWARE
});

// ❌ Insecure: AsyncStorage (Plain XML on Android, plist on iOS)
// AsyncStorage.setItem('tokens', JSON.stringify({ accessToken }));""",
            optionsJson = "AsyncStorage and unencrypted MMKV store plaintext data on disk readable by rooted/jailbroken devices or backup exploits|||AsyncStorage has an arbitrary 6MB size limit preventing token storage|||AsyncStorage cannot serialize JSON strings|||AsyncStorage is synchronous and freezes the React Native JS thread",
            correctOptionIndex = 0,
            answerSummary = "AsyncStorage stores strings in unencrypted files (SQLite or XML) within the app sandbox. Anyone with physical access to a rooted/jailbroken device, or utilizing ADB backup/forensic dumps, can inspect tokens. Android Keystore (backed by TEE/StrongBox) and iOS Keychain provide hardware-backed encryption with hardware-isolated cryptographic key protection.",
            deepDiveExplanation = "For maximum enterprise compliance (PCI-DSS / HIPAA), use `react-native-keychain` or `react-native-encrypted-storage`. On Android, this uses AndroidX Security's `MasterKey` in Android Keystore with AES256-GCM encryption. On iOS, tokens reside in `kSecClassGenericPassword` with `kSecAttrAccessibleWhenUnlockedThisDeviceOnly` preventing cloud backup extraction.",
            followUpInterviewerQuestion = "What happens to keys stored in Android Keystore when a user changes their device lock screen PIN or biometric enrollment?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 3,
            category = Category.REACT_NATIVE_SECURITY.id,
            title = "SSL / TLS Pinning & Certificate Rotation Strategy",
            questionText = "How do you implement SSL Pinning in a cross-platform mobile app to prevent Man-in-the-Middle (MITM) attacks while guarding against app lockouts upon certificate expiry?",
            scenarioContext = "An attacker uses proxy software like Charles Proxy or mitmproxy with a custom CA certificate installed on a test device to intercept encrypted mobile network traffic.",
            codeSnippet = """<!-- res/xml/network_security_config.xml (Android) -->
<network-security-config>
  <domain-config>
    <domain includeSubdomains="true">api.mycompany.com</domain>
    <pin-set expiration="2027-12-31">
      <!-- Primary leaf certificate SPKI SHA-256 -->
      <pin digest="SHA-256">47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=</pin>
      <!-- Backup Intermediate / CA pin for emergency rotation -->
      <pin digest="SHA-256">k2+HpRPNOXJY8Hzqk2YDkQIRU2x3fcBiXIq+RfAhEk8=</pin>
    </pin-set>
  </domain-config>
</network-security-config>""",
            optionsJson = "Pin the Subject Public Key Info (SPKI) SHA-256 hash and include at least one backup CA/intermediate pin|||Pin the raw leaf certificate byte array and set no expiry date|||Disable HTTPS and rely entirely on AES application-level payload encryption|||Rely on default OS trust store without pinning",
            correctOptionIndex = 0,
            answerSummary = "Pin the Subject Public Key Info (SPKI) SHA-256 hash rather than the entire leaf certificate. SPKI pinning allows re-issuing certificates with the same CSR/public key without breaking existing installed apps. Always configure at least one backup pin (e.g., intermediate CA or cold backup key) to avoid bricking apps if keys must be revoked.",
            deepDiveExplanation = "Leaf certificate pinning breaks whenever the certificate expires (now 90-397 days). If an app is pinned to an expired certificate and lacks a backup pin, users will be locked out until an App Store update is published. SPKI pinning on the public key ensures renewals with the same private key pair succeed without app updates.",
            followUpInterviewerQuestion = "How do you bypass SSL pinning during internal QA automation without creating separate insecure build flavors?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 4,
            category = Category.REACT_NATIVE_SECURITY.id,
            title = "OAuth 2.0 PKCE Flow in Native & Hybrid Apps",
            questionText = "Why is the PKCE (Proof Key for Code Exchange) extension strictly required when mobile apps implement OAuth 2.0 Authorization Code flow?",
            scenarioContext = "A malicious third-party app registers the same custom URL scheme (e.g. `myapp://oauth-callback`) to intercept authorization codes.",
            codeSnippet = """// Client creates code_verifier and code_challenge:
// code_verifier = high-entropy cryptographic random string [43..128 chars]
// code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))

// Step 1: Request auth code with code_challenge
// GET /authorize?response_type=code&client_id=123&code_challenge=xyz&code_challenge_method=S256

// Step 2: Exchange code for tokens sending the raw code_verifier
// POST /token 
// Body: grant_type=authorization_code&code=abc&code_verifier=high_entropy_secret""",
            optionsJson = "Because mobile apps cannot safely maintain a client_secret; PKCE binds authorization requests to token exchanges|||Because PKCE eliminates the need for HTTPS transport encryption|||Because PKCE speeds up OAuth handshake latency by skipping cryptographic verification|||Because PKCE allows tokens to never expire",
            correctOptionIndex = 0,
            answerSummary = "Mobile apps are 'public clients' because decompiling the APK or IPA reveals hardcoded secrets. Without PKCE, any rogue app intercepting the redirect URI can exchange the auth code for access tokens. With PKCE, only the client that generated the initial `code_verifier` can fulfill the token exchange with the server.",
            deepDiveExplanation = "RFC 7636 prevents Authorization Code Interception Attacks. The client generates a random `code_verifier`, hashes it with SHA-256 into `code_challenge`, and sends it to the Auth server. Even if an attacker intercepts the returned authorization code via an intent-filter hijack, they cannot obtain tokens without the original `code_verifier` stored in memory.",
            followUpInterviewerQuestion = "Why should mobile apps prefer Android App Links / iOS Universal Links over Custom URL Schemes for OAuth redirects?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),

        // ==========================================
        // SECTION 2: REAL-TIME SCENARIOS: ROOM DB & BUILD
        // ==========================================
        QuestionEntity(
            id = 5,
            category = Category.ROOM_PRODUCTION.id,
            title = "Production Crash: Room Migration Hash Mismatch",
            questionText = "Your app crashes immediately on launch after updating to a new version with: `IllegalStateException: Migration failed. Room cannot verify the data integrity. Expected hash: xyz, found: abc`. What is the root cause and standard mitigation?",
            scenarioContext = "A production release crashes for 40% of returning users who upgraded from database version 2 to version 3.",
            codeSnippet = """// Migration Definition
val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(db: SupportSQLiteDatabase) {
    // ⚠️ Trap: Column nullability or default value mismatch with Entity definition!
    // If Entity has: val priority: Int = 0 (non-null)
    // SQL must be: NOT NULL DEFAULT 0
    db.execSQL("ALTER TABLE tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
  }
}

// In test/MigrationTest.kt (Robolectric / JVM Unit Test):
@Test
fun migrate2To3() {
  val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java)
  helper.createDatabase(TEST_DB, 2).apply { close() }
  helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)
}""",
            optionsJson = "The SQL schema generated by the migration did not exactly match Room's generated JSON schema (e.g. nullability, indices, or defaults)|||The user's device ran out of SQLite disk space|||Room requires re-installing the APK to clear old cache|||The SQLite version in Android 14 changed table constraints automatically",
            correctOptionIndex = 0,
            answerSummary = "Room compiles a strict identity hash from the entities (stored in Room's schema JSON) and compares it with the schema of the migrated SQLite table. If there is even a subtle discrepancy—such as a missing index, different column collation, or nullability mismatch (e.g., column declared `INTEGER NOT NULL` without `DEFAULT`)—Room throws an `IllegalStateException`.",
            deepDiveExplanation = "Never rely on manual migration guessing. Enable `exportSchema = true` in `@Database`, and write unit tests using `MigrationTestHelper`. Check the diff between `schemas/.../2.json` and `3.json`. Inspect the generated `AppDatabase_Impl.java` to see the exact CREATE TABLE and CREATE INDEX statements Room expects.",
            followUpInterviewerQuestion = "Under what circumstances is `fallbackToDestructiveMigration()` acceptable in production?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 6,
            category = Category.ROOM_PRODUCTION.id,
            title = "Room @Relation vs Manual SQL Joins in Large Datasets",
            questionText = "When querying one-to-many parent-child relationships with thousands of records, why does Room's `@Relation` annotation cause severe memory overhead and frame drops compared to targeted SQL queries?",
            scenarioContext = "A user profile screen loading 5,000 transaction line-items stutters with jank and triggers an OutOfMemoryError.",
            codeSnippet = """// ⚠️ High overhead with huge child datasets:
data class UserWithTransactions(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "ownerId"
    )
    val transactions: List<TransactionEntity> // Loads entire list into memory!
)

// ✅ High performance alternative:
// 1. Paging 3: PagingSource<Int, TransactionEntity>
// 2. Focused Projection with Join:
@Query(""\"
    SELECT u.name, t.amount, t.timestamp 
    FROM users u INNER JOIN transactions t ON u.userId = t.ownerId 
    WHERE u.userId = :userId ORDER BY t.timestamp DESC LIMIT :limit
""\")
fun getRecentTransactions(userId: String, limit: Int): Flow<List<TransactionSummary>>""",
            optionsJson = "Room executes multi-step SELECT queries in memory without cursor pagination, loading all child rows at once|||Room relations run on the Android Main thread by default|||SQLite prohibits foreign key relations with more than 100 rows|||@Relation requires converting rows to JSON in SQLite",
            correctOptionIndex = 0,
            answerSummary = "Room's `@Relation` performs two distinct queries under the hood: it queries parent entities, extracts their primary keys, and then executes a second `SELECT * FROM child WHERE id IN (...)` query loading every child into memory as complete Kotlin objects. With thousands of items, this creates massive memory allocations and garbage collection pauses.",
            deepDiveExplanation = "To optimize relational data in Android: (1) Use Paging 3 with Room for paginated scrolling, (2) Use targeted SQL projections returning only the needed columns into lightweight DTOs, or (3) Use manual joins to stream results rather than hydrating full entity relationship graphs.",
            followUpInterviewerQuestion = "How does Room's `@Transaction` annotation guarantee consistency across multi-query `@Relation` reads?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 7,
            category = Category.ROOM_PRODUCTION.id,
            title = "Debugging Release-Only Crashes with R8 / ProGuard",
            questionText = "A build works perfectly in Debug mode but crashes in Release mode with `NoSuchMethodException` or `NullPointerException` during JSON deserialization or Room query execution. How do you diagnose and resolve it?",
            scenarioContext = "Your release APK crashes immediately on launch when parsing user authentication payloads or loading Room entities.",
            codeSnippet = """// 1. Identify missing ProGuard keep rules
// proguard-rules.pro
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

// Keep data models used with Moshi / Gson reflection:
-keepclassmembers class com.example.data.remote.model.** {
    <fields>;
    <init>(...);
}

// Or use Moshi CodeGen / kotlinx.serialization with KSP to avoid reflection:
@JsonClass(generateAdapter = true)
data class UserDto(val id: String, val name: String)

// 2. Retain mapping file for symbol de-obfuscation:
// build/outputs/mapping/release/mapping.txt""",
            optionsJson = "R8 stripped or renamed fields and default constructors used by reflection; resolve by adding @Keep or compile-time codegen|||Release APKs run a different JVM architecture than debug APKs|||ProGuard modifies AndroidManifest package names|||Room requires setting debuggable true in release builds",
            correctOptionIndex = 0,
            answerSummary = "R8 minification obfuscates class and field names and aggressively strips unused constructors. If a reflection-based serializer (Moshi without codegen, Gson) or Room query expects exact field names, reflection fails. To resolve: apply `@Keep`, configure `proguard-rules.pro`, or migrate to compile-time code generation (Moshi KSP or kotlinx.serialization).",
            deepDiveExplanation = "Always analyze the crash stacktrace by uploading `mapping.txt` to Google Play Console or using the Android command-line `retrace` tool. For library authors, bundle consumer ProGuard rules via `consumer-rules.pro` so consuming apps inherit the keep rules automatically.",
            followUpInterviewerQuestion = "What is the difference between R8 full mode and R8 compatibility mode in Android Gradle Plugin?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 8,
            category = Category.ROOM_PRODUCTION.id,
            title = "WorkManager Offline Sync & Conflict Resolution",
            questionText = "In an offline-first architecture syncing local Room data with a remote server, what is the best strategy to resolve write conflicts when both client and server update the same record?",
            scenarioContext = "A user edits a task offline on their phone. Meanwhile, a teammate updates the same task on the web dashboard.",
            codeSnippet = """// Entity with Version Vector / Revision Token
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isCompleted: Boolean,
    val version: Long, // Incremented on each update
    val isDirty: Boolean = false, // Pending sync
    val lastModifiedAt: Long = System.currentTimeMillis()
)

// Sync Logic in Coroutine Worker:
suspend fun syncTask(task: TaskEntity, remoteTask: RemoteTask) {
  if (task.version == remoteTask.version) {
    // Clean update
    api.updateTask(task.toDto())
    dao.markSynced(task.id, newVersion = task.version + 1)
  } else {
    // Conflict detected: Version mismatch!
    // Strategies: Three-Way Merge, Field-Level CRDT, or User Resolution Prompt
    resolveConflict(local = task, remote = remoteTask)
  }
}""",
            optionsJson = "Use revision numbers / version tokens and apply 3-way merge or field-level CRDTs, avoiding pure client-side timestamps|||Always overwrite server data with local device data blindly|||Delete local records whenever remote updates exist|||Block user interaction whenever the device is disconnected from Wi-Fi",
            correctOptionIndex = 0,
            answerSummary = "Never rely purely on client device timestamps because device clocks can be skewed or manipulated. Use server-assigned revision tokens or version vectors. When a version conflict occurs, either perform a field-level three-way merge (combining non-conflicting field edits) or use Conflict-free Replicated Data Types (CRDTs), falling back to user prompts when conflicting fields overlap.",
            deepDiveExplanation = "Using WorkManager with `ExistingWorkPolicy.APPEND_OR_REPLACE` or `KEEP` ensures sequential background sync execution. Local records are flagged with `isDirty = true` until the server acknowledges with a 200 OK and updated revision ID, ensuring transactional safety even during process death.",
            followUpInterviewerQuestion = "How do you handle exponential backoff and battery constraints when scheduling sync tasks with WorkManager?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),

        // ==========================================
        // SECTION 3: IOS QUESTION SET (MCQ FORMAT)
        // ==========================================
        QuestionEntity(
            id = 9,
            category = Category.IOS_SWIFT_CONCURRENCY.id,
            title = "Swift Concurrency: Actor Isolation vs DispatchQueue.main",
            questionText = "Which statement accurately describes how Swift Actors prevent data races compared to legacy GCD dispatch queues?",
            scenarioContext = "A senior iOS developer is refactoring shared mutable state across background threads to Swift 6 strict concurrency standards.",
            codeSnippet = """// Swift Actor Example
actor BankAccount {
    private var balance: Double = 0.0

    func deposit(amount: Double) {
        balance += amount
    }

    func getBalance() -> Double {
        return balance
    }
}

// Caller must await access outside actor:
let account = BankAccount()
await account.deposit(amount: 100.0)""",
            optionsJson = "Actors serialize access to their internal state via compiler-enforced data isolation, preventing simultaneous mutable access|||Actors execute all operations on the Main thread synchronously|||Actors replace locks with background OS threads created for every function call|||Actors allow arbitrary concurrent writes from any Task without synchronization",
            correctOptionIndex = 0,
            answerSummary = "Swift actors provide compiler-enforced synchronization. Only one task can execute actor-isolated code at any given time, preventing low-level data races without requiring manual locks or `DispatchQueue` synchronization. Cross-actor calls require `await`, signaling potential suspension points.",
            deepDiveExplanation = "Unlike GCD dispatch queues where developers must manually maintain serial queues or barrier blocks, the Swift compiler statically checks Sendable conformance and actor boundaries. However, actors are susceptible to 'actor reentrancy': between `await` points, another task can modify actor state, so state must be validated after each suspension.",
            followUpInterviewerQuestion = "What is 'actor reentrancy' in Swift and how can it introduce subtle logic bugs across await suspension points?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 10,
            category = Category.IOS_SWIFT_CONCURRENCY.id,
            title = "Automatic Reference Counting: [weak self] vs [unowned self]",
            questionText = "In Swift closures, when is it appropriate to use `[unowned self]` instead of `[weak self]`, and what happens if the referenced instance is deallocated?",
            scenarioContext = "Reviewing a PR where an async network completion handler captures `[unowned self]`.",
            codeSnippet = """class NetworkManager {
    func fetchData(completion: @escaping (Data) -> Void) {
        // Asynchronous URLSession call...
    }
}

class DetailViewController: UIViewController {
    var network = NetworkManager()

    func loadProfile() {
        // ⚠️ What happens if user navigates back before network finishes?
        network.fetchData { [unowned self] data in
            self.updateUI(with: data) // CRASH: Fatal error: Attempted to read dangling pointer!
        }
    }
}""",
            optionsJson = "Use [unowned self] only when the closure and captured instance have identical lifecycles; accessing deallocated unowned causes a crash|||[unowned self] automatically defaults to nil when deallocated, identical to weak self|||[unowned self] retains the instance indefinitely preventing deallocation|||[unowned self] is required for all escaping closures in Swift 5+",
            correctOptionIndex = 0,
            answerSummary = "Use `[unowned self]` only when you are 100% certain the closure and captured instance share the exact same lifetime and self will never be deallocated before the closure executes. If self is deallocated, accessing `[unowned self]` crashes the app with a memory trap. `[weak self]` creates an optional reference that becomes `nil` safely.",
            deepDiveExplanation = "For any asynchronous operation (such as network calls, timers, or background tasks), always prefer `[weak self]` with `guard let self else { return }`. `[unowned self]` is only suitable for parent-child hierarchies where the child closure cannot outlive the parent, or in simple delegate/closure relationships that execute synchronously.",
            followUpInterviewerQuestion = "What is the difference between `unowned(safe)` and `unowned(unsafe)` in Swift memory management?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 11,
            category = Category.IOS_SWIFT_CONCURRENCY.id,
            title = "SwiftUI vs UIKit: @StateObject vs @ObservedObject",
            questionText = "What is the critical lifecycle difference between `@StateObject` and `@ObservedObject` in SwiftUI?",
            scenarioContext = "A list item re-renders and unexpectedly recreates a ViewModel, resetting form state and re-triggering network requests.",
            codeSnippet = """struct ParentView: View {
    @State private var counter = 0

    var body: some View {
        VStack {
            Button("Increment: \(counter)") { counter += 1 }
            // When ParentView re-evaluates body:
            ChildView()
        }
    }
}

struct ChildView: View {
    // ⚠️ Re-instantiated every time ParentView re-renders!
    // @ObservedObject var viewModel = MyViewModel()

    // ✅ Preserved across parent view re-evaluations:
    @StateObject var viewModel = MyViewModel()

    var body: some View { Text(viewModel.data) }
}""",
            optionsJson = "@StateObject maintains ownership across view re-renderings; @ObservedObject can be re-created every time body executes|||@ObservedObject is for Core Data; @StateObject is for Swift structs|||@StateObject runs on background threads; @ObservedObject runs on main thread|||@StateObject was deprecated in iOS 15 in favor of @ObservedObject",
            correctOptionIndex = 0,
            answerSummary = "Use `@StateObject` when the View creates and owns the `ObservableObject` instance. SwiftUI instantiates it once and keeps it alive across view re-evaluations. Use `@ObservedObject` only when passing an already-instantiated object created by an ancestor view.",
            deepDiveExplanation = "If you instantiate an object using `@ObservedObject var vm = ViewModel()` inside a view, every time that view's parent invalidates and re-computes its `body`, a new instance of `ViewModel` is constructed, discarding previous state and re-running init methods. With `@StateObject`, SwiftUI manages storage in its internal view graph.",
            followUpInterviewerQuestion = "How does the newer `@Observable` macro introduced in iOS 17 change state management compared to `ObservableObject`?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 12,
            category = Category.IOS_SWIFT_CONCURRENCY.id,
            title = "Core Data Multithreading: NSManagedObjectContext Concurrency",
            questionText = "Why must an `NSManagedObject` never be passed across different thread boundaries or contexts in Core Data?",
            scenarioContext = "An app crashes with `EXC_BAD_ACCESS` or SQLite lock error during background Core Data save.",
            codeSnippet = """// ❌ Wrong: Accessing object on background thread
let user = mainContext.fetch(fetchRequest).first!
DispatchQueue.global().async {
    user.lastLogin = Date() // CRASH: Thread confinement violation!
}

// ✅ Correct: Pass NSManagedObjectID
let objectID = user.objectID
persistentContainer.performBackgroundTask { bgContext in
    let bgUser = bgContext.object(with: objectID) as? User
    bgUser?.lastLogin = Date()
    try? bgContext.save()
}""",
            optionsJson = "NSManagedObject instances are not thread-safe; only NSManagedObjectID can be safely passed between contexts|||Core Data automatically converts all models to immutable structs on background threads|||SQLite requires closing the database before switching threads|||NSManagedObject locks the CPU when accessed from a secondary queue",
            correctOptionIndex = 0,
            answerSummary = "Neither `NSManagedObject` nor `NSManagedObjectContext` is thread-safe. A context and its managed objects must only be accessed on the thread/queue where they were created. To pass data across threads, pass the lightweight, thread-safe `NSManagedObjectID` and fetch the object on the destination context using `perform` or `performBackgroundTask`.",
            deepDiveExplanation = "Attempting to read or mutate properties of an `NSManagedObject` from another thread violates Core Data thread confinement rules and causes memory corruption or crashes. Modern Core Data enforces this with `-com.apple.CoreData.ConcurrencyDebug 1` launch argument, instantly throwing an assertion if violated.",
            followUpInterviewerQuestion = "How do you automatically merge changes from a background context into the viewContext in modern Core Data?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),

        // ==========================================
        // SECTION 4: ANDROID KOTLIN: MVVM & CLEAN ARCHITECTURE
        // ==========================================
        QuestionEntity(
            id = 13,
            category = Category.ANDROID_ARCHITECTURE.id,
            title = "StateFlow vs SharedFlow: Cold vs Hot & Single-Event Anti-Patterns",
            questionText = "What is the key architectural difference between `StateFlow` and `SharedFlow`, and why is using `SharedFlow` with `replay = 0` for one-off events (like navigation or snackbars) problematic in Compose?",
            scenarioContext = "A navigation event emitted in a ViewModel is missed if the app is briefly backgrounded, or re-triggered on configuration change.",
            codeSnippet = """// ⚠️ SharedFlow anti-pattern for one-off events:
private val _eventFlow = MutableSharedFlow<UiEvent>()
val eventFlow = _eventFlow.asSharedFlow() // Events lost if collected before UI is ready!

// ✅ Recommended Compose Pattern: State-based events or Channels
sealed interface UiState {
    data class Content(val items: List<Item>, val userMessage: String? = null) : UiState
}

fun onMessageShown() {
    _uiState.update { it.copy(userMessage = null) }
}""",
            optionsJson = "StateFlow maintains a current value with replay=1 and conflation; SharedFlow has configurable replay. For events, state-based models avoid missed or duplicated emissions|||StateFlow is cold and SharedFlow is hot|||StateFlow runs only on Main thread; SharedFlow runs on IO|||StateFlow cannot be collected inside Jetpack Compose",
            correctOptionIndex = 0,
            answerSummary = "StateFlow is a hot flow that represents state: it requires an initial value, always holds a current value (`.value`), replays the latest value to new collectors, and conflates consecutive identical values. SharedFlow is a generic broadcast flow without an initial value. For UI events, modeling them directly in UI State (and resetting them after handling) guarantees events survive process death and configuration changes without being dropped.",
            deepDiveExplanation = "When Compose uses `repeatOnLifecycle` or `collectAsStateWithLifecycle`, collectors cancel when the activity stops. If a `SharedFlow` with `replay = 0` emits while the app is paused (e.g., during a screen transition), the event is dropped forever. Storing one-off events as nullable state fields ensures the UI displays the event as soon as it becomes visible, then clears it via a completion callback.",
            followUpInterviewerQuestion = "How does `Channel.receiveAsFlow()` behave differently from `MutableSharedFlow` for single-observer events?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 14,
            category = Category.ANDROID_ARCHITECTURE.id,
            title = "Compose Recomposition Optimization & Stability",
            questionText = "Why do standard Kotlin collection interfaces (like `List<T>`) cause unexpected recomposition in Jetpack Compose, and how do you ensure composables remain skippable?",
            scenarioContext = "In Compose Layout Inspector, a LazyColumn row recomposes on every keystroke in a search bar even though its item data has not changed.",
            codeSnippet = """// ⚠️ Unstable: List<User> is considered unstable by Compose compiler
@Composable
fun UserList(users: List<User>, onUserClick: (User) -> Unit) { ... }

// ✅ Fix Option 1: Immutable Collections library
@Composable
fun UserList(users: PersistentList<User>, onUserClick: (User) -> Unit) { ... }

// ✅ Fix Option 2: Stable wrapper class
@Immutable
data class UserListState(val users: List<User>)

// ✅ Fix Option 3: Remember lambda or method reference
val onUserClick = remember { { user: User -> viewModel.onSelect(user) } }""",
            optionsJson = "List is an interface that cannot guarantee immutability; Compose marks it unstable and re-evaluates the composable. Use @Immutable or PersistentList|||List cannot be parsed by Android ART compiler|||Compose requires ArrayList instead of standard List|||Compose compiler only supports primitives (Int, String)",
            correctOptionIndex = 0,
            answerSummary = "The Compose compiler cannot verify if a standard `List<T>` is immutable (it could be an upcast `MutableList`). Therefore, parameters typed as `List<T>` are treated as unstable, preventing Compose from skipping recomposition even if list contents did not change. Mark container models with `@Immutable` or `@Stable`, or use Kotlinx Immutable Collections (`PersistentList`).",
            deepDiveExplanation = "A composable is 'skippable' if all its parameters are stable. If even one parameter is unstable (or an unstable lambda is recreated during recomposition), Compose is forced to invoke the Composable body on every parent recomposition. With the Compose Compiler Metrics report, you can inspect `classes.txt` and `composables.txt` to verify stability.",
            followUpInterviewerQuestion = "How does the Compose Strong Skipping mode introduced in recent Compose compiler releases alter default stability rules?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 15,
            category = Category.ANDROID_ARCHITECTURE.id,
            title = "Clean Architecture: Domain Purity & Repository Segregation",
            questionText = "In Clean Architecture, why must the Domain layer have ZERO dependencies on the Android framework, and how does this affect testing and business rule longevity?",
            scenarioContext = "A developer imports `android.content.Context` and `android.os.Bundle` inside a domain UseCase to resolve strings and manage intent data.",
            codeSnippet = """// ❌ Violating Domain Purity:
import android.content.Context // BAD!

class CalculateDiscountUseCase(private val context: Context) {
    fun execute(price: Double): String {
        return context.getString(R.string.discount, price * 0.9)
    }
}

// ✅ Pure Domain UseCase (Pure Kotlin):
class CalculateDiscountUseCase {
    operator fun invoke(price: Double, rate: Double): DiscountResult {
        require(price >= 0) { "Price cannot be negative" }
        val finalAmount = price * (1.0 - rate)
        return DiscountResult(original = price, discounted = finalAmount)
    }
}""",
            optionsJson = "Domain purity ensures core business rules can run on plain JVM without Android SDK mocks or emulators, making tests 100x faster and code portable|||Android Studio refuses to build modules containing Context|||Domain layer only runs on backend servers|||Clean Architecture requires C++ for the domain layer",
            correctOptionIndex = 0,
            answerSummary = "The domain layer encapsulates enterprise business logic and use cases. By keeping it free of Android framework dependencies (`Context`, `Activity`, `Bitmap`), you can run all domain unit tests instantly on the local JVM without Robolectric or emulator overhead, and protect business rules when framework APIs evolve.",
            deepDiveExplanation = "If the domain layer references Android classes, the system becomes tightly coupled to Android OS versions. Instead, abstract platform capabilities behind domain interfaces (e.g. `StringProvider`, `LocationService`) and implement them in the Data or Platform layer, inverted via Dependency Injection.",
            followUpInterviewerQuestion = "Should a UseCase always be created for every single repository query, or is direct repository access acceptable in simple ViewModels?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 16,
            category = Category.ANDROID_ARCHITECTURE.id,
            title = "Coroutines Structured Concurrency: coroutineScope vs supervisorScope",
            questionText = "What happens to sibling coroutines when one child coroutine throws an unhandled exception inside `coroutineScope` versus `supervisorScope`?",
            scenarioContext = "An app launches two concurrent tasks: (1) loading vital cached user details, (2) fetching optional marketing banners. The marketing banner request throws an IOException.",
            codeSnippet = """// Scenario A: coroutineScope
coroutineScope {
    val userDeferred = async { fetchUser() }
    val bannerDeferred = async { throw IOException("Banner failed") }
    // 💥 The IOException cancels coroutineScope, cancelling fetchUser() immediately!
}

// Scenario B: supervisorScope
supervisorScope {
    val userDeferred = async { fetchUser() }
    val bannerDeferred = async { 
        try { fetchBanner() } catch(e: Exception) { null } 
    }
    // ✅ fetchUser() completes successfully despite banner failure!
}""",
            optionsJson = "coroutineScope cancels all sibling coroutines when one fails; supervisorScope isolates child failures so sibling coroutines continue|||supervisorScope throws an error if any child uses async|||coroutineScope automatically retries failed coroutines 3 times|||Both scopes behave identically in Kotlin 2.0+",
            correctOptionIndex = 0,
            answerSummary = "`coroutineScope` follows standard structured concurrency: if any child fails, it immediately cancels all sibling children and rethrows the failure. `supervisorScope` overrides this propagation: a failure in one child does not cancel other children, allowing independent parallel operations to succeed even if one fails.",
            deepDiveExplanation = "Inside a `SupervisorJob` or `supervisorScope`, each child becomes responsible for handling its own exceptions. If a child launched via `async` fails inside `supervisorScope`, the failure is deferred until `.await()` is called, preventing unnecessary cancellation of vital sibling tasks like user profiles or database writes.",
            followUpInterviewerQuestion = "Where in the coroutine hierarchy should a `CoroutineExceptionHandler` be installed for it to take effect?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 17,
            category = Category.ANDROID_ARCHITECTURE.id,
            title = "Testing ViewModels & StateFlow with StandardTestDispatcher",
            questionText = "When testing a ViewModel using Kotlin Coroutines and StateFlow in JUnit tests, why is `advanceUntilIdle()` or `UnconfinedTestDispatcher` necessary, and how do you mock the Main dispatcher?",
            scenarioContext = "A unit test fails intermittently with `IllegalStateException: Module with the Main dispatcher had failed to initialize` and state assertions execute before coroutines finish.",
            codeSnippet = """class UserViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set Main dispatcher to test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_updatesStateToSuccess() = runTest(testDispatcher) {
        val viewModel = UserViewModel(fakeRepository)
        viewModel.loadUsers()

        // Fast-forward virtual time until all launched coroutines complete:
        advanceUntilIdle()

        assertEquals(UiState.Success(expectedUsers), viewModel.uiState.value)
    }
}""",
            optionsJson = "Unit tests run on JVM without Android Looper; Dispatchers.setMain replaces Android Main thread with a controllable test scheduler|||StandardTestDispatcher connects tests directly to Android Emulator ADB|||Testing StateFlow requires running the tests in Robolectric only|||advanceUntilIdle sleeps the JVM thread for 5 seconds",
            correctOptionIndex = 0,
            answerSummary = "Because standard JUnit unit tests run on the host JVM where Android's `Looper.getMainLooper()` is unavailable, calling `Dispatchers.Main` throws an exception. `Dispatchers.setMain(testDispatcher)` replaces it with a test scheduler. `advanceUntilIdle()` fast-forwards virtual time so all pending coroutine tasks execute synchronously before assertions.",
            deepDiveExplanation = "`runTest` from `kotlinx-coroutines-test` controls virtual time. `StandardTestDispatcher` queues tasks without executing them immediately until triggered (`runCurrent()`, `advanceUntilIdle()`), enabling deterministic testing of loading spinners, timeouts, and sequential emissions without flaky `Thread.sleep` calls.",
            followUpInterviewerQuestion = "What is the difference between `StandardTestDispatcher` and `UnconfinedTestDispatcher` in coroutine unit testing?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 18,
            category = Category.ROOM_PRODUCTION.id,
            title = "Room Database TypeConverters for Complex Objects",
            questionText = "What is the recommended approach in Room when storing complex non-primitive objects (such as lists or nested configurations) versus normalized relational tables?",
            scenarioContext = "An entity contains `val tags: List<String>` and `val metadata: Map<String, String>`.",
            codeSnippet = """// TypeConverters for Room
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString("|||")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split("|||")?.filter { it.isNotEmpty() }
    }
}

// In AppDatabase:
@Database(entities = [QuestionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()""",
            optionsJson = "Use TypeConverters for simple scalar lists/primitives without query requirements; use normalized tables if items need indexing, searching, or relational joins|||Always store everything as JSON strings regardless of query requirements|||Room does not allow TypeConverters in version 2.6+|||TypeConverters must only be written in Java",
            correctOptionIndex = 0,
            answerSummary = "Use `@TypeConverter` to serialize small, self-contained collections (like list of string tags or enums) into SQLite primitives (TEXT or INTEGER) when you do not need to query, filter, or join against individual elements. If you need to search or perform relational queries on those elements, create a separate normalized Entity with foreign keys.",
            deepDiveExplanation = "Relying on TypeConverters to store deeply nested domain models into giant JSON blobs undermines SQLite's indexing and query power. Furthermore, schema evolution inside JSON strings is unmanaged by Room migrations, leading to runtime parsing crashes when models change.",
            followUpInterviewerQuestion = "Can a Room `@TypeConverter` be provided as an instantiated class via Dependency Injection?",
            difficulty = Difficulty.EASY.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        ),
        QuestionEntity(
            id = 19,
            category = Category.REACT_NATIVE_SECURITY.id,
            title = "Biometric Authentication with Hardware-Backed Crypto",
            questionText = "How do you securely implement biometric authentication in mobile apps so that physical access cannot be spoofed by overriding a boolean flag in memory?",
            scenarioContext = "A jailbroken device uses Frida or Cycript to hook the JS bridge: `Biometrics.authenticate().then(() => return true)`.",
            codeSnippet = """// ❌ Vulnerable to hook / bypass:
const isSuccess = await LocalAuthentication.authenticateAsync();
if (isSuccess) {
  // Directly loads auth token from memory or sends API request!
  navigateToDashboard();
}

// ✅ Hardware-Secured Pattern:
// 1. Generate an RSA/EC keypair in Secure Enclave / TEE:
// Key is configured with: setUserAuthenticationRequired(true)
// 2. To authorize payment/transfer, ask Secure Enclave to SIGN a server challenge nonce:
const signature = await BiometricKeystore.signPayload(serverNonce);
// 3. Server validates signature with previously enrolled public key.
// Bypassing client UI does NOT yield a valid cryptographic signature!""",
            optionsJson = "Use biometrics to unlock a cryptographic private key stored in the hardware Secure Enclave/Keystore to sign a server nonce, rather than trusting a client boolean|||Rely on fingerprint scanning without server-side validation|||Store the user password in plaintext inside the biometric callback|||Biometric spoofing is impossible so a boolean is completely sufficient",
            correctOptionIndex = 0,
            answerSummary = "Never rely on a simple boolean `authenticated == true` check returned from a biometric prompt. Attackers can easily hook method returns using tools like Frida. True security requires hardware-backed keys configured to require biometric authorization before decrypting the session token or signing a server-issued challenge nonce.",
            deepDiveExplanation = "On Android, `KeyGenParameterSpec.Builder.setUserAuthenticationRequired(true)` binds the cryptographic key in Android Keystore to the device's biometric master key. If biometrics are not authenticated, any call to `cipher.doFinal()` fails with `UserNotAuthenticatedException` at the hardware kernel level.",
            followUpInterviewerQuestion = "What security consideration arises when a user enrolls a new fingerprint on an existing enrolled device?",
            difficulty = Difficulty.HARD.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.FLASHCARD.name
        ),
        QuestionEntity(
            id = 20,
            category = Category.IOS_SWIFT_CONCURRENCY.id,
            title = "Swift Task Cancellation & Cooperative Concurrency",
            questionText = "Why is Task cancellation in Swift considered 'cooperative', and what code must a long-running background loop call to avoid wasting CPU cycles?",
            scenarioContext = "A user dismisses an image processing screen, but the background task continues churning CPU and memory for 30 seconds.",
            codeSnippet = """func processImages(items: [UIImage]) async throws -> [ProcessedImage] {
    var results: [ProcessedImage] = []
    
    for item in items {
        // ✅ Check if task was cancelled before heavy work:
        try Task.checkCancellation()
        
        // Or conditionally inspect:
        if Task.isCancelled { break }
        
        let processed = await heavyFilter(item)
        results.append(processed)
    }
    return results
}""",
            optionsJson = "Tasks are not forcefully killed upon cancellation; running code must periodically call Task.checkCancellation() or inspect Task.isCancelled|||Task cancellation immediately halts the CPU instruction pointer|||Swift concurrency automatically terminates all loops upon task cancel|||Cancellation throws an uncaught fatal exception terminating the process",
            correctOptionIndex = 0,
            answerSummary = "Cancellation in Swift is cooperative: calling `task.cancel()` merely sets a cancellation flag on the Task. It does not preempt or abruptly terminate execution. Long-running or loop-based tasks must cooperatively check `Task.checkCancellation()` (which throws `CancellationError`) or `Task.isCancelled` to exit cleanly.",
            deepDiveExplanation = "This prevents resource leaks and leaves shared state in a consistent condition. If an async function calls built-in cancellation-aware APIs (like `Task.sleep` or `URLSession`), those APIs automatically throw `CancellationError` when cancelled, bubbling cancellation up the call stack.",
            followUpInterviewerQuestion = "How does `withTaskCancellationHandler` allow legacy non-async cancellation mechanisms to integrate with Swift Tasks?",
            difficulty = Difficulty.MEDIUM.name,
            isBookmarked = false,
            isMastered = false,
            questionType = QuestionType.MCQ.name
        )
    )
}
