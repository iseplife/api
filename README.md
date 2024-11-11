# API - Plateforme assocative

## Getting Started
### Prerequisites
- [Java](https://java.com/): 11.0.6 LTS
- [Maven](https://maven.apache.org/): 3.5.4

### Installation

1. Download and setup iseplife backend project
```bash
# Clone the repository (stable branch)
git clone -b master https://github.com/iseplife/api api

# Go to the project root
cd api

# Install dependencies
mvn install
```

2. Create your database.
```bash
# Create your local dev one using docker
docker compose -f docker-compose.dev.yml up -d
```

3. Enable the `unaccent` extension in your database

Execute this in your database using your favorite SQL client (for instance psql using `psql -U iseplife_dev -h localhost -p 3333`).
```sql
CREATE EXTENSION unaccent;
```

4. Create your `.env` file. 
```bash
# Copy the example one to get started
cp example.env .env
```

### Contribute to project

Before contributing to the project be sure that you have read the [Contributor guidelines](CONTRIBUTING.md).
