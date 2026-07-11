# Mini Blog — Spring Cloud microservices

A learning project: a **posts + comments** app split into independent Spring Boot services behind an API gateway, coordinated by a Eureka registry. A single MySQL container hosts one schema per service. An OpenAPI 3.0 spec for the public API lives at [`openapi.yaml`](openapi.yaml).

## Repository layout

| Path | What it is |
|---|---|
| `microservices/` | The **active** system — four Spring Boot apps (see below) |
| `src/` | Original monolith (`com.example.miniblog`) — kept as reference only |
| `docker-compose.yml` | MySQL 8.4 container (`miniblog-mysql`, port `3306`) |
| `openapi.yaml` | OpenAPI 3.0 spec for the gateway-facing API |
| `microservices/README.md` | Long-form architecture doc with sequence diagrams |
| `CLAUDE.md` | Guidance for the Claude Code agent |

## Services

| Service | Port | Database | Role |
|---|---|---|---|
| `eureka-server` | 8761 | — | Service registry (dashboard at <http://localhost:8761>) |
| `api-gateway` | 8080 | — | Single entry point; `lb://` routing via Eureka |
| `post-service` | 8081 | `posts_db` | CRUD for posts + `/exists` check |
| `comment-service` | 8082 | `comments_db` | CRUD for comments; verifies posts via HTTP |

Clients only ever talk to `:8080`. `comment-service` calls `post-service` through a `@LoadBalanced RestClient` using the Eureka name `http://post-service` — no host/port is hardcoded.

## Stack

Spring Boot 3.4.1 · Spring Cloud 2024.0.0 (Gateway, Eureka, LoadBalancer) · Java 21 · Spring Data JPA · MySQL 8.4 · Maven. No Lombok in `microservices/`.

## Run

Prereqs: Java 21+, Maven, Docker.

```bash
# 1. Start MySQL
docker compose up -d

# 2. First-time only — create the two schemas
docker exec miniblog-mysql mysql -uroot -prootpass -e \
  "CREATE DATABASE IF NOT EXISTS posts_db;
   CREATE DATABASE IF NOT EXISTS comments_db;
   GRANT ALL PRIVILEGES ON posts_db.*    TO 'miniblog'@'%';
   GRANT ALL PRIVILEGES ON comments_db.* TO 'miniblog'@'%';
   FLUSH PRIVILEGES;"

# 3. Start the apps (Eureka FIRST, then the rest — order matters)
cd microservices/eureka-server   && mvn spring-boot:run   # :8761
cd microservices/post-service    && mvn spring-boot:run   # :8081
cd microservices/comment-service && mvn spring-boot:run   # :8082
cd microservices/api-gateway     && mvn spring-boot:run   # :8080
```

The monolith at `src/` also binds `:8080` — do **not** run it alongside `api-gateway`.

### Environment overrides

`DB_HOST` · `DB_PORT` · `DB_NAME` · `DB_USER` · `DB_PASSWORD` · `EUREKA_URL` (default `http://localhost:8761/eureka/`).

## API — quick reference

Full contract in [`openapi.yaml`](openapi.yaml). All requests go through the gateway.

### Posts — `/api/posts`
| Method | Path | Body |
|---|---|---|
| POST | `/api/posts` | `{title, content, author?}` |
| GET | `/api/posts` | — |
| GET | `/api/posts/{id}` | — |
| GET | `/api/posts/{id}/exists` | — (status only: 200 or 404) |
| PUT | `/api/posts/{id}` | `{title, content, author?}` |
| DELETE | `/api/posts/{id}` | — |

### Comments — `/api/comments`
| Method | Path | Body |
|---|---|---|
| POST | `/api/comments` | `{postId, content, author?}` (rejects if post missing) |
| GET | `/api/comments?postId={id}` | — |
| DELETE | `/api/comments/{id}` | — |

### Status codes
`200/201` success · `204` deleted · `400` validation error · `404` not found (incl. cross-service) · `503` `post-service` unreachable.

## Example

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello","content":"My first post","author":"alice"}'

curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -d '{"postId":1,"content":"Nice post!","author":"bob"}'

curl "http://localhost:8080/api/comments?postId=1"
```

See [`microservices/README.md`](microservices/README.md) for architecture diagrams, discovery flow, and troubleshooting.
