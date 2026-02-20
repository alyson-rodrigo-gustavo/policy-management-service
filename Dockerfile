# 1. Imagem base extremamente leve contendo apenas o JRE do Java 21 (Alpine Linux)
FROM eclipse-temurin:21-jre-alpine

# 2. Informações de manutenção (Opcional, mas boa prática)
LABEL maintainer="Alyson Gustavo <contato@cookiesoft.com.br>"
LABEL version="1.0"
LABEL description="Policy Management Service"

# 3. Define o diretório de trabalho padrão dentro do container
WORKDIR /app

# 4. SEGURANÇA: Cria um grupo e um usuário não-root.
# Rodar containers como root no Kubernetes (EKS) é uma falha crítica de segurança.
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
USER springuser:springgroup

# 5. Define um argumento apontando para o JAR gerado pelo Maven no Jenkins
ARG JAR_FILE=target/*.jar

# 6. Copia o JAR do host (Jenkins workspace) para o diretório /app do container
COPY ${JAR_FILE} app.jar

# 7. Documenta a porta que a aplicação vai expor (facilita a leitura e configuração do K8s)
EXPOSE 8080

# 8. Ponto de entrada otimizado para ambientes conteinerizados (Cgroups)
# -XX:MaxRAMPercentage=75.0 : Garante que a JVM respeite os limites de memória (requests/limits) do Kubernetes.
# -XX:+UseZGC : O Java 21 possui o ZGC geracional, excelente para microsserviços de baixa latência (opcional, mas recomendado).
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseZGC", "-jar", "/app/app.jar"]