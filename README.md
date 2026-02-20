# Policy Management Service

Este microsserviço é responsável pelo ciclo de vida de apólices de seguro, focado em alta volumetria e performance. O projeto utiliza **Arquitetura Hexagonal** para garantir o desacoplamento de infraestrutura e facilitar a testabilidade.

---

## 🛠 1. Estrutura do Projeto

A solução foi organizada para refletir maturidade arquitetural e separação de responsabilidades:

* **`domain`**: Entidades de negócio (`Policy`), exceções e interfaces de saída (**Ports**). Livre de frameworks.
* **`application`**: Casos de uso (`Use Cases`) que implementam as regras de negócio descritas no desafio.
* **`infra`**: Adaptadores de entrada (REST Controllers) e saída (JPA, Redis, Keycloak).
* **`config`**: Configurações de Beans do Spring, Segurança e Cache.
* **`k8s`**: Manifestos Kubernetes organizados por **Kustomize** (base e overlays).



[Image of Hexagonal Architecture diagram]


---

## 🚀 2. Como Subir o Projeto

### Pré-requisitos
* Docker e Docker Compose
* Java 21 e Maven 3.9+

### Passo 1: Infraestrutura (Docker Compose)
Este comando inicia o PostgreSQL, Redis e Keycloak:
```bash
docker-compose up -d
```

## Passo 2: Configuração do IAM (Keycloak)

Para validar os endpoints protegidos, configure o **Keycloak** disponível em:
* URL: `http://localhost:8080`


### 🔐 Configurações necessárias

1. **Criar o Realm**
    - Nome do Realm: `app-policy-management-realm`

2. **Criar as Roles**
    - `ADMIN`
    - `MANAGER`
    - `USER`

3. **Criar um Usuário**
    - Acesse a seção **Users**
    - Crie um novo usuário

4. **Atribuir Role ao Usuário**
    - Abra o usuário criado
    - Vá até a aba **Role Mapping**
    - Atribua a role:
        - `ADMIN`

5. **Definir Senha**
    - Acesse a aba **Credentials**
    - Defina uma senha
    - ❗ Desmarque a opção **Temporary**

## Passo 3: Configuração Local (IDE)

Se preferir executar a aplicação localmente (**fora do Docker**), crie o arquivo:


com o seguinte conteúdo:

```properties
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/db_policy_manager_service
spring.datasource.username=user_policy_manager_service
spring.datasource.password=sua_senha_aqui
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.flyway.locations=classpath:db/migration

# Security (JWT Keycloak)
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://127.0.0.1:8080/realms/app-policy-management-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://127.0.0.1:8080/realms/app-policy-management-realm/protocol/openid-connect/certs

# Observability
management.datadog.metrics.export.apiKey=seu_token_datadog_aqui

# Cache (Redis)
spring.cache.type=redis
spring.cache.redis.time-to-live=24h
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.username=policy_user
spring.data.redis.password=sua_senha_redis_aqui
spring.data.redis.timeout=2s

# Logs
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=INFO
```

Execute via terminal:

