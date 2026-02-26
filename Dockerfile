# =========================================================================
# STEP 1: BUILDER
# =========================================================================
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /build
RUN mkdir -p logs
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================================
# STEP 2: RUNNER (SLIM & DEBUGGABLE)
# Using a slim image that supports apt-get to install curl/tools
# =========================================================================
FROM eclipse-temurin:17-jre

# 1. Install dependencies as ROOT
#    We combine update, install, and cleanup in one layer to save size.
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl iputils-ping telnet && \
    rm -rf /var/lib/apt/lists/*

ENV TZ=Asia/Jakarta
WORKDIR /app

# 2. Setup User Non-Root (For security best practices)
#    Create group and user 'appuser'
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy logs & jar (Note --chown so non-root users can access)
COPY --from=builder --chown=appuser:appgroup /build/logs ./logs
COPY --from=builder --chown=appuser:appgroup /build/target/demandlane-booklending-*.jar app.jar

EXPOSE 9090

# 3. Switch to Non-Root User before running the application
USER appuser

CMD ["java", "-jar", "app.jar"]