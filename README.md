# API - Plateforme assocative

## Getting Started
### Prerequisites
- [Java](https://java.com/): 1.8
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

## Rules
- Use IntelliJ IDEA.
- Code needs to be written in english. (documentation included)
- Document all your changes before MR.
- Create a new branch for each issue
- Create a new branch on issue's branch for each feature
