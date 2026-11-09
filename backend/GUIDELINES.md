# Spring Boot Guidelines

Spring Boot has a lot to learn at first, but this guideline covers the minimum knowledge you need for this project.

## Directory

```
backend/
├── gradle/                          // build tool wrapper (rarely touched)
├── src/                             // source code
│   ├── main/
│   │   ├── java/
│   │   │   └── com/puang/
│   │   │       ├── domain/
│   │   │       │   ├── user/
│   │   │       │   │   ├── controller/
│   │   │       │   │   ├── service/
│   │   │       │   │   ├── repository/
│   │   │       │   │   ├── entity/
│   │   │       │   │   └── dto/
│   │   │       │   └── game/
│   │   │       │       ├── controller/
│   │   │       │       ├── service/
│   │   │       │       ├── repository/
│   │   │       │       ├── entity/
│   │   │       │       └── dto/
│   │   │       ├── global/
│   │   │       │   ├── config/      // Spring configuration (Security, CORS, etc.)
│   │   │       │   ├── exception/   // custom exceptions and the global exception handler
│   │   │       │   ├── response/    // common API response format
│   │   │       │   └── util/        // small helpers shared by every domain
│   │   │       └── PuangApplication.java   // main function (entry point, nothing to do here)
│   │   └── resources/               // static files and configuration
│   │       └── application.yaml     // settings and values required to run the project
│   └── test/                        // test code (often drafted with AI, but always review it)
└── build.gradle                     // dependencies
```

In `domain/`, code is grouped by its main role and concern. For example, `user/` only has code related to user management, login, and so on, and `game/` only has code related to the game map and robot movement.

Every domain directory has the same structure:

- `controller`: receives requests from the app and returns responses. It does not contain any logic.
- `service`: processes all the core logic (e.g. calculates where a robot stops after sliding, checks whether a nickname is already taken).
- `repository`: saves and loads data from the database.
- `entity`: classes that map one-to-one to database tables (e.g. `User` ↔ `users` table). Only `service` and `repository` use them.
- `dto`: objects that carry request and response data between the app and the server (e.g. `SignUpRequest`, `UserResponse`).

Entities are never sent to the app directly. The service converts an entity into a DTO before returning it, so that internal fields (passwords, DB ids, etc.) are not exposed and the DB structure can change without breaking the app.

`global/` holds code that is not tied to a single domain and is shared by all of them.

So when you write new code, **deciding which directory it belongs to is important.** If you are not sure, ask in the team chat before you start.

## DI (Dependency Injection)

As mentioned above, every class has its own role and never takes over another class's role. Instead, if you need another class to process data, add a field of that class's type. Spring creates the object and puts it into the field for you. This is called dependency injection.

For example, `UserService` does not read the database itself. It declares a `UserRepository` field and asks it for the data.

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;   // injected by Spring

    public UserResponse signUp(SignUpRequest request) {
        if (userRepository.existsByNickname(request.nickname())) {
            throw new DuplicateNicknameException();
        }
        // ...
    }
}
```

Rules to follow:

- Declare dependencies as `private final` fields and add `@RequiredArgsConstructor` (Lombok) to the class. Do not use `@Autowired` on fields.
- Never create these classes yourself with `new UserService()`. Spring only injects objects that it created.
- Calls only go one way: `controller → service → repository`. A repository never calls a service, and a controller never calls a repository directly.

## Service

Most of the code you write will be in `service`. The controller and repository are thin layers around it, so you only need to know what they return (see [Return Types](#return-types)).

A service method usually does three things:

1. Gets entities from a repository.
2. Runs the actual logic (checks, calculations, changes).
3. Converts the result into a DTO and returns it.

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // returns one DTO
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)              // 1. get entity
                .orElseThrow(UserNotFoundException::new);    //    not found → throw
        return UserResponse.from(user);                      // 3. Entity → DTO
    }

    // returns a list of DTOs
    public List<RankingResponse> getTopRankings() {
        return userRepository.findTop10ByOrderByScoreDesc().stream()
                .map(RankingResponse::from)                  // Entity → DTO, one by one
                .toList();
    }
}
```

Things to keep in mind:

- **Return a DTO, never an entity.** Entities are only used inside the service and repository.
- **When something goes wrong, `throw` an exception.** Do not return `null` or an error code. The global exception handler in `global/exception/` turns it into an error response for the app.
- **A repository's single lookup returns `Optional`.** Decide what to do when it is empty, usually with `orElseThrow(...)`.
- **A service can use other services or repositories.** For example, `GameService` may need `UserRepository` to update a player's score. Just add another `private final` field.

## Repository

A repository is an interface. You do not write a class that implements it: extend `JpaRepository<Entity, IdType>` and Spring creates the implementation for you.

```java
public interface ItemRepository extends JpaRepository<Item, Long> {
}
```

### Built-in methods

Even an empty repository like the one above already has these methods:

| Method | Returns | What it does |
|---|---|---|
| `findById(id)` | `Optional<Entity>` | Finds one row by its id |
| `findAll()` | `List<Entity>` | Finds every row |
| `existsById(id)` | `boolean` | Checks whether a row with the id exists |
| `count()` | `long` | Counts the rows |
| `save(entity)` | `Entity` | Inserts a new row, or updates an existing one. Use the returned entity (it has the generated id) |
| `saveAll(entities)` | `List<Entity>` | `save` for many entities |
| `delete(entity)` / `deleteById(id)` | `void` | Deletes a row |

Inside a `@Transactional` service method, changes to an entity you loaded are saved automatically when the method ends, so you do not need to call `save` again after changing a field.

### Query methods

When you need a different lookup, add a method to the interface. Spring reads the method name and writes the SQL for you, so you only declare it:

```java
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);          // WHERE email = ?
    boolean existsByEmail(String email);               // is there a row WHERE email = ?
}

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByPosXAndPosY(int posX, int posY);  // WHERE pos_x = ? AND pos_y = ?
}

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findTop10ByOrderByScoreDesc();        // ORDER BY score DESC LIMIT 10
}
```

- The name starts with `findBy`, `existsBy`, `countBy` or `deleteBy`, followed by **field names of the entity** (Java names like `posX`, not column names like `pos_x`).
- Join conditions with `And` / `Or`. Other keywords you will often use: `Between`, `LessThan`, `GreaterThan`, `Containing`, `In`, `OrderBy...Asc/Desc`, `Top10`.
- Follow the [Return Types](#return-types) rule: `Optional<Entity>` for one row, `List<Entity>` for many.
- If the name gets too long or the query is complicated, ask in the team chat before writing a `@Query`.

## Return Types

Each layer returns a fixed type, so every API responds in the same format.

| Layer | Returns | Example |
|---|---|---|
| `controller` | `ApiResponse<DTO>` | `ApiResponse<UserResponse>` |
| `service` | `DTO`, `List<DTO>`, ... | `UserResponse`, `List<RankingResponse>` |
| `repository` | `Optional<Entity>` (single), `List<Entity>` (many), ... | `Optional<User>`, `List<User>` |

- **controller**: wraps what the service returned with `ApiResponse.ok(...)`. That is usually the whole method: `return ApiResponse.ok(userService.getUser(id));`
- **repository**: a single lookup always returns `Optional<Entity>`, never a bare `Entity`, because the row may not exist. Many rows return `List<Entity>`.

## Running Locally

```bash
cd backend
./gradlew bootRun     # starts the server at http://localhost:8080
./gradlew test        # runs all tests
```

Requires JDK 21.
