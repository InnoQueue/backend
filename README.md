# Ensuring Quality in Software Projects F22 elective

> Semester 7, 4th study-year, Innopolis University.

[![Hits-of-Code](https://hitsofcode.com/github/InnoQueue/Backend?branch=main)](https://hitsofcode.com/github/InnoQueue/Backend/view?branch=main)

REST API Backend for the **InnoQueue**.
To read the full description,
check [README repository](https://github.com/InnoQueue/.github/blob/main/profile/README.md)

## **Team**

- Roman Soldatov
- Timur Nugaev
- Daniil Livitin
- Mikhail Martovitsky

## Contents

- [About](#-about)
- [How to build](#how-to-build)
- [Ensuring Quality](#ensuring-quality)
- [Other repositories](#-other-repositories)

## 📌 About

- For the mobile application the Backend REST API was developed.
- You can read [Postman API documentation](https://documenter.getpostman.com/view/16213957/UVsSP4ER)
- You can read [Swagger](https://innoqueue.herokuapp.com/swagger-ui.html)
- In case you want to reset the database by default mock data, use
  this [endpoint](https://innoqueue.herokuapp.com/reset)
- The backend is hosted on [Heroku](https://innoqueue.herokuapp.com)

## How to build

### Manually

- Install [JDK](https://www.oracle.com/java/technologies/downloads/)
- Install [Gradle](https://gradle.org/install/)
- Install [Docker](https://docs.docker.com/engine/install/)
- Open your terminal and run the following commands:

#### Set up the database

- `docker pull postgres`
- `docker run --name postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres`
  > Note that you can set your own container name and password. Just make sure that
  in [application.yml](/src/main/resources/application.yml) file
  **spring.datasource.username** and **spring.datasource.password** properties have
  the same values as your container.

#### Run

- `git clone https://github.com/InnoQueue/Backend.git`
- `cd Backend`
- Run application: `./gradlew bootRun`

#### Firebase

- Additionally, if you want to send push notifications, set up the firebase project:
- Create your project using the [Firebase](https://console.firebase.google.com)
- Go to **Project settings** -> **Service accounts**.
- In the **Admin SDK configuration snippet section** select `Java`
  and click on **Generate new private key**.
- The JSON file's content should be similar to
  [innoqueue-firebase.json](/src/main/resources/innoqueue-firebase.json.origin)
- Place this JSON file in **Backend** folder.
- Run application:

```bash
GOOGLE_CREDENTIALS=`cat firebase.json` ./gradlew bootRun
```

where **firebase.json** is your json credentials file.

### Docker

- TODO

## Ensuring Quality

### Hooks

- GitHub Actions will check the branch and commit message name policy.
  However, you can check it before pushing the code using the **git hooks**
- Make files in `.githooks` executable:
  - `chmod 755 commit-msg`
  - `chmod 755 prepare-commit-msg`
- In order to enable hooks enter the project and use the following command: `git config core.hooksPath .githooks`
- Hooks checks:
  - branch name: `regex = r"^[A-Z]{1,9}_[0-9]{1,9}"`
  - commit message: `regex = r"(Add |Created |Fix |Update |Rework)(.+)(?:closes #[1-9])|Minor"`

### GitHub Actions

- Runs Markdown linter to check .md files
- Runs `Gradle build` to run
  - Code linter and checkstyle `detekt`
  - Runs source files compilation to check possible errors
  - Runs unit and integration tests
  - Build an application
- All build failures are published in [Telegram chat](https://t.me/+nkVX0j3FXo8zMmNi)

> Features on which we are working can be found in [issues](https://github.com/InnoQueue/Backend/issues)

## 📊 Other repositories

- README about this project [in this repository](https://github.com/InnoQueue/README)
- The Mobile application is [in this repository](https://github.com/InnoQueue/Mobile)
