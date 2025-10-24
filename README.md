# Product Manager - Sistema Completo de Gerenciamento

Sistema completo de gerenciamento de produtos desenvolvido com Spring Boot seguindo os princípios de Clean Architecture, com interface web moderna em Vaadin, autenticação, monitoramento, testes unitários e inteligência artificial integrada.

**Desafio Técnico - Dev Jr - PARTNER Pro**

## 📸 Screenshots da Aplicação

### Tela de Login
Interface de autenticação desenvolvida com Vaadin e Spring Security

<img width="1869" height="926" alt="image" src="https://github.com/user-attachments/assets/fc1f91d3-7256-4bef-8078-dd73c79f9497" />

### Tela de Registro
Cadastro de novos usuários com validação

<img width="1866" height="919" alt="image" src="https://github.com/user-attachments/assets/a6d99041-8f0b-4434-9b8f-2de28a87159a" />

### Gerenciamento de Produtos
CRUD completo com filtros avançados e validação

<img width="1865" height="928" alt="image" src="https://github.com/user-attachments/assets/8a622212-eb28-4834-a26c-34c5223cd90e" />

<img width="1867" height="919" alt="image" src="https://github.com/user-attachments/assets/b87205b7-fe89-433c-8dd7-e474936efd85" />


### Dashboard Interativo
Dashboard com gráficos e estatísticas em tempo real

<img width="1862" height="920" alt="image" src="https://github.com/user-attachments/assets/d144f6f3-8b23-4f86-9e21-05103af96d80" />

### Assistente AI
Chat inteligente para análise de dados e geração de relatórios

<img width="1862" height="913" alt="image" src="https://github.com/user-attachments/assets/b4110178-8346-4f28-acc0-f44a508a4492" />

### Monitoramento com Grafana
Dashboards de monitoramento com métricas em tempo real usando Prometheus e Grafana

<img width="1874" height="914" alt="image" src="https://github.com/user-attachments/assets/a496e721-9ea4-4783-b227-d74e11ca92c1" />

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com separação clara de responsabilidades:

```
src/main/java/br/com/partnerpro/product_manager/
├── domain/                    # Camada de Domínio
│   ├── entity/               # Entidades de negócio
│   │   ├── Product.java
│   │   └── User.java
│   ├── repository/           # Interfaces de repositório
│   │   ├── ProductRepository.java
│   │   └── UserRepository.java
│   ├── model/                # Modelos de domínio
│   └── specification/        # Especificações para queries
├── application/              # Camada de Aplicação
│   ├── usecase/             # Casos de uso
│   │   ├── ProductUseCase.java
│   │   └── DashboardUseCase.java
│   └── service/             # Serviços de aplicação
│       ├── AIAssistantService.java
│       ├── AIReportService.java
│       ├── ChartGeneratorService.java
│       ├── ExportService.java
│       └── SessionManager.java
├── framework/                # Camada de Framework
│   ├── controller/          # Controllers REST
│   │   ├── ProductController.java
│   │   ├── DashboardController.java
│   │   └── AIReportController.java
│   └── dto/                 # Data Transfer Objects
├── ui/                       # Interface Vaadin
│   ├── views/               # Views da aplicação
│   │   ├── LoginView.java
│   │   ├── RegisterView.java
│   │   ├── ProductsView.java
│   │   ├── DashboardView.java
│   │   └── AIChatView.java
│   └── MainLayout.java      # Layout principal
├── config/                   # Configurações
│   ├── SecurityConfig.java
│   ├── CacheConfig.java
│   └── AIConfig.java
└── ProductManagerApplication.java
```


## 🚀 Funcionalidades Principais

### 🔐 Autenticação e Segurança
- Login e registro de usuários
- Autenticação com Spring Security
- Senhas criptografadas com BCrypt
- Sessões gerenciadas com segurança
- Proteção de rotas e endpoints

### 📦 Gerenciamento de Produtos
- CRUD completo de produtos
- Filtros avançados (nome, categoria, preço, estoque, data)
- Ordenação por múltiplos critérios
- Validação de formulários
- Exportação em CSV e PDF
- Cache inteligente para performance

### 📊 Dashboard Interativo
- Estatísticas em tempo real
- Gráficos de produtos por categoria
- Distribuição de preços
- Níveis de estoque
- Valor total por categoria
- Produtos recentes
- Preço médio por categoria

### 🤖 Assistente AI (Spring AI + OpenAI)
- Chat inteligente para análise de dados
- Geração automática de relatórios
- Análise de tendências
- Recomendações baseadas em dados
- Geração de gráficos dinâmicos
- Persistência de conversas

### 📈 Monitoramento e Observabilidade
- Spring Boot Actuator para métricas
- Prometheus para coleta de dados
- Grafana para visualização
- Dashboards customizados
- Alertas configuráveis

