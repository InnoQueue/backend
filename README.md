# Software Quality and Reliability S23 course

> Semester 8, 4th study-year, Innopolis University.

![Build workflow](https://github.com/InnoQueue/backend/actions/workflows/build.yml/badge.svg)
![MD Linter workflow](https://github.com/InnoQueue/backend/actions/workflows/mdlinter.yml/badge.svg)
![Dcoker publish workflow](https://github.com/InnoQueue/backend/actions/workflows/docker_publish.yml/badge.svg)
![Dev deploy workflow](https://github.com/InnoQueue/backend/actions/workflows/heroku_dev.yml/badge.svg)
![Prod deploy workflow](https://github.com/InnoQueue/backend/actions/workflows/heroku_prod.yml/badge.svg)
![Code coverage workflow](https://github.com/InnoQueue/backend/actions/workflows/codecov.yml/badge.svg)
[![codecov](https://codecov.io/gh/InnoQueue/backend/branch/main/graph/badge.svg?token=9RQU24PHSX)](https://codecov.io/gh/InnoQueue/backend)
[![Hits-of-Code](https://hitsofcode.com/github/InnoQueue/backend?branch=main)](https://hitsofcode.com/github/InnoQueue/backend/view?branch=main)
![Alt](https://repobeats.axiom.co/api/embed/83cc35353a9b2635ff57f87dc01ad35ed9d48e32.svg "Repobeats analytics image")

## 📖 Contents

- [About](#-about)
- [How to build](#how-to-build)
- [Ensuring Quality](#ensuring-quality)
- [Deployment](#deployment)
- [Related repositories](#-related-repositories)

![Logo](https://user-images.githubusercontent.com/49106163/200147786-3e414c4a-d6ca-4240-b00e-1ef35fac6308.png)

## InnoQueue: Backend

When you live with 5 other people, you have to manage your household chores.
This could mean taking trash every once in a while or washing dishes.
That said, there is a problem of bearing in mind all those different orders and chores.
And then somebody may take a rain check here and there and you have no idea who's doing what
and there is no way to tell except for boring Excel sheets, but it requires a lot of time and dedication to set up and
maintain a spreadsheet.

So, we present to you the application that can do the heavy lifting of maintaining and automating
all of these nuances for you!

This is how it works: users complete tasks in a queue one by one in a loop.
So, ideally, every roommate should complete a task on each iteration.
You can also have multiple queues go in parallel and there's no confusion!

[I want to know more!](https://github.com/InnoQueue/.github/blob/main/profile/README.md)

## 👨🏻‍💻 Team

- [Roman Soldatov](https://github.com/SMore-Napi)
- [Daniil Livitin](https://github.com/Dablup)
- [Mikhail Martovitsky](https://github.com/MikhailMarch)
- [Emil Khabibullin](https://github.com/emileyray)
- [Timur Nugaev](https://github.com/al1ych)

![Contributors](https://contrib.rocks/image?repo=InnoQueue/backend)

## 📌 About

- For the mobile application the Backend REST API was developed.
- Here is the REST API in [Swagger](https://innoqueue-dev.herokuapp.com/swagger-ui.html)
- [Javadoc documentation](https://innoqueue.github.io/backend/)
- [SonarQube analysis](https://sonarcloud.io/summary/overall?id=InnoQueue_backend)

## 🔨 How to build

- `git clone https://github.com/InnoQueue/backend.git`
- `cd backend`
- There are two ways to run the server: **Gradle run** and **Docker compose**

### Gradle run

- Install [JDK](https://www.oracle.com/java/technologies/downloads/)
- Install [Gradle](https://gradle.org/install/)
- Install [Docker](https://docs.docker.com/engine/install/)

#### Set up the database

- `docker pull postgres`
- `docker run --name postgres -e POSTGRES_PASSWORD=postgres -d -p 5432:5432 postgres`
  > Note that you can set your own container name and password. Just make sure that
  > in [application.yml](/src/main/resources/application.yml) file
  > **spring.datasource.username** and **spring.datasource.password** properties have
  > the same values as your container.

#### Firebase

- Additionally, if you want to send push notifications, set up the firebase project:
- Create your project using the [Firebase](https://console.firebase.google.com)
- Go to **Project settings** -> **Service accounts**.
- In the **Admin SDK configuration snippet section** select `Java`
  and click on **Generate new private key**.
- The JSON file's content should be similar to
  [innoqueue-firebase.json](/src/main/resources/innoqueue-firebase.json.origin)
- Provide this JSON file for `GOOGLE_CREDENTIALS` credentials in `application.yml`
- Run application:

```bash
GOOGLE_CREDENTIALS=`cat firebase.json` ./gradlew bootRun
```

where **firebase.json** is your json credentials file.

#### Deep links

- To support Deep links on Android, you need to proved assertlinks.json file
- This file can be generated [here](https://developers.google.com/digital-asset-links/tools/generator)
- Provide file content for `ASSET_LINKS` credentials in `application.yml`
- Run application:

```bash
ASSET_LINKS=`cat assertlinks.json` ./gradlew bootRun
```

### Docker compose up

- Install [Docker](https://docs.docker.com/engine/install/)
- Run: `docker-compose up`

## 🏃🏻‍♂️ How to run

- Run application: `./gradlew bootRun`

## 👍🏻 Ensuring Quality

> Features on which we are working can be found in [issues](https://github.com/InnoQueue/backend/issues)

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
- All build failures are published in [our Telegram chat](https://t.me/+nkVX0j3FXo8zMmNi)
- All build successes are also published in [our Telegram chat](https://t.me/+nkVX0j3FXo8zMmNi)
- The backend image is also published in Docker Hub. You can pull it:
  `docker pull smorenapi/inno_queue:latest`

### Database migrations

- We use **flyway** to track database changes.
- Migration files are placed in [migration](src/main/resources/db/migration) folder.

## 🚀 Deployment

- The backend is hosted for production on Heroku [here](https://innoqueue.herokuapp.com)
- Also, we host for dev purposes [here](https://innoqueue-dev.herokuapp.com).
  We use it to test new features with the test database.
- Deployment is possible only when the Build workflow is successful.
- If build fails or succeeds, our team receives information in our Telegram chat through our bot.
- `innoqueue-dev` is automatically deployed when pushed on the `main` branch.
- `innoqueue-dev` can also be manually deployed from any branch.
  Run the `Deploy DEV` workflow.
- `innoqueue` is deployed manually from main branch.

## 📚 Contribution

If you have any ideas on how to imporove the quality of our product or of the quality ensurance
for our project, we welcome your enthusiasm and here's how you can contribute!

In short:

- Fork this repo
- Create a new issue in our repo, where you describe what contribution you want to make thoroughly
- Assign the correct labels to the issue [*]
- Create a new branch using appropriate naming conventions [**]
- Make the necessary changes
- Push the changes to your repo
- Make a pull request from your repo on your branch to our repo's main branch
- Don't forget to add a comprehensive description so we understand better what you did
- We're going to review your changes and decide on whether to accept your code [***]

[*] Issue labels:

- `routine` is for routine tasks like quality ensurance. the code under this tag should
  not add any new functionality application-wise. for example: `rework database schemas`
- `feature` is for tasks that add new functionality, features, etc. for example: `add pagination`
- `bug` is for bugfixes.

> Note: we do not accept any other labels for contribution issues

[**] Naming conventions:

- If it's a feature you're adding, name your branch `feature/task-title`
- If it's a routine task, name your branch `routine/task-title`
- If it's a bug fix, name your branch `fix/task-title`

[***] Code review may take up to 2 working days

## 📊 Related repositories

- README about this project [in this repository](https://github.com/InnoQueue/.github/blob/main/profile/README.md)
- The Mobile application is [in this repository](https://github.com/InnoQueue/Mobile)
