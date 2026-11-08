plugins { java }
java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }
repositories { mavenCentral() }
dependencies {
 implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
 implementation("com.amazonaws:aws-lambda-java-events:3.16.1")
 implementation(platform("software.amazon.awssdk:bom:2.30.38"))
 implementation("software.amazon.awssdk:dynamodb")
 implementation("software.amazon.awssdk:sfn")
}
tasks.register<Zip>("lambdaZip") {
 dependsOn("jar")
 into("lib") {
  from(tasks.jar)
  from(configurations.runtimeClasspath)
 }
 archiveFileName.set("lambdas.zip")
 destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}