### 📤 Exportação de Dados
- Exportação em CSV
- Exportação em PDF com formatação
- Filtros aplicados nas exportações

## 🔧 Tecnologias Utilizadas

### Backend
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistência de dados com Hibernate
- **Spring Security** - Autenticação e autorização
- **Spring Cache + Caffeine** - Cache em memória
- **Spring Boot Actuator** - Métricas e health checks
- **Spring AI** - Integração com OpenAI GPT-4
- **PostgreSQL** - Banco de dados relacional
- **Flyway** - Migração e versionamento de banco
- **Lombok** - Redução de boilerplate

### Frontend
- **Vaadin 24.9** - Framework web Java para UI moderna
- **Vaadin Flow** - Componentes reativos
- **Vaadin Charts** - Gráficos interativos (via código)

### Monitoramento
- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização de dashboards
- **Micrometer** - Métricas da aplicação

### Exportação
- **Apache Commons CSV** - Geração de arquivos CSV
- **iText PDF** - Geração de arquivos PDF

### Testes Unitários
- **JUnit 5** - Framework de testes moderno
- **Mockito** - Mocks e stubs para isolamento
- **Spring Boot Test** - Testes de integração
- **AssertJ** - Assertions fluentes e legíveis

### Qualidade de Código
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks e stubs
- **Spring Boot Test** - Testes de integração

### Documentação
- **SpringDoc OpenAPI** - Documentação automática da API
- **Swagger UI** - Interface interativa para testar endpoints

### DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers
- **Maven** - Gerenciamento de dependências

## ⚡ Cache e Performance

Implementei cache na listagem de produtos utilizando **Spring Cache com Caffeine**, para otimizar a performance em consultas repetidas. O cache é limpo automaticamente quando há alterações nos produtos.

**Configuração de Cache:**
```properties
spring.cache.type=caffeine
spring.cache.cache-names=products,dashboard
spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=300s
```

**Estratégia Implementada:**
- `@Cacheable("products")` - Lista de produtos em cache por 5 minutos
- `@Cacheable("dashboard")` - Estatísticas do dashboard em cache por 5 minutos
- `@CacheEvict` - Cache limpo automaticamente em operações de criar/atualizar/deletar

**Benefícios:**
- ✅ Redução de até 80% no tempo de resposta
- ✅ Menor carga no banco de dados
- ✅ Melhor experiência do usuário
- ✅ Escalabilidade aprimorada

## 📋 Requisitos

- **Java 17+**
- **PostgreSQL 12+** (instalado localmente)
- **Maven 3.8+**
- **Docker Desktop** (obrigatório para Prometheus e Grafana)
- **Chave API OpenAI** (para funcionalidades de AI)

## ⚙️ Configuração e Execução

### 1. Instale o PostgreSQL Localmente

Baixe e instale o PostgreSQL:
- **Windows**: https://www.postgresql.org/download/windows/
- **macOS**: `brew install postgresql`
- **Linux**: `sudo apt-get install postgresql`

Crie o banco de dados:
```sql
CREATE DATABASE "product-manager-db";
```

### 2. Instale o Docker Desktop

O Docker Desktop é **obrigatório** para executar Prometheus e Grafana:

- **Windows/macOS**: https://www.docker.com/products/docker-desktop/
- **Linux**: https://docs.docker.com/desktop/install/linux-install/

Após instalar, certifique-se de que o Docker Desktop está rodando.

### 3. Clone o Repositório

```bash
git clone <repository-url>
cd Teste_Partner_Pro
cd product-manager
```

### 4. Configure as Variáveis de Ambiente

Edite o arquivo `src/main/resources/application.properties`:

```properties
# OpenAI API Key (obrigatório para AI)
spring.ai.openai.api-key=sua-chave-aqui

# Database Local (ajuste usuário e senha conforme sua instalação)
spring.datasource.url=jdbc:postgresql://localhost:5432/product-manager-db
spring.datasource.username=postgres
spring.datasource.password=1234
```

### 5. Inicie os Serviços de Monitoramento com Docker

```bash
cd product-manager
docker-compose up -d
```

Isso iniciará:
- ✅ Prometheus na porta 9090
- ✅ Grafana na porta 3000

**Nota**: O PostgreSQL **não** está no Docker, ele deve estar instalado e rodando localmente.

### 6. Execute a Aplicação

```bash
./mvnw spring-boot:run
```

Ou no Windows:
```bash
.\mvnw.cmd spring-boot:run
```

### 5. Acesse a Aplicação

- **Aplicação Web**: http://localhost:8080
- **API REST**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 🧪 Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes com cobertura
./mvnw test jacoco:report

