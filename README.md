# Ensuring Quality in Software Projects F22 elective

> Semester 7, 4th study-year, Innopolis University.

[![Hits-of-Code](https://hitsofcode.com/github/InnoQueue/Backend?branch=main)](https://hitsofcode.com/github/InnoQueue/Backend/view?branch=main)

REST API Backend for the **InnoQueue**.
To read the full description, check [README repository](https://github.com/InnoQueue/.github/blob/main/profile/README.md)

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
- You can read [API documentation](https://documenter.getpostman.com/view/16213957/UVsSP4ER)
- In case you want to reset the database by default mock data, use
  this [endpoint](https://innoqueue.herokuapp.com/reset)
- The backend is hosted on [Heroku](https://innoqueue.herokuapp.com)

## How to build

### Manually

- TODO

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
