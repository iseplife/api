# API - Plateforme assocative

## Getting Started
### Prerequisites
- [Java](https://java.com/): 11.0.6 LTS
- [Maven](https://maven.apache.org/): 3.5.4

### Installation
```bash
# Clone the repository (stable branch)
git clone -b master https://git.dev.juniorisep.com/iseplife/api api

# Go to the project root
cd api

# Install dependencies
mvn install

# Start application through CLI
mvn spring-boot:run
```

### Build - Production
This springboot app uses the plugin `spring-boot-maven-plugin` to create an executable jar file.
It can then be used to create a service.
```bash
#Build the springboot app with the prod profile
mvn package -Dspring.profiles.active=prod -DskipTests

```

### Contribute to project

Before contributing to the project be sure that you have read the [Contributor guidelines](CONTRIBUTING.md).