# Apenas testes unitários
./mvnw test -Dtest=*Test

# Apenas testes de integração
./mvnw test -Dtest=*IT
```

## �  Documentação da API (Swagger)

A API está completamente documentada com **Swagger/OpenAPI 3.0**, permitindo visualizar e testar todos os endpoints de forma interativa.

### Acessar Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### Recursos do Swagger

- ✅ **Documentação Interativa** - Visualize todos os endpoints disponíveis
- ✅ **Teste de API** - Execute requisições diretamente pela interface
- ✅ **Schemas de Dados** - Veja a estrutura de todos os DTOs
- ✅ **Exemplos de Requisições** - Modelos prontos para usar
- ✅ **Códigos de Resposta** - Documentação completa de status HTTP
- ✅ **Filtros e Parâmetros** - Descrição detalhada de cada parâmetro

### OpenAPI JSON

Acesse a especificação OpenAPI em formato JSON:

```
http://localhost:8080/api-docs
```

## 📡 API REST Endpoints

### 📖 Documentação Interativa com Swagger

A API REST está completamente documentada com **Swagger/OpenAPI 3.0**, permitindo visualizar e testar todos os endpoints de forma interativa.

**Acesse o Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**Especificação OpenAPI (JSON):**
```
http://localhost:8080/api-docs
```

### ✨ Recursos do Swagger

- ✅ **Interface Interativa** - Teste todos os endpoints diretamente no navegador
- ✅ **Documentação Completa** - Descrição detalhada de cada endpoint
- ✅ **Schemas de Dados** - Visualize a estrutura de todos os DTOs
- ✅ **Exemplos de Requisições** - Modelos prontos para copiar e usar
- ✅ **Códigos de Resposta** - Documentação de todos os status HTTP
- ✅ **Validações** - Veja quais campos são obrigatórios
- ✅ **Try it Out** - Execute requisições reais e veja as respostas

### Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Lista todos os produtos (paginado) |
| GET | `/api/products/{id}` | Busca produto por ID |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto |
| DELETE | `/api/products/{id}` | Remove produto |
| POST | `/api/products/filter` | Filtra produtos com critérios avançados |
| GET | `/api/products/export/csv` | Exporta produtos em CSV |
| GET | `/api/products/export/pdf` | Exporta produtos em PDF |

### Dashboard

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/dashboard` | Estatísticas gerais do sistema |

### AI Reports

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/ai/report` | Gera relatório personalizado com AI |
| POST | `/api/ai/query` | Consulta dados com linguagem natural |
| POST | `/api/ai/chat` | Chat interativo com assistente AI |
| POST | `/api/ai/chat/clear` | Limpa histórico de conversas |

### Exemplos de Uso da API

#### Criar Produto
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook Dell Inspiron 15",
    "price": 3500.00,
    "category": "Eletrônicos",
    "stock": 10
  }'
```

#### Listar Produtos com Paginação
```bash
curl "http://localhost:8080/api/products?page=0&size=20&sort=createdAt,desc"
```

#### Filtrar Produtos
```bash
curl -X POST http://localhost:8080/api/products/filter \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook",
    "category": "Eletrônicos",
    "minPrice": 2000,
    "maxPrice": 5000,
    "minStock": 5
  }'
```

#### Exportar para PDF
```bash
curl -O http://localhost:8080/api/products/export/pdf
```

#### Gerar Relatório com AI
```bash
curl -X POST http://localhost:8080/api/ai/report \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Analise os produtos mais vendidos e sugira estratégias de estoque"
  }'
```

## 📦 Modelo de Dados

### Product
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "price": "decimal",
  "category": "string",
  "stock": "integer",
  "createdAt": "datetime"
}
```

### User
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "fullName": "string",
  "password": "string (encrypted)",
  "enabled": "boolean",
  "createdAt": "datetime"
}
```

## 🎯 Boas Práticas Implementadas

✅ **Clean Architecture** - Separação clara de camadas e responsabilidades  
✅ **SOLID Principles** - Código manutenível e extensível  
✅ **Dependency Injection** - Baixo acoplamento  
✅ **DTOs Imutáveis** - Uso de Records do Java  
✅ **Validação de Entrada** - Bean Validation em todos os endpoints  
✅ **Tratamento Global de Exceções** - Respostas consistentes  
✅ **Logging Estruturado** - Rastreabilidade completa  
✅ **Testes Automatizados** - Alta cobertura de código  
✅ **Migrations Versionadas** - Flyway para controle de schema  
✅ **Cache Inteligente** - Performance otimizada  
✅ **Segurança** - Autenticação e autorização robustas  
✅ **Monitoramento** - Observabilidade completa  
✅ **Documentação** - Código e APIs bem documentados  