```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 📬 3. Testando com Postman

Disponibilizei uma coleção pronta para testes na pasta:

/postman

### ▶️ Como utilizar

1. Abra o **Postman**
2. Clique em **Import**
3. Selecione os seguintes arquivos:

- `postman/postman_collection.json`  
  → Coleção contendo os endpoints da API

- `postman/local.postman_environment.json`  
  → Ambiente com URLs e variáveis locais

4. No canto superior direito do Postman, selecione o **environment**:
- `Local` (para ambiente local)

5. Execute primeiro a request de **Auth** para obter o **token JWT**.

> ⚠️ O token é necessário antes de chamar os endpoints de **Policy** protegidos.


## 📝 4. Respostas do Teste Técnico

### PARTE 2 — Análise de Incidente (Performance)

#### 1. Investigação

**Tracing (APM)**  
Utilizaria o **Datadog** para identificar o gargalo no fluxo, verificando se o problema está relacionado a:
- Latência de rede
- Lock de banco de dados
- Processamento síncrono

**Database**  
Analisaria o `pg_stat_activity` no **PostgreSQL** para identificar:
- Queries presas
- Falta de índices
- Sessões bloqueadas

---

#### 2. Hipóteses

- **Contenção de Lock**  
  A regra de *não permitir duplicidade* pode estar gerando locks em nível de linha sob alta concorrência.

- **Exaustão do Pool**  
  O pico de CPU combinado com aumento de queries sugere saturação do pool de conexões (**HikariCP**).

---

#### 3. Mitigação Rápida

- **Auto-scaling**  
  Incrementar o número de réplicas utilizando **HPA (Horizontal Pod Autoscaler)** no Kubernetes.

- **Kill Long Queries**  
  Identificar e encerrar queries que excedam o tempo esperado de execução no banco.

---

#### 4. Melhorias Estruturais

- **Arquitetura Orientada a Eventos**  
  Processar o registro de apólices de forma assíncrona utilizando filas (*queue-based processing*).

- **Cache de Idempotência**  
  Utilizar **Redis** para validar duplicidade antes mesmo da persistência no banco relacional.

### PARTE 3 — Perguntas de Arquitetura

#### 🧠 Cache (Redis)
Utilizado para:
- **Warmup** dos tipos de apólice (`PolicyType`)
- Evitar consultas repetitivas a dados que mudam com baixa frequência
- Reduzir o **I/O** no banco de dados e melhorar a performance

---

#### ☁️ Alta Disponibilidade (AWS)
Estratégia baseada em:
- Deploy em **múltiplas Availability Zones (AZs)**
- Utilização do **Amazon RDS Multi-AZ**
- Configuração de **Application Load Balancer (ALB)** para distribuição do tráfego

---

#### 🔄 Versionamento de API
Versionamento realizado via URL:
- Exemplo: `/api/v1/policies`


Garantindo:
- Compatibilidade com clientes antigos
- Evolução segura da API

---

#### ⚙️ Pipeline Jenkins

O pipeline de CI/CD está definido no arquivo **`Jenkinsfile`** localizado na raiz do projeto.

📄 **Verifique o arquivo:**
O pipeline está estruturado nos seguintes estágios:

**Descrição dos estágios:**

- **Build**
    - Compilação do projeto
    - Resolução de dependências Maven

- **Unit Tests**
    - Execução dos testes unitários com **JUnit 5** e **Mockito**
    - Validação das regras de negócio

- **SonarQube**
    - Análise estática de código
    - Verificação de qualidade, cobertura e vulnerabilidades

- **Docker Build**
    - Geração da imagem Docker da aplicação
    - Versionamento da imagem para deploy

- **Kustomize Deploy**
    - Aplicação dos manifests Kubernetes
    - Deploy automatizado no cluster


## 🛠 Tecnologias Utilizadas

### 🚀 Backend
- **Java 21**
- **Spring Boot 4**

---

### 🗄️ Persistência de Dados
- **PostgreSQL** — banco relacional principal
- **Flyway** — versionamento e migração de banco de dados

---

### ⚡ Cache e Performance
- **Redis**
    - Cache de dados frequentemente acessados
    - Warmup de informações críticas (ex: `PolicyType`)
    - Redução de acesso ao banco relacional

---

### 🔐 Segurança
- **Keycloak**
    - Autenticação via **OAuth2**
    - Autorização baseada em **JWT**
    - Controle de acesso por roles

---

### 📊 Observabilidade
- **Datadog**
    - Centralização de logs
    - Métricas de aplicação
    - Monitoramento e tracing (APM)

---

### 🧪 Testes Automatizados

#### ✅ Testes Unitários
- **JUnit 5**
- **Mockito**
    - Mock de dependências
    - Isolamento da camada de aplicação
    - Validação de regras de negócio sem dependência de infraestrutura

#### 🧪 Testes de Integração
- **Testcontainers**
    - Execução de bancos reais em containers Docker durante os testes
    - PostgreSQL e Redis simulando ambiente produtivo
    - Garantia de comportamento real da aplicação
    - Testes reproduzíveis e independentes do ambiente do desenvolvedor