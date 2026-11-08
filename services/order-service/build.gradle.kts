plugins { java; id("org.springframework.boot") version "3.5.6"; id("io.spring.dependency-management") version "1.1.7" }
group = "com.example"; version = "0.2.0"
java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }
repositories { mavenCentral() }
dependencies {
 implementation("org.springframework.boot:spring-boot-starter-webflux")
 implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
 implementation("org.springframework.boot:spring-boot-starter-validation")
 implementation("org.springframework.boot:spring-boot-starter-actuator")
 implementation("org.postgresql:r2dbc-postgresql")
 implementation("software.amazon.awssdk:sfn:2.30.38")
 runtimeOnly("org.postgresql:postgresql")
 testImplementation("org.springframework.boot:spring-boot-starter-test")
 testImplementation("io.projectreactor:reactor-test")
 testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.test { useJUnitPlatform() }