## 📚 Documentação Adicional

- [ARCHITECTURE.md](ARCHITECTURE.md) - Detalhes da arquitetura
- [FEATURES.md](FEATURES.md) - Lista completa de funcionalidades
- [SETUP.md](SETUP.md) - Guia de instalação detalhado
- [MONITORING.md](MONITORING.md) - Configuração de monitoramento
- [AI_ASSISTANT_GUIDE.md](AI_ASSISTANT_GUIDE.md) - Guia do assistente AI
- [EXPORT_GUIDE.md](EXPORT_GUIDE.md) - Guia de exportação

## 🐳 Docker

### Pré-requisito: Docker Desktop

Antes de executar os serviços, certifique-se de ter o **Docker Desktop** instalado e rodando:

- **Download**: https://www.docker.com/products/docker-desktop/
- Após instalar, abra o Docker Desktop e aguarde até que esteja completamente iniciado

### Serviços Disponíveis no Docker

**Importante**: O PostgreSQL **NÃO** está no Docker. Ele deve estar instalado e rodando localmente na sua máquina.

Os serviços Docker são apenas para **monitoramento**:

```yaml
services:
  prometheus:
    image: prom/prometheus:v2.48.0
    ports:
      - "9090:9090"
    volumes:
      - ./config/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:10.2.2
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
```

### Comandos Úteis do Docker

```bash
# Iniciar serviços de monitoramento (Prometheus e Grafana)
docker-compose up -d

# Ver logs dos serviços
docker-compose logs -f

# Ver status dos containers
docker-compose ps

# Parar serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v

# Reiniciar um serviço específico
docker-compose restart prometheus
docker-compose restart grafana
```

### Verificar se o Docker Desktop está Rodando

**Windows/macOS**: Verifique se o ícone do Docker Desktop está ativo na barra de tarefas

**Linha de comando**:
```bash
docker --version
docker ps
```

Se aparecer erro, certifique-se de que o Docker Desktop está aberto e iniciado.

## 🔍 Monitoramento

### Métricas Disponíveis

- **JVM**: Uso de memória, threads, garbage collection
- **HTTP**: Requisições, latência, erros
- **Database**: Conexões, queries, transações
- **Cache**: Hits, misses, evictions
- **Custom**: Métricas de negócio

### Acessar Dashboards

1. **Prometheus**: http://localhost:9090
   - Visualize métricas brutas
   - Execute queries PromQL

2. **Grafana**: http://localhost:3000
   - Login: admin/admin
   - Dashboards pré-configurados
   - Alertas customizáveis

## 🧪 Testes Unitários

O projeto possui uma suíte completa de testes unitários cobrindo todas as camadas da aplicação.

### Cobertura de Testes

- **Use Cases**: Testes de lógica de negócio
  - `ProductUseCaseTest` - CRUD de produtos
  - `DashboardUseCaseTest` - Estatísticas e métricas

- **Services**: Testes de serviços
  - `ChartGeneratorServiceTest` - Geração de gráficos
  - `ExportServiceTest` - Exportação de dados

- **Entities**: Testes de entidades de domínio
  - `ProductTest` - Validações e comportamentos

- **DTOs**: Testes de objetos de transferência
  - `ChartDataTest` - Estruturas de dados
  - `ProductResponseTest` - Mapeamentos

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes com relatório de cobertura
./mvnw test jacoco:report

# Ver relatório de cobertura
open target/site/jacoco/index.html

# Testes específicos
./mvnw test -Dtest=ProductUseCaseTest
./mvnw test -Dtest=DashboardUseCaseTest

# Testes em modo watch (desenvolvimento)
./mvnw test -Dspring-boot.run.fork=false
```

### Estrutura dos Testes

```java
@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductUseCase productUseCase;
    
    @Test
    void shouldCreateProductSuccessfully() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
            "Notebook", "Dell Inspiron", 
            new BigDecimal("3500.00"), "Electronics", 10
        );
        
        // When
        ProductResponse response = productUseCase.createProduct(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Notebook");
        verify(productRepository).save(any(Product.class));
    }
}
```

### Boas Práticas de Teste

✅ **Arrange-Act-Assert** - Estrutura clara dos testes  
✅ **Mocks Isolados** - Testes independentes  
✅ **Nomes Descritivos** - Fácil identificação de falhas  
✅ **Cobertura Alta** - Código confiável  
✅ **Testes Rápidos** - Feedback imediato  
✅ **Assertions Fluentes** - Leitura natural com AssertJ

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto foi desenvolvido como desafio técnico para PARTNER Pro.

---

**Nota**: Para usar as funcionalidades de AI, você precisa de uma chave válida da OpenAI API. Configure em `application.properties`.
